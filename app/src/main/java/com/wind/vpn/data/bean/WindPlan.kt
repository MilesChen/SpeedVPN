package com.wind.vpn.data.bean

class WindPlan {
    /*HTTP Status Code 200

    Name	Type	Required	Restrictions	Title	description
     id	number	true	none	订阅id	none
     group_id	number	true	none	权限组id	none
     transfer_enable	number	true	none	可用流量	none
     device_limit	number	true	none	设备数限制	none
     name	string	true	none	套餐名称	none
     speed_limit	number	true	none	限速	none
     show	number	true	none	是否显示	none
     sort	string	true	none	分类	none
     renew	number	true	none	开启续费	none
     content	string	true	none	套餐描述	none
     month_price	number	true	none	月付价格	none
     quarter_price	number	true	none	季度价格	none
     half_year_price	number	true	none	半年价格	none
     year_price	number	true	none	年价格	none
     two_year_price	number	true	none	两年价格	none
     three_year_price	number	true	none	三年价格	none
     onetime_price	number	true	none	一次性价格	none
     reset_price	number	true	none	重置价格	none
     reset_traffic_method	number	true	none	重置流量方式	none
     capacity_limit	number	true	none	最大容纳用户量	none
     created_at	number	true	none	套餐创建时间	timestamp
     updated_at	number	true	none	套餐更新时间	timestamp
    */
    var id = 0L
    var group_id = 0L
    var transfer_enable = 0L
    var device_limit = 0
    var name: String? = ""
    var speed_limit = 0L
    var show = 0
    var sort:String? = ""
    var renew = 0
    var content:String? = ""
    var month_price = 0L
    var quarter_price = 0L
    var half_year_price = 0L
    var year_price = 0L
    var two_year_price = 0L
    var three_year_price = 0L
    var onetime_price = 0L
    var reset_price = 0L
    var reset_traffic_method = 0L
    var capacity_limit = 0
    var created_at = 0L
    var updated_at = 0L
}