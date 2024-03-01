package com.wind.vpn

import android.content.Context
import android.content.Intent
import android.os.Build
import com.github.kr328.clash.common.Global
import com.github.kr328.clash.common.constants.Intents
import com.github.kr328.clash.service.util.sendBroadcastSelf
import com.github.kr328.clash.store.AppStore
import com.github.kr328.clash.util.isMainProgress
import com.google.gson.Gson
import com.wind.vpn.bean.toBean
import com.wind.vpn.data.DomainManager
import com.wind.vpn.data.WindApi
import com.wind.vpn.data.account.WindAccount
import com.wind.vpn.data.account.WindProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object WindGlobal: CoroutineScope by CoroutineScope(Dispatchers.IO) {
    var email: String = ""
    var token: String = ""
    var vName: String = ""
    var vCode: Long = 0
    var osVer = Build.VERSION.SDK_INT
    var pModel = Build.MODEL
    var brand = Build.BRAND
    var manufacturer = Build.MANUFACTURER
    var product = Build.PRODUCT
    var store = AppStore(Global.application)
    private var account_ = store.account.toBean<WindAccount>() ?: WindAccount()
    private var userInfo_ = store.userInfo.toBean<WindProfile>() ?: WindProfile()

    var account: WindAccount
        get() {
            return account_
        }
        set(value) {
            account_ = value
            store.account = Gson().toJson(account_)
            if (account_.isLogin())
                Global.application.sendBroadcastSelf(Intent(Intents.ACTION_LOGIN_SUCCESS))
            else Global.application.sendBroadcastSelf(Intent(Intents.ACTION_LOGOUT))
        }

    var userInfo: WindProfile
        get() = userInfo_
        set(value) {
            userInfo_ = value
            store.userInfo = Gson().toJson(userInfo_)
            Global.application.sendBroadcastSelf(Intent(Intents.ACTION_USER_LOADED))
        }

    fun init(context: Context) {
        if (context.isMainProgress()) {
            DomainManager.init()
        }
        var info = context.packageManager.getPackageInfo(context.packageName, 0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            vCode = info.longVersionCode
        } else {
            vCode = info.versionCode.toLong()
        }
        vName = info.versionName
    }


    fun loadUserInfoFromServer() {
        launch {
            var userProfile = WindApi.loadUserProfile()
            if (userProfile.isSuccess && userProfile.data != null) {
                userInfo = userProfile.data!!
            }
        }
        launch {
            var windSubscribe = WindApi.loadWindSubscribe()
        }
    }




}