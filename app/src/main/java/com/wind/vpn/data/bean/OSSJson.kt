package com.wind.vpn.data.bean

class OSSJson {
    var BuiltInProxy: String? = null
    var HomePage: String? = null
    var RemoteHosts: List<String>? = null
    var RemoteJson: List<String>? = null
    var RemoteType: Int = 0
    var TelegramGroup: String = "https://t.me/mao3vpn"
    var clientLastVersion: List<ClientLastVersion>? = null
    var everyPlayingUrl: String? = null
    var qrCodeUrl: String = ""
    var registerUrl: String = "https://metaconcet.com"
    fun isValid(): Boolean {
        return !RemoteHosts.isNullOrEmpty()
    }
}