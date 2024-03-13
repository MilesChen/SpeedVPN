package com.wind.vpn.data.bean

class OrderInfo {
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

    var actual_commission_balance: Any? = null
    var balance_amount: Long =0L
    var callback_no: Any? = null
    var commission_balance: Int = 0
    var commission_status: Int = 0
    var coupon_id: Any? = null
    var created_at: Int = 0
    var discount_amount: Long = 0L
    var handling_amount: Any? = null
    var invite_user_id: Any? = null
    var paid_at: Any? = null
    var payment_id: Any? = null
    var period: String? = null
    var plan: WindPlan? = null
    var plan_id: Int? = null
    var refund_amount: Any? = null
    var status: Int = 0
    var surplus_amount: Any? = null
    var surplus_order_ids: Any? = null
    var total_amount: Long = 0L
    var trade_no: String? = null
    var type: Int = 0
    var updated_at: Int = 0
}