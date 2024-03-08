package com.wind.vpn.util

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.wind.vpn.activity.UserCenterActivity

class RoutUtils {
}

fun goTargetClass(context:Context, clazz: Class<*>, bundle:Bundle?=null) {
    val intent = Intent(context, clazz)
    bundle?.let {
        intent.putExtras(bundle)
    }
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}