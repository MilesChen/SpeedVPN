package com.wind.vpn.data.bean

class CommConfig{
    var commission_distribution_enable: Int = 0
    var commission_distribution_l1: Any? = null
    var commission_distribution_l2: Any? = null
    var commission_distribution_l3: Any? = null
    var currency: String? = null
    var currency_symbol: String? = null
    var is_telegram: Int? = null
    var stripe_pk: Any? = null
    var telegram_discuss_link: String? = null
    var withdraw_close: Int = 0
    var withdraw_methods: List<String>? = null
}