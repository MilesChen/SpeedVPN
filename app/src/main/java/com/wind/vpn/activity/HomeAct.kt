package com.wind.vpn.activity

import android.content.Intent
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.github.kr328.clash.BaseActivity
import com.github.kr328.clash.ProfilesActivity
import com.github.kr328.clash.R
import com.github.kr328.clash.common.constants.Intents
import com.github.kr328.clash.common.util.intent
import com.github.kr328.clash.common.util.ticker
import com.github.kr328.clash.design.MainDesign
import com.github.kr328.clash.design.ui.ToastDuration
import com.github.kr328.clash.service.util.sendBroadcastSelf
import com.github.kr328.clash.util.startClashService
import com.github.kr328.clash.util.stopClashService
import com.github.kr328.clash.util.withClash
import com.github.kr328.clash.util.withProfile
import com.wind.vpn.WindGlobal
import com.wind.vpn.bean.NET_ERR
import com.wind.vpn.data.WindApi
import com.wind.vpn.data.account.GUEST_SUFFIX
import com.wind.vpn.data.account.WindAccount
import com.wind.vpn.design.HomeDesign
import com.wind.vpn.util.goTargetClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class HomeAct : BaseActivity<HomeDesign>() {
    private var firstResume = true
    override suspend fun main() {
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
                            Log.d("chenchao", "events.onReceive $it and start fetch")
                            loadUserInfo()
                            design.fetch()
                        }

                        else -> Unit
                    }
                }
                design.requests.onReceive {
                    Log.d("chenchao", "on receive $it")
                    when (it) {
                        HomeDesign.Request.ToggleStatus -> {
                            if (clashRunning)
                                stopClashService()
                            else
                                design.startClash()
                        }
                        HomeDesign.Request.OpenCharge -> {
                            goRenew()
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

    private suspend fun HomeDesign.startClash() {
        val active = withProfile { queryActive() }

        if (active == null || !active.imported) {
            if (WindGlobal.account.isLogin()) {
                if (WindGlobal.account.isGuestAccount) {
                    showToast(getString(R.string.toast_expired))
                    goTargetClass(this@HomeAct, RegisterActivity::class.java)
                } else if(WindGlobal.userInfo.expired_at < System.currentTimeMillis()) {
                    showToast(getString(R.string.toast_plan_expired))
                    goRenew()
                }
            } else {
                goTargetClass(this@HomeAct, RegisterActivity::class.java)
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
            design?.showToast(R.string.unable_to_start_vpn, ToastDuration.Long)
        }
    }

    private suspend fun HomeDesign.fetch() {
        setClashRunning(clashRunning)
        val state = withClash {
            queryTunnelState()
        }
        val providers = withClash {
            queryProviders()
        }
        withProfile {
//            setProfileName(queryActive()?.name)
        }
        updateUI()
    }

    override fun overrideBackPress(): Boolean {
        return design?.overrideBackPress() == true
    }

    private fun loadUserInfo() {
        if (!WindGlobal.account.isLogin()) return
        WindGlobal.loadUserInfoFromServer()
    }

    override fun onResume() {
        super.onResume()
        if (firstResume && !WindGlobal.account.isLogin()) {
            firstResume = false
            tryRegisterGuest()
        }
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
}