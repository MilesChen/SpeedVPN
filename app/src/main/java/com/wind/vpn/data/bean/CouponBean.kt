package com.wind.vpn.data.bean

data class CouponBean(
    val code: String,
    val created_at: Int,
    val ended_at: Int,
    val id: Int,
    val limit_period: List<String>,
    val limit_plan_ids: List<String>,
    val limit_use: Any,
    val limit_use_with_user: Any,
    val name: String,
    val show: Int,
    val started_at: Int,
    val type: Int,
    val updated_at: Int,
    val value: Long
)