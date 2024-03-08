package com.wind.vpn.data.bean

data class CommConfig(
    val commission_distribution_enable: Int,
    val commission_distribution_l1: Any,
    val commission_distribution_l2: Any,
    val commission_distribution_l3: Any,
    val currency: String,
    val currency_symbol: String,
    val is_telegram: Int,
    val stripe_pk: Any,
    val telegram_discuss_link: String,
    val withdraw_close: Int,
    val withdraw_methods: List<String>
)