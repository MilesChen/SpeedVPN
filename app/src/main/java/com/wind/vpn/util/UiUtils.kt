package com.wind.vpn.util

import android.content.Context
import android.util.TypedValue
import java.security.MessageDigest

var statusBarHeight:Int = 0;
fun getStatusBarHeight(context: Context):Int {
    if (statusBarHeight != 0) {
        return statusBarHeight;
    }
    val resId = context.resources.getIdentifier("status_bar_height", "dime", "android")
    if (resId > 0) {
        statusBarHeight = context.resources.getDimensionPixelSize(resId)
    } else {
        statusBarHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24f, context.resources.displayMetrics).toInt();
    }
    return statusBarHeight
}

fun dp2px(context: Context, dp: Float): Int {
    val metrics = context.resources.displayMetrics
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics).toInt()
}

fun String.calculateMd5(): String {
    val digest = MessageDigest.getInstance("MD5")
    val bytes = toByteArray()

    val resultBytes = digest.digest(bytes)

    return StringBuilder().apply {
        for (byte in resultBytes) {
            append("%02x".format(byte))
        }
    }.toString()
}
