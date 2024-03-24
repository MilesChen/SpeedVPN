package com.wind.vpn.data

import android.util.Log
import com.github.kr328.clash.common.Global
import com.wind.vpn.WindGlobal
import com.wind.vpn.bean.BaseBean
import com.wind.vpn.bean.ERROR
import com.wind.vpn.bean.NET_ERR
import com.wind.vpn.bean.SUCCESS
import com.wind.vpn.bean.toRespBean
import okhttp3.FormBody
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

object RequestManager {
    const val TAG = "RequestManager"
    val commonParams = HashMap<String, Any?>()
    val client = OkHttpClient()
    val downloadClient by lazy { OkHttpClient() }

    inline fun <reified R> requestByGetComm(
        api: String,
        params: HashMap<String, Any?> = HashMap(),
        transform: (String) -> String = { ex -> ex }
    ): BaseBean<R> {
        return requestByGetDomainRetry<R>(api, params, transform)
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
                if (result.retCode != NET_ERR && result.httpCode !in 400..499) {
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
        params: HashMap<String, Any?> = HashMap(),
        transform: (String) -> String = { ex -> ex }
    ): BaseBean<R>? {
        Log.d(TAG, "start do GET request $url")
        var result: BaseBean<R> = BaseBean()
        try {
            refreshCommHeaders()
            val httpUrl = url.toHttpUrl().newBuilder()
            httpUrl.apply {
                for (entry in params) {
                    addQueryParameter(entry.key, "${entry.value}")
                }
            }
            val request = Request.Builder().apply {
                url(httpUrl.build())
                for (entry in commonParams) {
                    addHeader(entry.key, "${entry.value}")
                }
                method("GET", null)
            }.build()
            val response: Response = client.newCall(request).execute()
            val respStr = transform((response.body?.string()) ?: "{}")
            result = respStr.toRespBean<R>()
            result?.retCode = if (response.isSuccessful) SUCCESS else ERROR
            Log.d(TAG, "get response from url:$url retCode:${response.code} resp:$respStr")
            result.httpCode = response.code
        } catch (e: Exception) {
            Log.e(TAG, "get exception by getReq url $url", e)
            e.printStackTrace()
            result.retCode = NET_ERR
        }
        return result
    }

    inline fun <reified R> requestByPostComm(
        api: String,
        params: HashMap<String, Any?> = HashMap<String, Any?>(),
        transform: (String) -> String = { ex -> ex }
    ): BaseBean<R> {
        return requestByPostDomainRetry<R>(api, params, transform)
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
                if (result.retCode != NET_ERR && result.httpCode !in 400..499) {
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
            refreshCommHeaders()
            val json = JSONObject(params).toString()
            val requestBody = json?.let {
                var contentType: MediaType = "application/json".toMediaType()
                json.toRequestBody(contentType)
            } ?: run { FormBody.Builder().build() }
            Log.d(TAG, "start post request and url is $url requestBody:$json")
            val request = Request.Builder().apply {
                url(url)
                post(requestBody)
                for (entry in commonParams) {
                    addHeader(entry.key, "${entry.value}")
                }

            }.build()
            val response: Response = client.newCall(request).execute()
            val respStr = transform((response.body?.string()) ?: "{}")
            result = respStr.toRespBean<R>()
            result?.retCode = if (response.isSuccessful) SUCCESS else ERROR
            Log.d(TAG, "get response from url:$url retCode:${response.code} resp:$respStr")
            result.httpCode = response.code
        } catch (e: Exception) {
            result.retCode = NET_ERR
            e.printStackTrace()
            Log.e(TAG, "get exception by postReq url $url", e)
        }
        return result
    }

    @Synchronized
    fun refreshCommHeaders(): HashMap<String, Any?> {
        if (commonParams.isEmpty()) {
            commonParams.apply {
                put("osVer", WindGlobal.osVer)
                put("vCode", WindGlobal.vCode)
                put("vName", WindGlobal.vName)
                put("pModel", WindGlobal.pModel)
                put("manufacturer", WindGlobal.manufacturer)
                put("brand", WindGlobal.brand)
                put("product", WindGlobal.product)
                put("platform", "Android")
                put("aid", WindGlobal.androidid)
            }
        }
        if (WindGlobal.account.isLogin()) {
            commonParams.apply {
                put("uEmail", WindGlobal.email)
                put("token", WindGlobal.token)
                put("Authorization", WindGlobal.account.auth_data)
            }

        }
        return commonParams
    }

    suspend fun startDownload(file: File, targetUrl: String): File? {
        if (file.exists()) {
            file.delete()
        }
        var newFile: File? = null

        val request = Request.Builder().url(targetUrl).build()
        try {
            downloadClient.newCall(request).execute().use {
                if (it.isSuccessful) {
                    it.body?.byteStream()?.use { ips ->
                        FileOutputStream(file).use { os ->
                            val buffer = ByteArray(1024)
                            var length = -1
                            while (ips.read(buffer).also { l -> length = l } != -1) {
                                os.write(buffer, 0, length)
                            }
                            os.flush()
                        }

                    }
                    newFile = file
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return newFile
    }

    fun buildFormBody(): FormBody {
        val build = FormBody.Builder()
        for (entry in commonParams) {
            build.add(entry.key, (entry.value?.toString()) ?: "")
        }
        return build.build()
    }
}

fun <R> BaseBean<R>.getErrMsg(): String {
    return if (message.isNullOrEmpty()) Global.application.getString(com.github.kr328.clash.R.string.toast_error) else message!!
}
