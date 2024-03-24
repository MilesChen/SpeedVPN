package com.wind.vpn.activity

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.os.Debug
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.github.kr328.clash.R
import com.github.kr328.clash.design.ui.ToastDuration
import com.github.kr328.clash.remote.Remote
import com.github.kr328.clash.util.ActivityResultLifecycle
import com.google.android.material.snackbar.Snackbar
import com.wind.vpn.WindGlobal
import com.wind.vpn.util.goTargetClass
import com.wind.vpn.widget.TopBar
import com.wind.vpn.widget.TopBarListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.suspendCoroutine

open abstract class BaseActivity : AppCompatActivity(), TopBarListener, CoroutineScope by MainScope() {
    private val nextRequestKey = AtomicInteger(0)
    private var loadingProgressBar: AlertDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        window.getDecorView()
            .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setBackgroundDrawable(getDrawable(com.github.kr328.clash.design.R.drawable.bg_b))
        if (getLayoutResId() != 0) {
            setContentView(getLayoutResId())
        } else {
            setContentView(genCustomView())
        }
        var topBar: TopBar = findViewById(R.id.top_bar_view)
        topBar.setTopBarListener{onTopBarIconClick()}
        topBar.setIcon(getToBarIcon())
        topBar.setTitle(getTopTitle())
        initView()
    }

    open fun initView() {

    }

    open fun genCustomView():View {
        return View(this)
    }

    open fun getLayoutResId(): Int {
        return 0
    }

    open fun getToBarIcon(): Int {
        return R.drawable.icon_title_back
    }

    abstract fun getTopTitle(): Int

    open fun onTopBarIconClick() {
        finish()
    }

    override fun onIconClick() {
        onTopBarIconClick()
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }

    fun showLoading() {
        if (loadingProgressBar == null) {
            val builder = AlertDialog.Builder(this)
            builder.setCancelable(false)
            builder.setView(R.layout.view_loading_dialog)
            loadingProgressBar = builder.create()
            loadingProgressBar!!.setCanceledOnTouchOutside(false)
        }
        loadingProgressBar?.show()
    }

    fun hideLoading() {
        loadingProgressBar?.let {
            try {
                loadingProgressBar?.dismiss()
            }catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    protected val clashRunning: Boolean
        get() = Remote.broadcasts.clashRunning

    suspend fun <I, O> startActivityForResult(
        contracts: ActivityResultContract<I, O>,
        input: I
    ): O = withContext(Dispatchers.Main) {
        val requestKey = nextRequestKey.getAndIncrement().toString()

        ActivityResultLifecycle().use { lifecycle, start ->
            suspendCoroutine { c ->
                activityResultRegistry.register(requestKey, lifecycle, contracts) {
                    c.resumeWith(Result.success(it))
                }.apply { start() }.launch(input)
            }
        }
    }

}

fun Context.showToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
}

fun Context.goRenew() {
    if (WindGlobal.account.isLogin() && !WindGlobal.account.isGuestAccount) {
        goTargetClass(this, RechargeActivity::class.java)
    } else {
        val bundle = Bundle()
        bundle.putBoolean(KEY_GO_RENEW, true)
        goTargetClass(this, RegisterActivity::class.java, bundle)
    }
}