package com.wind.vpn.bean

const val ERROR = -1;
const val TIMEOUT = 0;
const val SUCCESS = 1;
const val NET_ERR = 2

open class BaseBean<T> {
    var retCode: Int = NET_ERR
    var data: T? = null
    var httpCode = -10086
    var message: String? = ""
    var type: Int = 0//奇怪，接口竟然把该字段放在外层
    var isSuccess = false
        get() = retCode == SUCCESS

}