package com.wind.vpn.data.account

class WindAccount {
    var email: String? = ""
    var pwd: String? = ""
    var token: String? = ""
    var auth_data: String? = ""
    var is_admin = 0
    fun isLogin():Boolean {
        return !email.isNullOrEmpty() && !token.isNullOrEmpty() && !auth_data.isNullOrEmpty()
    }
}