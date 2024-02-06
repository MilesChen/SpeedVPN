package com.wind.vpn.util

import android.content.Context
import android.content.Intent
import com.wind.vpn.activity.UserCenterActivity

class RoutUtils {
}

fun goTargetClass(context:Context, clazz: Class<*>) {
    val intent = Intent(context, clazz)
    context.startActivity(intent)
}