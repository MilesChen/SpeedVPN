package com.wind.vpn.util

import android.content.pm.ApplicationInfo
import com.github.kr328.clash.common.Global

object WindLog {
    private const val TAG = "WindLog"
    fun d(msg:String, tr:Throwable?=null) {
        if (Global.application.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0)
        android.util.Log.d(TAG, msg)
    }
}