package com.wind.vpn.activity

import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Debug
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.github.kr328.clash.BaseActivity
import com.github.kr328.clash.BuildConfig
import com.github.kr328.clash.R
import com.github.kr328.clash.common.log.Log
import com.github.kr328.clash.common.util.ticker
import com.github.kr328.clash.databinding.ViewForbiddenDialogBinding
import com.github.kr328.clash.databinding.ViewRenewalDialogBinding
import com.github.kr328.clash.design.ui.ToastDuration
import com.github.kr328.clash.util.startClashService
import com.github.kr328.clash.util.stopClashService
import com.github.kr328.clash.util.withClash
import com.github.kr328.clash.util.withProfile
import com.google.android.play.core.review.ReviewException
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.model.ReviewErrorCode
import com.google.android.play.core.review.testing.FakeReviewManager
import com.wind.vpn.WindGlobal
import com.wind.vpn.bean.NET_ERR
import com.wind.vpn.data.WindApi
import com.wind.vpn.data.account.GUEST_SUFFIX
import com.wind.vpn.data.account.WindAccount
import com.wind.vpn.design.HomeDesign
import com.wind.vpn.widget.CommDialog
import im.crisp.client.Crisp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.concurrent.TimeUnit


class HomeAct : BaseActivity<HomeDesign>() {
    private var firstResume = true
    private var profileState = 0
    override suspend fun main() {
        Crisp.configure(this, "3a504d1e-a777-482d-abf1-fa378b30ad2c")
        val design = HomeDesign(this)
        setContentDesign(design)
        val ticker = ticker(TimeUnit.SECONDS.toMillis(1))
        while (isActive) {
            select<Unit> {
                events.onReceive {
                    when (it) {
                        Event.ServiceRecreated,
                        Event.ClashStop, Event.ClashStart,
                        Event.ProfileLoaded, Event.ProfileChanged, Event.UserInfoChanged -> design.fetch()

                        Event.LoginSuccess -> loadUserInfo()
                        Event.ActivityStart -> {
                            loadUserInfo()
                            design.fetch()
                        }

                        Event.HasNewVersion -> {
                            showNewDialog()
                        }

                        Event.OssUpdate -> {
                            if (!WindGlobal.account.isLogin()) {
                                tryRegisterGuest()
                            }
                        }
                        Event.ProfileUpdateFailed -> {
                            showErrorDialog()
                        }

                        else -> Unit
                    }
                }
                design.requests.onReceive {
                    when (it) {
                        HomeDesign.Request.ToggleStatus -> {
                            if (clashRunning)
                                stopClashService()
                            else
                                design.startClash()
                        }

                        HomeDesign.Request.OpenCharge -> {
                            showReviewInfo()
//                            goRenew()
                        }
                    }
                }
                if (clashRunning) {
                    ticker.onReceive {
                        fetchTraffic()
                    }
                }
            }

        }
    }

    private suspend fun fetchTraffic() {
        withClash {
            queryTrafficTotal()
        }
    }

    private fun showNewDialog() {
        try {
            var dialog: AlertDialog? = null
            dialog = AlertDialog.Builder(this).apply {
                setTitle(R.string.dialog_title_new_version)
                setMessage(R.string.dialog_message_new_version)
                setNegativeButton(
                    R.string.dialog_cancel_new_version
                ) { _, _ ->
                    uiStore.lastPromptTime = System.currentTimeMillis()
                    dialog!!.dismiss()
                }
                setPositiveButton(
                    R.string.dialog_confirm_new_version
                ) { _, _ ->
                    run {
                        uiStore.lastPromptTime = System.currentTimeMillis()
                        dialog!!.dismiss()
                        startLunchInstall()
                    }
                }
            }.create().apply {
                show()
                getButton(AlertDialog.BUTTON_POSITIVE).isAllCaps = false
                getButton(AlertDialog.BUTTON_NEGATIVE).isAllCaps = false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun showRenewDialog() {
        val dialog = Dialog(this, R.style.RenewalDialog).apply {
            setCancelable(false)
            setCanceledOnTouchOutside(false)
        }
        val viewBinding = ViewRenewalDialogBinding.inflate(layoutInflater, findViewById(android.R.id.content), false)
        dialog.setContentView(viewBinding.root)
        if (WindGlobal.account.isLogin() && WindGlobal.account.isGuestAccount) {
            viewBinding.tvDialogContent.setText(R.string.renewal_dialog_content_guest)
        } else {
            viewBinding.tvDialogContent.setText(R.string.renewal_dialog_content_user)
        }
        dialog.show()
        viewBinding.btnRenewal.setOnClickListener {
            dialog.dismiss()
            goRenew()
        }

        viewBinding.ivClose.setOnClickListener {
            dialog.dismiss()
        }

    }
     private var forbiddenDialog: Dialog? = null
    private fun showForbiddenDialog() {
        forbiddenDialog?.let {
            if (it.isShowing) {
                return
            }
        }
        if (forbiddenDialog == null) {
            forbiddenDialog = Dialog(this, R.style.RenewalDialog).apply {
                setCancelable(false)
                setCanceledOnTouchOutside(false)
                val viewBinding = ViewForbiddenDialogBinding.inflate(layoutInflater, findViewById(android.R.id.content), false)
                setContentView(viewBinding.root)
            }

        }
        forbiddenDialog!!.show()

    }

    private fun showErrorDialog() {
        launch(Dispatchers.IO) {
            val tempActive = withProfile { queryActive() }
            if (tempActive?.imported == false) {
                profileState = -1
                withContext(Dispatchers.Main) {
                    val dialog = CommDialog(this@HomeAct, R.style.RenewalDialog).apply {
                        setMessage(getString(R.string.profile_load_err))
                        setButton(getString(R.string.retry)) {
                            loadUserInfo()
                        }
                    }
                    dialog.show()
                }
            }
        }


    }

    private fun startLunchInstall() {
        val installIntent = Intent()
        installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        installIntent.setAction(Intent.ACTION_VIEW)
        val apkFile = WindGlobal.upgradeManager.getApkFile()
        val apkFileUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(
                this,
                BuildConfig.APPLICATION_ID + ".provider",
                apkFile
            )
        } else {
            Uri.fromFile(apkFile)
        }
        installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        installIntent.setDataAndType(apkFileUri, "application/vnd.android.package-archive")
        try {
            startActivity(installIntent)
        } catch (e: ActivityNotFoundException) {
        }
    }

    private suspend fun HomeDesign.startClash() {
//        if (true) {
//            showReviewInfo()
//            return
//        }
        val active = withProfile { queryActive() }
        if (WindGlobal.userInfo.expired_at * 1000L < System.currentTimeMillis()){
            showRenewDialog()
            return;
        }
        if (active == null || !active.imported) {
            showLoading(getString(R.string.init_msg))
            var retryCount = 0;
            profileState = 0
            launch(Dispatchers.IO) {
                while (retryCount < 10 && profileState != -1) {
                    delay(1000)
                    retryCount++
                    val tempActive = withProfile { queryActive() }
                    Log.d("tempActive: $tempActive imported ${tempActive?.imported}")
                    if (tempActive?.imported == true) {
                        withContext(Dispatchers.Main){
                            hideLoading()
                        }
                        startClash()
                        break
                    }
                }
                withContext(Dispatchers.Main){
                    hideLoading()
                }
            }
            return
        }

        val vpnRequest = startClashService()

        try {
            if (vpnRequest != null) {
                val result = startActivityForResult(
                    ActivityResultContracts.StartActivityForResult(),
                    vpnRequest
                )

                if (result.resultCode == RESULT_OK)
                    startClashService()
            }
        } catch (e: Exception) {
            design?.showToast(com.github.kr328.clash.design.R.string.unable_to_start_vpn, ToastDuration.Long)
        }
    }

    private suspend fun HomeDesign.fetch() {
        var local:Locale? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            local = resources.configuration.locales.get(0)
        } else {
            local = resources.configuration.locale
        }
        local?.let {
            val lc = "${it.language}-${it.country}"
            if (BuildConfig.FORBIDDEN.contains(lc)) {
                showForbiddenDialog()
                return
            }
        }
        setClashRunning(clashRunning)
//        val state = withClash {
//            queryTunnelState()
//        }
//        val providers = withClash {
//            queryProviders()
//        }
//        withProfile {
//            setProfileName(queryActive()?.name)
//        }
        updateUI()
    }

    override fun overrideBackPress(): Boolean {
        return design?.overrideBackPress() == true
    }

    private fun loadUserInfo() {
        if (!WindGlobal.account.isLogin()) return
        if (clashRunning) {
            return;
        }
        WindGlobal.loadUserInfoFromServer()
    }

    override fun onResume() {
        super.onResume()
        if (firstResume && !WindGlobal.account.isLogin()) {
            firstResume = false
            tryRegisterGuest()
        }
        WindGlobal.upgradeManager.shouldPromptNewVersion()
    }

    private fun tryRegisterGuest() {
        showLoading()
        launch(Dispatchers.IO) {
            val email = "${WindGlobal.androidid}$GUEST_SUFFIX"
//            val email = "648672865$GUEST_SUFFIX"
            val pwd = WindGlobal.androidid
//            val email = "648672865@qq.com"
//            val pwd = "111111111"
            var loginResult = WindApi.register(email, pwd)
            if (!loginResult.isSuccess && loginResult.retCode != NET_ERR) {
                loginResult = WindApi.login(email, pwd)
            }
            withContext(Dispatchers.Main) {
                if (loginResult.isSuccess && loginResult.data != null) {
                    val account = WindAccount()
                    account.token = loginResult.data!!.token
                    account.email = email
                    account.pwd = pwd
                    account.auth_data = loginResult.data!!.auth_data
                    WindGlobal.account = account
                } else if (loginResult.httpCode == 422) {
                    showToast(getString(R.string.toast_pwd_err))
                } else {
                    showToast(getString(R.string.toast_error))
                }
                hideLoading()
            }
        }
    }

    private fun showReviewInfo() {
        val manager = FakeReviewManager(this)
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // We got the ReviewInfo object
                val reviewInfo = task.result
                val flow = manager.launchReviewFlow(this, reviewInfo)
                flow.addOnCompleteListener { _ ->
                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown. Thus, no
                    // matter the result, we continue our app flow.
                }
            }
        }
    }
}