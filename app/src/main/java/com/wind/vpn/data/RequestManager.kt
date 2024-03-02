package com.wind.vpn.data

import android.util.Log
import com.wind.vpn.WindGlobal
import com.wind.vpn.bean.BaseBean
import com.wind.vpn.bean.ERROR
import com.wind.vpn.bean.NET_ERR
import com.wind.vpn.bean.SUCCESS
import com.wind.vpn.bean.toRespBean
import okhttp3.FormBody
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject

object RequestManager {
    const val TAG = "RequestManager"
    private val commonParams = HashMap<String, Any?>()
    val client = OkHttpClient()

    inline fun <reified R> requestByGetComm(
        api: String,
        params: HashMap<String, Any?> = HashMap(),
        transform: (String) -> String = { ex -> ex }
    ): BaseBean<R> {
        addCommonParams(params)
        if (WindGlobal.account.isLogin()) {
            params["Authorization"] = WindGlobal.account.auth_data
        }
        return requestByGetDomainRetry(api, params, transform)
    }

    inline fun <reified R> requestByGetDomainRetry(
        api: String,
        params: HashMap<String, Any?>,
        transform: (String) -> String = { ex -> ex }
    ): BaseBean<R> {
        var index = -1;
        var url = DomainManager.buildUrlWithHost(api, index)
        while (!url.isNullOrEmpty()) {
            var result = requestByGet<R>(url, params, transform)
            result?.let {
                if (result.httpCode != NET_ERR) {
                    DomainManager.validHost = DomainManager.getValidHost(index)!!
                    return result
                }
            }
            index++
            url = DomainManager.buildUrlWithHost(api, index)
        }
        return BaseBean()
    }

    inline fun <reified R> requestByGet(
        url: String,
        headers: HashMap<String, Any?> = HashMap(),
        transform: (String) -> String = { ex -> ex }
    ): BaseBean<R>? {
        Log.d(TAG, "start do GET request $url")
        var result: BaseBean<R> = BaseBean()
        try {
            val request = Request.Builder().apply {
                url(url)
                for (entry in headers) {
                    addHeader(entry.key, "${entry.value}")
                }
                method("GET", null)
            }.build()
            val response: Response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                Log.d(TAG, "get error from $url $response")
                result = "{}".toRespBean<R>()
                result?.retCode = ERROR
            } else {
                val respStr = transform((response.body?.string()) ?: "{}")
                result = respStr.toRespBean<R>()
                result?.retCode = SUCCESS
                Log.d(TAG, "get response from url:$url retCode:${response.code} resp:$respStr")
            }
            result.httpCode = response.code
        } catch (e : Exception) {
            Log.e(TAG, "get exception by getReq url $url", e)
            e.printStackTrace()
        }
        return result
    }

    inline fun <reified R> requestByPostDomainRetry(
        api: String,
        params: HashMap<String, Any?>,
        transform: (String) -> String = { ex -> ex }
    ): BaseBean<R> {
        var index = -1;
        var url = DomainManager.buildUrlWithHost(api, index)
        while (!url.isNullOrEmpty()) {
            var result = requestByPost<R>(url, params, transform)
            result?.let {
                if (result.httpCode != NET_ERR) {
                    DomainManager.validHost = DomainManager.getValidHost(index)!!
                    return result
                }
            }
            index++
            url = DomainManager.buildUrlWithHost(api, index)
        }
        return BaseBean()
    }

    inline fun <reified R> requestByPost(
        url: String,
        params: HashMap<String, Any?>,
        transform: (String) -> String = { ex -> ex }
    ): BaseBean<R>? {
        var result: BaseBean<R> = BaseBean()
        try {
            addCommonParams(params)
            val json = JSONObject(params).toString()
            val requestBody = json?.let {
                var contentType: MediaType = "application/json".toMediaType()
                json.toRequestBody(contentType)
            } ?: run { FormBody.Builder().build() }
            Log.d(TAG, "start post request and url is $url requestBody:$json")
            val request = Request.Builder().url(url).post(requestBody).build()
            val response: Response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                Log.d(TAG, "get error from $url $response")
                result = "{}".toRespBean<R>()
                result?.retCode = ERROR
            } else {
                val respStr = transform((response.body?.string()) ?: "{}")
                result = respStr.toRespBean<R>()
                result?.retCode = SUCCESS
                Log.d(TAG, "get response from url:$url retCode:${response.code} resp:$respStr")
            }
            result.httpCode = response.code
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "get exception by postReq url $url", e)
        }
        return result
    }

    fun addCommonParams(params: HashMap<String, Any?>) {
        if (commonParams.isEmpty()) {
            commonParams.apply {
                put("uEmail", WindGlobal.email)
                put("brand", WindGlobal.token)
                put("osVer", WindGlobal.osVer)
                put("vCode", WindGlobal.vCode)
                put("vName", WindGlobal.vName)
                put("pModel", WindGlobal.pModel)
                put("manufacturer", WindGlobal.manufacturer)
                put("brand", WindGlobal.brand)
                put("product", WindGlobal.product)
                put("platform", "Android")
            }
        } else {
            commonParams.apply {
                put("uEmail", WindGlobal.email)
                put("brand", WindGlobal.token)
            }
        }
        params.putAll(commonParams)
    }

    fun buildFormBody(params: HashMap<String, Any?>): FormBody {
        addCommonParams(params)
        val build = FormBody.Builder()
        for (entry in params) {
            build.add(entry.key, (entry.value?.toString()) ?: "")
        }
        return build.build()
    }
}
