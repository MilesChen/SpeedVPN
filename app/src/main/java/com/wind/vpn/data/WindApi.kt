package com.wind.vpn.data

import com.wind.vpn.bean.BaseBean
import com.wind.vpn.data.bean.LoginBean
import com.wind.vpn.data.account.WindProfile
import com.wind.vpn.data.account.WindSubscribe
import com.wind.vpn.data.bean.OrderInfo
import com.wind.vpn.data.bean.PayMethod
import com.wind.vpn.data.bean.CommConfig
import com.wind.vpn.data.bean.CouponBean
import com.wind.vpn.data.bean.InviteResp
import com.wind.vpn.data.bean.NoticeBean
import com.wind.vpn.data.bean.WindPlan

const val API_REGISTER = "/passport/auth/register"//注册接口
const val API_LOGIN = "/passport/auth/login"//登录
const val API_USER_INFO = "/user/info"//获取用户信息
const val API_USER_SUBSCRIBE = "/user/getSubscribe"//获取订阅信息
const val API_PLAN_PAY = "/user/plan/fetch"//获取订阅套餐商品
const val API_PLAN_CONF = "/user/comm/config"//获取当前货币单位
const val API_ORDER_SAVE = "/user/order/save"//下单
const val API_GET_PAYMENT = "/user/order/getPaymentMethod"//获取支付方式
const val API_GET_ORDER_LIST = "/user/order/fetch"//获取订单列表
const val API_CANCEL_ORDER = "/user/order/cancel"//取消订单
const val API_CHECKOUT_ORDER = "/user/order/checkout"//checkout发起订单支付
const val API_ORDER_DETAIL = "/user/order/detail"//查询订单详情
const val API_ORDER_CHECK = "/user/order/check"//查询订单详情
const val API_VERIFY_COUPON = "/user/coupon/check"//校验优惠券
const val API_GET_NOTICE = "/user/notice/fetch"//获取公告信息
const val API_GET_INVITE = "/user/invite/fetch"//获取邀请信息和邀请码
const val API_GET_GEN_INVITE_CODE = "/user/invite/save"//生成邀请码--接口不返回邀请码信息，实属不应该


object WindApi {
    /**
     * 登录
     */
    fun login(email: String, pwd: String): BaseBean<LoginBean> {
        val params = HashMap<String, Any?>()
        params.apply {
            put("email", email)
            put("password", pwd)
        }
        return RequestManager.requestByPostDomainRetry<LoginBean>(API_LOGIN, params)
    }

    /**
     * 注册
     * @param email 注册邮箱
     * @param pwd 密码
     */
    fun register(email: String, pwd: String): BaseBean<LoginBean> {
        val params = HashMap<String, Any?>()
        params.apply {
            put("email", email)
            put("password", pwd)
            put("invite_code", "")
            put("email_code", "")
        }
        return RequestManager.requestByPostDomainRetry<LoginBean>(API_REGISTER, params)
    }

    /**
     * 获取用户信息
     */
    fun loadUserProfile(): BaseBean<WindProfile> {
        return RequestManager.requestByGetComm<WindProfile>(API_USER_INFO)
    }

    /**
     * 获取订阅地址
     */
    fun loadWindSubscribe(): BaseBean<WindSubscribe> {
        return RequestManager.requestByGetComm<WindSubscribe>(API_USER_SUBSCRIBE)
    }

    fun fetchWindPackage(): BaseBean<List<WindPlan>> {
        return RequestManager.requestByGetComm<List<WindPlan>>(API_PLAN_PAY)
    }

    /**
     * 获取订单货币类型
     */
    fun getCommConf(): BaseBean<CommConfig> {
        return RequestManager.requestByGetComm<CommConfig>(API_PLAN_CONF)
    }


    /**
     * 创建订单
     * @param plan_id WindPlan中的id
     */
    fun saveOrder(period:String, plan_id:Long, coupon_code:String?): BaseBean<String> {
        val params = HashMap<String, Any?>()
        params["period"] = period
        params["plan_id"] = plan_id
        coupon_code?.let {
            params["coupon_code"] = coupon_code
        }
        return RequestManager.requestByPostComm<String>(API_ORDER_SAVE, params)
    }

    fun getOrderDetail(trade_no: String): BaseBean<OrderInfo> {
        val params = HashMap<String, Any?>()
        params["trade_no"] = trade_no
        return RequestManager.requestByGetComm<OrderInfo>(API_ORDER_DETAIL, params)
    }

    /**
     * 获取支持的支付方式
     */
    fun getPayMethod(): BaseBean<List<PayMethod>> {
        return RequestManager.requestByGetComm<List<PayMethod>>(API_GET_PAYMENT)
    }

    /**
     * 获取用户订单列表
     */
    fun getOrderList(): BaseBean<List<OrderInfo>> {
        return RequestManager.requestByGetComm<List<OrderInfo>>(API_GET_ORDER_LIST)
    }


    /**
     * 取消某个待支付订单
     * @param trade_no 订单号
     */
    fun cancelOrder(trade_no: String): BaseBean<Boolean> {
        var params = HashMap<String, Any?>()
        params.put("trade_no", trade_no)
        return RequestManager.requestByPostComm<Boolean>(API_CANCEL_ORDER, params)
    }

    /**
     * 尝试取消已经存在的订单，系统中默认只会有一个待支付订单
     */
    fun cancelExistOrder():Boolean {
        val ordersResp = getOrderList()
        if (ordersResp.isSuccess && !ordersResp.data.isNullOrEmpty()) {
            for (order in ordersResp.data!!) {
                if (order.status == 0) {//系统中最多只会有一笔待支付订单
                    return cancelOrder(order.trade_no!!).isSuccess
                }
            }
        }
        return true
    }

    /**
     * checkout出聚合支付地址
     */
    fun checkoutOrder(trade_no:String, method:Int): BaseBean<String> {
        val params = HashMap<String, Any?>()
        params["trade_no"] = trade_no
        params["method"] = method
        return RequestManager.requestByPostComm<String>(API_CHECKOUT_ORDER, params)
    }

    fun checkOrderStatus(trade_no: String): BaseBean<Int> {
        val params = HashMap<String, Any?>()
        params["trade_no"] = trade_no
        return RequestManager.requestByGetComm<Int>(API_ORDER_CHECK, params)
    }

    fun verifyCoupon(code: String, plan_id: Long): BaseBean<CouponBean> {
        val params = HashMap<String, Any?>()
        params["code"] = code
        params["plan_id"] = plan_id
        return RequestManager.requestByPostComm<CouponBean>(API_VERIFY_COUPON, params)
    }

    fun getNotice(): BaseBean<List<NoticeBean>> {
        return RequestManager.requestByGetComm<List<NoticeBean>>(API_GET_NOTICE)
    }

    fun getInviteInfo(): BaseBean<InviteResp> {
        return RequestManager.requestByGetComm<InviteResp>(API_GET_INVITE)
    }

    fun getInviteCode(): BaseBean<Boolean> {
        return RequestManager.requestByGetComm<Boolean>(API_GET_GEN_INVITE_CODE)
    }
}