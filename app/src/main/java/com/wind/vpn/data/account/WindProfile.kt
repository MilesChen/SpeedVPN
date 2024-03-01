package com.wind.vpn.data.account

class WindProfile {
    /** 参数名	类型	描述
    email	string	邮箱地址
    transfer_enable	number	总可用流量
    last_login_at	timestamp	最后登入时间
    created_at	timestamp	创建时间
    banned	number	是否封禁使用
    remind_expire	number	到期邮件提醒
    remind_traffic	number	流量邮件提醒
    expired_at	timestamp	过期时间
    balance	number	用户余额
    commission_balance	number	佣金余额
    plan_id	number - object(&plan)	当前订阅id
    discount	number	消费折扣
    commission_rate	number	佣金率
    telegram_id	number	绑定TG id
    uuid	string	唯一UUID
    avatar_url	string	头像地址
    **/
    var email:String = ""
    var transfer_enable = 0L
    var last_login_at = 0L
    var created_at = 0L
    var banned = 0L
    var remind_expire = 0L
    var remind_traffic = 0L
    var expired_at = 0L
    var balance = 0L
    var commission_balance = 0L
    var plan_id = 0L
    var discount = 0L
    var commission_rate = 0L
    var telegram_id = 0L
    var uuid:String = ""
    var avatar_url = ""
}