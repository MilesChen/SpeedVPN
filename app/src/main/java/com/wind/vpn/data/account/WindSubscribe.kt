package com.wind.vpn.data.account

import com.wind.vpn.data.bean.WindPlan

data class WindSubscribe(
    val alive_ip: Int,
    val d: Int,
    val device_limit: Int,
    val email: String,
    val expired_at: Int,
    val plan: WindPlan,
    val plan_id: Int,
    val reset_day: Int,
    val subscribe_url: String,
    val token: String,
    val transfer_enable: Long,
    val u: Int,
    val uuid: String
)