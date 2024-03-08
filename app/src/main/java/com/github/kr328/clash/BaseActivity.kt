package com.github.kr328.clash

import android.app.AlertDialog
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import com.github.kr328.clash.common.compat.isAllowForceDarkCompat
import com.github.kr328.clash.common.compat.isLightNavigationBarCompat
import com.github.kr328.clash.common.compat.isLightStatusBarsCompat
import com.github.kr328.clash.common.compat.isSystemBarsTranslucentCompat
import com.github.kr328.clash.core.bridge.ClashException
import com.github.kr328.clash.design.Design
import com.github.kr328.clash.design.model.DarkMode
import com.github.kr328.clash.design.store.UiStore
import com.github.kr328.clash.design.ui.DayNight
import com.github.kr328.clash.design.util.resolveThemedBoolean
import com.github.kr328.clash.design.util.resolveThemedColor
import com.github.kr328.clash.design.util.showExceptionToast
import com.github.kr328.clash.remote.Broadcasts
import com.github.kr328.clash.remote.Remote
import com.github.kr328.clash.util.ActivityResultLifecycle
import com.github.kr328.clash.util.ApplicationObserver
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

abstract class BaseActivity<D : Design<*>> :
    AppCompatActivity(),
    CoroutineScope by MainScope(),
    Broadcasts.Observer {
    enum class Event {
        ServiceRecreated,
        ActivityStart,
        ActivityStop,
        ClashStop,
        ClashStart,
        ProfileLoaded,
        ProfileChanged,
        ProfileUpdateCompleted,
        ProfileUpdateFailed,
        LoginSuccess,
        UserInfoChanged
    }


    protected val uiStore by lazy { UiStore(this) }
    protected val events = Channel<Event>(Channel.UNLIMITED)
    protected var activityStarted: Boolean = false
    protected val clashRunning: Boolean
        get() = Remote.broadcasts.clashRunning
    protected var design: D? = null
        private set(value) {
            field = value

            if (value != null) {
                setContentView(value.root)
                value.initDesign()
            } else {
                setContentView(View(this))
            }
        }

    private var defer: suspend () -> Unit = {}
    private var deferRunning = false
    private val nextRequestKey = AtomicInteger(0)
    private var dayNight: DayNight = DayNight.Day

    protected abstract suspend fun main()

    fun defer(operation: suspend () -> Unit) {
        this.defer = operation
    }

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

    suspend fun setContentDesign(design: D) {
        suspendCoroutine<Unit> {
            window.decorView.post {
                this.design = design

                it.resume(Unit)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setWindowStyle()
        launch {
            main()

            finish()
        }
    }

    override fun onStart() {
        super.onStart()

        activityStarted = true

        Remote.broadcasts.addObserver(this)

        events.trySend(Event.ActivityStart)
    }

    override fun onStop() {
        super.onStop()

        activityStarted = false

        Remote.broadcasts.removeObserver(this)

        events.trySend(Event.ActivityStop)
    }

    override fun onDestroy() {
        design?.cancel()

        cancel()

        super.onDestroy()
    }

    override fun finish() {
        if (deferRunning) {
            return
        }

        deferRunning = true

        launch {
            try {
                defer()
            } finally {
                withContext(NonCancellable) {
                    super.finish()
                }
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

    }

    open fun shouldDisplayHomeAsUpEnabled(): Boolean {
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        this.onBackPressed()

        return true
    }

    override fun onBackPressed() {
        if (overrideBackPress()) {
            return;
        }
        super.onBackPressed()
    }

    open fun overrideBackPress(): Boolean{
        return false
    }

    override fun onProfileChanged() {
        events.trySend(Event.ProfileChanged)
    }

    override fun onProfileUpdateCompleted(uuid: UUID?) {
        events.trySend(Event.ProfileUpdateCompleted)
    }

    override fun onProfileUpdateFailed(uuid: UUID?, reason: String?) {
        events.trySend(Event.ProfileUpdateFailed)
    }

    override fun onProfileLoaded() {
        events.trySend(Event.ProfileLoaded)
    }

    override fun onServiceRecreated() {
        events.trySend(Event.ServiceRecreated)
    }

    override fun onStarted() {
        events.trySend(Event.ClashStart)
    }

    override fun onStopped(cause: String?) {
        events.trySend(Event.ClashStop)

        if (cause != null && activityStarted) {
            launch {
                design?.showExceptionToast(ClashException(cause))
            }
        }
    }

    override fun onLogin() {
        events.trySend(Event.LoginSuccess)
    }

    override fun onUserInfo() {
        events.trySend(Event.UserInfoChanged)
    }

    private fun setWindowStyle() {
        window.decorView
            .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setBackgroundDrawable(getDrawable(R.drawable.bg_b))
    }

    private var loadingProgressBar: AlertDialog? = null
    fun showLoading() {
        if (loadingProgressBar == null) {
            val builder = AlertDialog.Builder(this)
            builder.setView(R.layout.view_loading_dialog)
            loadingProgressBar = builder.create()
        }
        loadingProgressBar?.show()
    }

    fun hideLoading() {
        loadingProgressBar?.let {
            try {
                loadingProgressBar?.dismiss()
            }catch (e: Exception) {

            }
        }
    }

}
