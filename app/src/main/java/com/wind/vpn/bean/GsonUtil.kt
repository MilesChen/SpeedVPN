package com.wind.vpn.bean

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

inline fun <reified T>String.toBean():T? {
    try {
        return Gson().fromJson(this, object : TypeToken<T>(){}.type)
    } catch ( e: Exception) {
        e.printStackTrace()
    }
    return null
}

inline fun <reified T>String.toRespBean(): com.wind.vpn.bean.BaseBean<T> {
    try {
        return Gson().fromJson(this, object : TypeToken<com.wind.vpn.bean.BaseBean<T>>(){}.type)
    } catch ( e: Exception) {
        e.printStackTrace()
    }
    return com.wind.vpn.bean.BaseBean()
}