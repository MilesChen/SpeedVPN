package com.wind.vpn.bean

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Objects

inline fun <reified T>String.toBean():T? {
    try {
        return Gson().fromJson(this, object : TypeToken<T>(){}.type)
    } catch ( e: Exception) {
        e.printStackTrace()
    }
    return null
}

inline fun <reified T>String.toRespBean():BaseBean<T> {
    try {
        return Gson().fromJson(this, object : TypeToken<BaseBean<T>>(){}.type)
    } catch ( e: Exception) {
        e.printStackTrace()
    }
    return BaseBean()
}