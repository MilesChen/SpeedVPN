package com.wind.vpn.util

import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import com.github.kr328.clash.R
import com.github.kr328.clash.common.Global
import com.github.kr328.clash.util.openServiceOnline
import com.github.kr328.clash.util.startLoadUrl
import java.io.File
import java.io.FileInputStream
import java.math.BigDecimal
import java.security.MessageDigest

var statusBarHeight: Int = 0;
fun getStatusBarHeight(context: Context): Int {
    if (statusBarHeight != 0) {
        return statusBarHeight;
    }
    val resId = context.resources.getIdentifier("status_bar_height", "dime", "android")
    if (resId > 0) {
        statusBarHeight = context.resources.getDimensionPixelSize(resId)
    } else {
        statusBarHeight = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            24f,
            context.resources.displayMetrics
        ).toInt();
    }
    return statusBarHeight
}

fun dp2px(dp: Float): Int {
    val metrics = Global.application.resources.displayMetrics
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

fun File.getFileMD5(): String {
    val digest = MessageDigest.getInstance("MD5")
    val fis = FileInputStream(this)
    val buffer = ByteArray(1024)
    var read = fis.read(buffer)
    while (read != -1) {
        digest.update(buffer, 0, read)
        read = fis.read(buffer)
    }
    fis.close()
    val md5Bytes = digest.digest()
    return md5Bytes.joinToString("") { "%02x".format(it) }
}

fun ByteArray.getMd5():String {
    val md = MessageDigest.getInstance("MD5")
    val digest = md.digest(this)
    return digest.joinToString ("") { "%02x".format(it) }
}

fun centToYuan(price:Long):String {
    return "${BigDecimal(price).divide(BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP)}"
}

fun Context.periodToDesc(period: String): String {
    return if (period == "month_price") {
        getString(R.string.month_price)
    } else if (period == "quarter_price") {
        getString(R.string.quarter_price)
    } else if (period == "half_year_price") {
        getString(R.string.half_year_price)
    } else if (period == "year_price") {
        getString(R.string.year_price)
    } else if (period == "two_year_price") {
        getString(R.string.two_year_price)
    } else if (period == "three_year_price") {
        getString(R.string.three_year_price)
    } else if (period == "onetime_price") {
        getString(R.string.onetime_price)
    } else {
        ""
    }
}

fun TextView.buildSupperLinkSpan(originText:String, targetText:String, targetUrl:String, underline:Boolean = false) {
    val spannable = SpannableString(originText)
    val start = originText.indexOf(targetText)
    spannable.setSpan(object: ClickableSpan(){
        override fun onClick(widget: View) {
            if (targetUrl.startsWith("http")) {
                context.startLoadUrl(targetUrl)

            } else if (targetUrl.startsWith("crisp")) {
                context.openServiceOnline()
            } else {

            }

//            context.openInnerBrowser(targetUrl)
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.color = Color.parseColor("#198CFF")
            ds.isUnderlineText = underline
            ds.bgColor = Color.TRANSPARENT
        }
    }, start, originText.length, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
    text = spannable
    movementMethod = LinkMovementMethod.getInstance()
    highlightColor = Color.TRANSPARENT
}