package com.wind.vpn.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.github.kr328.clash.R
import com.github.kr328.clash.databinding.ActSaveOrderBinding
import com.github.kr328.clash.databinding.ViewPaymentItemBinding
import com.github.kr328.clash.util.startLoadUrl
import com.wind.vpn.bean.toBean
import com.wind.vpn.data.WindApi
import com.wind.vpn.data.bean.OrderInfo
import com.wind.vpn.data.bean.PayMethod
import com.wind.vpn.getPropertyValue
import com.wind.vpn.toJson
import com.wind.vpn.util.buildSupperLinkSpan
import com.wind.vpn.util.centToYuan
import com.wind.vpn.util.dp2px
import com.wind.vpn.util.periodToDesc
import com.wind.vpn.widget.MenuInfo
import com.wind.vpn.widget.MenuView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val KEY_PAY_METHOD = "key_pay_method"
const val KEY_ORDER_DETAIL = "key_trade_no"
const val KEY_CURRENCY_SYMBOL = "key_currency_symbol"

class PayCheckoutActivity : BaseActivity() {
    private lateinit var binding: ActSaveOrderBinding
    private var paySymbol: String? = null
    private var payMethods: List<PayMethod>? = null;
    private var orderDetail: OrderInfo? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!iniData()) {
            return
        }
        bindView()
    }

    private var isFirstResume = true;
    override fun onResume() {
        super.onResume()
        if (!isFirstResume && payClicked) {
            checkOrderState()
        }
        isFirstResume = false
    }

    private fun iniData(): Boolean {
        payMethods = intent.getStringExtra(KEY_PAY_METHOD)?.toBean<List<PayMethod>>()
        orderDetail = intent.getStringExtra(KEY_ORDER_DETAIL)?.toBean<OrderInfo>()
        paySymbol = intent.getStringExtra(KEY_CURRENCY_SYMBOL)
        return if (orderDetail == null || payMethods.isNullOrEmpty() || paySymbol.isNullOrEmpty()) {
            onBackPressed()
            showToast(getString(R.string.toast_error))
            false
        } else {
            true
        }
    }


    private fun checkOrderState() {
        showLoading()
        launch(Dispatchers.IO) {
            var result = WindApi.checkOrderStatus(orderDetail!!.trade_no!!)
            withContext(Dispatchers.Main) {
                if (result.isSuccess && result.data != 0) {
                    showToast(getString(R.string.toast_pay_suc))
                    finish()
                }
                hideLoading()
            }

        }
    }

    override fun genCustomView(): View {
        binding =
            ActSaveOrderBinding.inflate(layoutInflater, findViewById(android.R.id.content), false)
        return binding.root
    }

    override fun getTopTitle(): Int {
        return R.string.act_title_pay_order
    }

    private fun bindView() {
        binding.tvOrderTitle.text =
            "${orderDetail!!.plan?.name} ${periodToDesc(orderDetail!!.period!!)}"
        binding.tvPayPrice.text = "$paySymbol ${centToYuan(orderDetail!!.total_amount)}"
        bindPayMethodView()
        binding.btnPayCheckout.setOnClickListener {
            startCheckout()
        }
        bindOrderDetail()
        binding.tvBottomTips.buildSupperLinkSpan(
            "${getString(R.string.charge_bottom_tips_prefix)} ${
                getString(
                    R.string.charge_bottom_tips_suffix
                )
            }", getString(R.string.charge_bottom_tips_suffix), "crisp://", true
        )
    }

    private fun bindOrderDetail() {
        binding.llOrderDetail.removeAllViews()
        var menuView: MenuView? = null
        if (orderDetail!!.balance_amount > 0) {
            menuView = MenuView(this)
            menuView.menuIcon.visibility = View.GONE
            menuView.setMenu(MenuInfo(R.drawable.icon_device, R.string.balance_amount))
            menuView.setRight("-$paySymbol ${centToYuan(orderDetail!!.balance_amount)}")
            binding.llOrderDetail.addView(menuView, LinearLayout.LayoutParams(-1, dp2px(40f)))
        }
        if (orderDetail!!.discount_amount > 0) {
            menuView = MenuView(this)
            menuView.menuIcon.visibility = View.GONE
            menuView.setMenu(MenuInfo(R.drawable.icon_device, R.string.discount_amount))
            menuView.setRight("-$paySymbol ${centToYuan(orderDetail!!.discount_amount)}")
            menuView.menuRight.setTextColor(Color.parseColor("#ff8c00"))
            binding.llOrderDetail.addView(menuView, LinearLayout.LayoutParams(-1, dp2px(40f)))
        }
        if (binding.llOrderDetail.childCount > 0) {
            menuView = MenuView(this)
            menuView.menuIcon.visibility = View.GONE
            menuView.setMenu(MenuInfo(R.drawable.icon_device, R.string.total_amount))
            val period = orderDetail!!.period
            val count: Long = orderDetail!!.plan!!.getPropertyValue(period!!) as Long
            menuView.setRight("$paySymbol ${centToYuan(count)}")
            binding.llOrderDetail.addView(menuView, 0, LinearLayout.LayoutParams(-1, dp2px(40f)))
        }
    }

    private var selectMethod = 0;
    private fun bindPayMethodView() {
        for ((index, value) in payMethods!!.withIndex()) {
            val itemBinding =
                ViewPaymentItemBinding.inflate(layoutInflater, binding.llPayment, false)
            itemBinding.tvSubTitle.text = value.name
            Glide.with(this@PayCheckoutActivity).load(value.icon).into(itemBinding.ivPrefix)
            if (index == 0) {
                itemBinding.cbSuffix.isChecked = true
            }
            binding.llPayment.addView(itemBinding.root)
            itemBinding.apply {
                root.setOnClickListener {
                    if (selectMethod != index) {
                        binding.llPayment.getChildAt(selectMethod)
                            .findViewById<CheckBox>(R.id.cb_suffix).isChecked = false
                        selectMethod = index
                        cbSuffix.isChecked = true
                    }

                }

            }

        }
    }

    private var payClicked = false;
    private fun startCheckout() {
        payClicked = true;
        showLoading()
        launch(Dispatchers.IO) {
            val checkResult =
                WindApi.checkoutOrder(orderDetail!!.trade_no!!, payMethods!![selectMethod].id)
            if (checkResult.isSuccess && !checkResult.data.isNullOrEmpty()) {
                if (checkResult.data == "true" && checkResult.type == -1) {
                    checkOrderState()
                } else {
                    startLoadUrl(checkResult.data!!)
                }
            }
            withContext(Dispatchers.Main) {
                hideLoading()
            }
        }

    }


}

fun goPayCheckActivity(
    context: Context,
    orderDetail: String,
    currency_symbol: String,
    payMethods: List<PayMethod>
) {
    val intent = Intent(context, PayCheckoutActivity::class.java)
    intent.putExtra(KEY_CURRENCY_SYMBOL, currency_symbol)
    intent.putExtra(KEY_ORDER_DETAIL, orderDetail)
    intent.putExtra(KEY_PAY_METHOD, payMethods.toJson())
    if (context !is Activity) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
}