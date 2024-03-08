package com.wind.vpn.data.bean

data class OrderInfo(
    /*invite_user_id	number	邀请人id
    plan_id	number	订阅id
    coupon_id	number	优惠券id
    payment_id	number	支付方式id
    type	number	订单类型 1新购2续费3升级
    cycle	string	订阅周期
    trade_no	string	订单号
    callback_no	string	退款单号
    total_amount	number	总金额
    discount_amount	number	折扣金额
    surplus_amount	number	剩余价值
    refund_amount	number	退款金额
    balance_amount	number	使用余额
    surplus_order_ids	number	折抵订单
    status	number	订单状态 0待支付1开通中2已取消3已完成4已折抵
    commission_status	number	佣金状态 0待确认1发放中2有效3无效
    commission_balance	number	佣金余额
    paid_at	timestamp	支付时间
    created_at	timestamp	创建时间
    updated_at	timestamp	更新时间
    plan	关联表	订阅详情*/

val actual_commission_balance: Any,
    val balance_amount: Long,
    val callback_no: Any,
    val commission_balance: Int,
    val commission_status: Int,
    val coupon_id: Any,
    val created_at: Int,
    val discount_amount: Long,
    val handling_amount: Any,
    val invite_user_id: Any,
    val paid_at: Any,
    val payment_id: Any,
    val period: String,
    val plan: WindPlan,
    val plan_id: Int,
    val refund_amount: Any,
    val status: Int,
    val surplus_amount: Any,
    val surplus_order_ids: Any,
    val total_amount: Long,
    val trade_no: String,
    val type: Int,
    val updated_at: Int
)