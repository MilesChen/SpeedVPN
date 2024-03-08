package com.wind.vpn

import com.google.gson.Gson

fun Any?.toJson():String{
    if (this == null) return ""
    if (this is String) return this.toString()
    return Gson().toJson(this)
}

fun Any.getPropertyValue(propName: String): Any? {
    val field = this::class.java.getDeclaredField(propName)
    field?.isAccessible = true
    return field?.get(this)
}
