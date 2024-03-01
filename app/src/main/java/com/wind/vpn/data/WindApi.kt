package com.wind.vpn.data

import com.wind.vpn.bean.BaseBean
import com.wind.vpn.data.bean.LoginBean
import com.wind.vpn.data.account.WindProfile
import com.wind.vpn.data.account.WindSubscribe

const val API_REGISTER = "/passport/auth/register"//注册接口
const val API_LOGIN = "/passport/auth/login"//登录
const val API_USER_INFO = "/user/info"//获取用户信息
const val API_USER_SUBSCRIBE = "/user/getSubscribe"//获取订阅信息


object WindApi {
    fun login(email: String, pwd: String): BaseBean<LoginBean> {
        val params = HashMap<String, Any?>()
        params.apply {
            put("email", email)
            put("password", pwd)
        }
        return RequestManager.requestByPostDomainRetry<LoginBean>(API_LOGIN, params)
    }

    fun register(email: String, pwd: String): BaseBean<LoginBean> {
        val params = HashMap<String, Any?>()
        params.apply {
            put("email", email)
            put("password", pwd)
            put("invite_code", "")
            put("email_code", "")
        }
        return RequestManager.requestByPostDomainRetry<LoginBean>(API_REGISTER, params)
    }

    fun loadUserProfile(): BaseBean<WindProfile> {
        return RequestManager.requestByGetComm<WindProfile>(API_USER_INFO)
    }

    fun loadWindSubscribe():BaseBean<WindSubscribe> {
        return RequestManager.requestByGetComm<WindSubscribe>(API_USER_SUBSCRIBE)
    }
}