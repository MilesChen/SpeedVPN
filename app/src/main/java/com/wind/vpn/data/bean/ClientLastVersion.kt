package com.wind.vpn.data.bean

class ClientLastVersion {
    var clientType: Int = 0
    var md5: String? = null
    var targetUrl: String? = null
    var versionCode: Long = 0
    var versionName: String? = null

    fun isValid():Boolean {
        return !(md5.isNullOrEmpty() || targetUrl.isNullOrEmpty())
    }
}