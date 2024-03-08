package com.wind.vpn.data.bean

data class PayMethod(
    val handling_fee_fixed: Any,
    val handling_fee_percent: Any,
    val icon: String,
    val id: Int,
    val name: String,
    val payment: String
)