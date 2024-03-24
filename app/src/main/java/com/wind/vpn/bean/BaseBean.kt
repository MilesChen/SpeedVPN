package com.wind.vpn.bean

import com.github.kr328.clash.R
import com.github.kr328.clash.common.Global

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

    val showMsg: String
        get() = if (message.isNullOrEmpty()) Global.application.getString(R.string.toast_error) else message!!

}