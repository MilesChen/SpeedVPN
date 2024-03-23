package com.github.kr328.clash.util

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Process
import androidx.core.content.ContextCompat
import com.wind.vpn.activity.BrowserActivity
import com.wind.vpn.activity.KEY_TARGET_URL
import com.wind.vpn.util.goTargetClass
import im.crisp.client.ChatActivity
import java.io.File
import java.util.zip.ZipFile


object ApplicationObserver {
    private val _createdActivities: MutableSet<Activity> = mutableSetOf()
    private val _visibleActivities: MutableSet<Activity> = mutableSetOf()

    private var visibleChanged: (Boolean) -> Unit = {}

    private var appVisible = false
        private set(value) {
            if (field != value) {
                field = value

                visibleChanged(value)
            }
        }

    val createdActivities: Set<Activity>
        get() = _createdActivities

    private val activityObserver = object : Application.ActivityLifecycleCallbacks {
        @Synchronized
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            _createdActivities.add(activity)
        }

        @Synchronized
        override fun onActivityDestroyed(activity: Activity) {
            _createdActivities.remove(activity)
            _visibleActivities.remove(activity)
            appVisible = _visibleActivities.isNotEmpty()
        }

        override fun onActivityStarted(activity: Activity) {
            _visibleActivities.add(activity)
            appVisible = true
        }

        override fun onActivityStopped(activity: Activity) {
            _visibleActivities.remove(activity)
            appVisible = _visibleActivities.isNotEmpty()
        }

        override fun onActivityPaused(activity: Activity) {}
        override fun onActivityResumed(activity: Activity) {}
        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    }

    fun onVisibleChanged(visibleChanged: (Boolean) -> Unit) {
        this.visibleChanged = visibleChanged
    }

    fun attach(application: Application) {
        application.registerActivityLifecycleCallbacks(activityObserver)
    }
}

fun Context.verifyApk(): Boolean {
    return try {
        val info = applicationInfo
        val sources = info.splitSourceDirs ?: arrayOf(info.sourceDir) ?: return false

        val regexNativeLibrary = Regex("lib/(\\S+)/libclash.so")
        val availableAbi = Build.SUPPORTED_ABIS.toSet()
        val apkAbi = sources
            .asSequence()
            .filter { File(it).exists() }
            .flatMap { ZipFile(it).entries().asSequence() }
            .mapNotNull { regexNativeLibrary.matchEntire(it.name) }
            .mapNotNull { it.groups[1]?.value }
            .toSet()

        availableAbi.intersect(apkAbi).isNotEmpty()
    } catch (e: Exception) {
        false
    }
}

fun Context.isMainProgress():Boolean {
    val pid = Process.myPid()
    val ams: ActivityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    ams.runningAppProcesses ?: return false
    val progressInfos = ams.runningAppProcesses
    for (item in progressInfos) {
        if (item.pid == pid && item.processName == packageName) {
            return true
        }
    }
    return false

}

fun Context.startLoadUrl(url:String) {
//    openInnerBrowser(url)
    val intent = Intent(Intent.ACTION_VIEW)
    intent.addCategory(Intent.CATEGORY_BROWSABLE)
    val url = Uri.parse(url)
    intent.setData(url)
//    intent.setPackage("com.android.browser")
    startActivity(intent);
}

fun Context.openInnerBrowser(url:String) {
    val bundle = Bundle()
    bundle.putString(KEY_TARGET_URL, url)
    goTargetClass(this, BrowserActivity::class.java, bundle)
}

fun Context.openServiceOnline() {
    val crispIntent = Intent(this, ChatActivity::class.java)
    startActivity(crispIntent)
}