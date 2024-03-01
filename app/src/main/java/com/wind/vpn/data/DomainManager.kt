package com.wind.vpn.data

import com.github.kr328.clash.common.Global
import com.github.kr328.clash.store.AppStore
import com.google.gson.Gson
import com.wind.vpn.bean.BaseBean
import com.wind.vpn.bean.toBean
import com.wind.vpn.data.bean.SSOBean
import com.wind.vpn.toJson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object DomainManager : CoroutineScope by CoroutineScope(Dispatchers.IO) {
    private const val api_version = "/api/v1"
    private val sso_list = arrayOf(
        "https://1apnortheast12grpc.meetfast.xyz/3m.json",
        "https://1cacentral11grcp.meetfast.xyz/3m.json"
    )
    private val store = AppStore(Global.application)
    private var lastValidHost_: String = store.lastHost;
    private const val TAG = "DomainManager"
    private const val defaultSSOJson: String =
        "{\"RemoteHosts\":[\"https://fastmeta.net\",\"\"],\"RemoteType\":0,\"HomePage\":\"https://fastmeta.net\",\"SupportApi\":\"\",\"TelegramGroup\":\"https://t.me/mao3vpn\",\"BuiltInProxy\":\"\"}"
    private var ssoBean: SSOBean =
        store.lastSSO.toBean<SSOBean>() ?: (defaultSSOJson.toBean<SSOBean>() ?: SSOBean())

    fun init() {
        launch {
            loadSSOConfig()
        }
    }

    private fun loadSSOConfig() {
        for (url in sso_list) {
            var ssoResult: BaseBean<SSOBean>? =
                RequestManager.requestByGet<SSOBean>(url) { ex ->
                    run {
                        var tempSSO = ex.toBean<SSOBean>()
                        var temp = BaseBean<SSOBean>()
                        temp.data = tempSSO
                        Gson().toJson(temp)
                    }
                }
            ssoResult?.let {
                if (ssoResult.isSuccess && ssoResult.data != null) {
                    ssoBean = ssoResult.data!!
                    store.lastSSO = ssoBean.toJson()
                    return
                }
            }

        }
    }


    var validHost: String
        get() = if (lastValidHost_.isNullOrEmpty()) ssoBean.RemoteHosts[0] else lastValidHost_
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
        } else if (index < ssoBean.RemoteHosts.size) {
            return ssoBean.RemoteHosts[index]
        }
        return null;
    }
}