package com.github.kr328.clash.common.log

import com.github.kr328.clash.common.BuildConfig

object Log {
    private const val TAG = "ClashMetaForAndroid"

    fun i(message: String, throwable: Throwable? = null){
        if (!BuildConfig.Loggable) {
            return
        }
        android.util.Log.i(TAG, message, throwable)
    }


    fun w(message: String, throwable: Throwable? = null) {
        android.util.Log.w(TAG, message, throwable)
    }


    fun e(message: String, throwable: Throwable? = null){
        if (!BuildConfig.Loggable) {
            return
        }
        android.util.Log.e(TAG, message, throwable)
    }


    fun d(message: String, throwable: Throwable? = null) {
        if (!BuildConfig.Loggable) {
            return
        }
        android.util.Log.d(TAG, message, throwable)
    }

    fun v(message: String, throwable: Throwable? = null) {
        if (!BuildConfig.Loggable) {
            return
        }
        android.util.Log.v(TAG, message, throwable)
    }


    fun f(message: String, throwable: Throwable) {
        android.util.Log.wtf(message, throwable)
    }

}
