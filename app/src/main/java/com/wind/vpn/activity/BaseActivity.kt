package com.wind.vpn.activity

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.github.kr328.clash.R
import com.github.kr328.clash.design.ui.ToastDuration
import com.github.kr328.clash.remote.Remote
import com.github.kr328.clash.util.ActivityResultLifecycle
import com.google.android.material.snackbar.Snackbar
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
        window.getDecorView()
            .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setBackgroundDrawable(getDrawable(R.drawable.bg_b))
        setContentView(getLayoutResId())
        var topBar: TopBar = findViewById(R.id.top_bar_view)
        topBar.setTopBarListener{onTopBarIconClick()}
        topBar.setIcon(getToBarIcon())
        topBar.setTitle(getTopTitle())
        initView()
    }

    open fun initView() {

    }

    abstract fun getLayoutResId(): Int

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

    protected fun showLoading() {
        if (loadingProgressBar == null) {
            val builder = AlertDialog.Builder(this)
            builder.setView(R.layout.view_loading_dialog)
            loadingProgressBar = builder.create()
        }
        loadingProgressBar?.show()
    }

    protected fun hideLoading() {
        loadingProgressBar?.let {
            try {
                loadingProgressBar?.dismiss()
            }catch (e: Exception) {

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