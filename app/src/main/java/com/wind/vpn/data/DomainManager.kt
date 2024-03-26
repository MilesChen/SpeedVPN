package com.wind.vpn.data

import android.content.Intent
import com.github.kr328.clash.common.Global
import com.github.kr328.clash.common.constants.Intents
import com.github.kr328.clash.common.log.Log
import com.github.kr328.clash.service.util.sendBroadcastSelf
import com.github.kr328.clash.store.AppStore
import com.google.gson.Gson
import com.wind.vpn.WindGlobal
import com.wind.vpn.bean.BaseBean
import com.wind.vpn.bean.toBean
import com.wind.vpn.data.bean.OSSJson
import com.wind.vpn.toJson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object DomainManager : CoroutineScope by CoroutineScope(Dispatchers.IO) {
    private const val api_version = "/api/v1"
    private var sso_list = mutableListOf<String>(
        "https://f123.ink/windfast.json",
        "https://f123.ink/windfast.json",
        "https://fast2.ink/windfast.json",
        "https://fwind.ink/windfast.json",
        "https://fwind.xyz/windfast.json"
    )
    private val store = AppStore(Global.application)
    private var lastValidHost_: String = store.lastHost
    private const val TAG = "DomainManager"
    private const val defaultSSOJson: String =
        "{\n" +
                "    \"RemoteHosts\": [\n" +
                "        \"https://fastmeta.net\"\n" +
                "    ],\n" +
                "    \"RemoteJson\": [\n" +
                "        \"https://metaconcet.com/windfast.json\",\n" +
                "        \"https://f123.ink/windfast.json\",\n" +
                "        \"https://fast2.ink/windfast.json\",\n" +
                "        \"https://fwind.ink/windfast.json\",\n" +
                "        \"https://fwind.xyz/windfast.json\"\n" +
                "    ],\n" +
                "    \"clientLastVersion\": [\n" +
                "        {\n" +
                "            \"clientType\": 1,\n" +
                "            \"versionCode\": 240322,\n" +
                "            \"versionName\": \"1.0.7\",\n" +
                "            \"targetUrl\": \"https://f123.ink/dl/android/wind-1.0.7-meta-arm64-v8a-release.apk\",\n" +
                "            \"md5\": \"74db202d0e053ffbc825a1880097df31\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"clientType\": 2,\n" +
                "            \"versionCode\": 1234,\n" +
                "            \"versionName\": \"版本号\",\n" +
                "            \"targetUrl\": \"文件下载地址或应用市场地址\",\n" +
                "            \"md5\": \"完整文件md5的值\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"clientType\": 4,\n" +
                "            \"versionCode\": 1234,\n" +
                "            \"versionName\": \"版本号\",\n" +
                "            \"targetUrl\": \"文件下载地址或应用市场地址\",\n" +
                "            \"md5\": \"完整文件md5的值\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"RemoteType\": 0,\n" +
                "    \"HomePage\": \"https://metaconcet.com\",\n" +
                "    \"TelegramGroup\": \"https://t.me/mao3vpn\",\n" +
                "    \"BuiltInProxy\": \"\",\n" +
                "    \"everyPlayingUrl\": \"https://f123.ink/play.htm\",\n" +
                "    \"qrCodeUrl\": \"https://f123.ink/dl/qr/download1.0.7.png\"\n" +
                "}"
    var ossBean: OSSJson =
        store.lastOSS.toBean<OSSJson>() ?: (defaultSSOJson.toBean<OSSJson>() ?: OSSJson())

    fun init() {
        launch {
            loadOSSConfig()
        }
    }

    private suspend fun loadOSSConfig() {
        if (ossBean.RemoteJson.isNullOrEmpty()) {
            ossBean.RemoteJson = sso_list
        }
        for (url in ossBean.RemoteJson!!) {
            var ssoResult: BaseBean<OSSJson>? =
                RequestManager.requestByGet<OSSJson>(url) { ex ->
                    run {
                        var tempSSO = ex.toBean<OSSJson>()
                        var temp = BaseBean<OSSJson>()
                        temp.data = tempSSO
                        Gson().toJson(temp)
                    }
                }
            ssoResult?.let {
                if (it.isSuccess && it.data != null && it.data!!.isValid()) {
                    ossBean = it.data!!
                    store.lastOSS = ossBean.toJson()
                    Global.application.sendBroadcastSelf(Intent(Intents.ACTION_OSS_UPDATE_SUC))
                    launch(Dispatchers.IO) {
                        WindGlobal.upgradeManager.checkVersion()
                    }
                    Log.d("get oss success! stop loading")
                    return
                }
            }

        }
    }


    var validHost: String
        get() = if (lastValidHost_.isNullOrEmpty()) ossBean.RemoteHosts?.get(0)!! else lastValidHost_
        set(value) {
            if (!value.isNullOrEmpty()) {
                lastValidHost_ = value
                store.lastHost = value
            }
        }

    //根据重试次数轮询host
    fun buildUrlWithHost(api: String, index: Int): String? {
        val host = getValidHost(index)
        if (host.isNullOrEmpty()) {
            return null
        }
        return "$host$api_version$api"
    }

    //获取接口域名
    fun getValidHost(index: Int): String? {
        if (index < 0) {
            return validHost
        } else if (index < ossBean.RemoteHosts!!.size) {
            return ossBean.RemoteHosts?.get(index)
        }
        return null;
    }
}