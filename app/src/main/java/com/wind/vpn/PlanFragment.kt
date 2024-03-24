package com.wind.vpn

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.github.kr328.clash.R
import com.github.kr328.clash.databinding.FragmentPlanBinding
import com.wind.vpn.activity.RechargeActivity
import com.wind.vpn.activity.goPayCheckActivity
import com.wind.vpn.activity.showToast
import com.wind.vpn.bean.BaseBean
import com.wind.vpn.bean.NET_ERR
import com.wind.vpn.data.WindApi
import com.wind.vpn.data.bean.CommConfig
import com.wind.vpn.data.bean.CouponBean
import com.wind.vpn.data.bean.WindPlan
import com.wind.vpn.data.getErrMsg
import com.wind.vpn.holder.BaseHolder
import com.wind.vpn.util.centToYuan
import com.wind.vpn.util.dp2px
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToLong


class PlanFragment(private val plan: WindPlan, val conf: CommConfig) : Fragment(),
    CoroutineScope by CoroutineScope(Dispatchers.IO) {
    private val TYPE_CONENT = 0
    private var TYPE_PLAN_ITEM = 1
    private val showItems = mutableListOf<PlanItem>()
    private lateinit var binding: FragmentPlanBinding
    private lateinit var activity: RechargeActivity
    private var couponBean: BaseBean<CouponBean> = BaseBean()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = getActivity() as RechargeActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fillData()
        val layoutManager = GridLayoutManager(context, 2)
        layoutManager.spanSizeLookup = Lookup()
        binding.recyclerList.layoutManager = layoutManager
        binding.recyclerList.addItemDecoration(
            MyDecoration(
                context!!,
                DividerItemDecoration.VERTICAL
            )
        )
        binding.recyclerList.adapter = MyAdapter()
        initCouponHandle()
    }

    private fun initCouponHandle() {
        updateCouponUI()
        binding.btnVerify.text = getString(R.string.coupon_btn_verify)
        binding.btnVerify.setOnClickListener {
            if (!binding.etCoupon.isEnabled) {
                couponBean = BaseBean()
                binding.recyclerList.adapter?.notifyDataSetChanged()
                binding.etCoupon.setText("")
                updateCouponUI()
            } else {
                val code = binding.etCoupon.text
                if (!code.isNullOrEmpty()){
                    activity.showLoading()
                    launch { val result = WindApi.verifyCoupon(code.toString(), plan.id)
                        withContext(Dispatchers.Main) {
                            activity.hideLoading()
                            if (result.isSuccess) {
                                couponBean = result
                                binding.recyclerList.adapter?.notifyDataSetChanged()
                            } else {
                                activity.showToast(result.getErrMsg())
                                if (result.retCode != NET_ERR) {
                                    binding.etCoupon.setText("")
                                }
                            }
                            updateCouponUI()
                        }

                    }
                }

            }
        }
    }

    private fun updateCouponUI() {
        if (couponBean.isSuccess) {
            binding.btnVerify.text = getString(R.string.coupon_btn_clear)
            binding.etCoupon.isEnabled = false
        } else {
            binding.btnVerify.text = getString(R.string.coupon_btn_verify)
            binding.etCoupon.isEnabled = true
        }
    }

    private fun genOrder(period: String, price: Long) {
        activity.showLoading()
        launch {
            val payMethods = WindApi.getPayMethod()
            WindApi.cancelExistOrder()
            if (payMethods.isSuccess && payMethods.data != null && payMethods.data!!.isNotEmpty()) {
                val order = WindApi.saveOrder(period, plan.id, couponBean.data?.code)
                if (order.isSuccess) {
                    val orderResult = WindApi.getOrderDetail(order.data!!)
                    if (orderResult.isSuccess) {
                        context?.let {
                            goPayCheckActivity(
                                it,
                                orderResult.data.toJson(),
                                conf.currency_symbol!!,
                                payMethods.data!!
                            )
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            activity.showToast(getString(R.string.toast_error))
                        }
                    }

                } else {
                    withContext(Dispatchers.Main) {
                        activity.showToast(order.getErrMsg())
                    }
                }
            }
            withContext(Dispatchers.Main) {
                activity.hideLoading()
            }
        }
    }

    private fun fillData() {
        var item: PlanItem
        showItems.clear()
        if (!plan.content.isNullOrEmpty()) {
            item = PlanItem(TYPE_CONENT)
            item.content = plan.content
            showItems.add(item)
        }

        if (plan.month_price > 0) {
            item = PlanItem(TYPE_PLAN_ITEM)
            item.price = plan.month_price
            item.period = "month_price"
            item.periodStr = getString(R.string.month_price)
            showItems.add(item)
        }
        if (plan.quarter_price > 0) {
            item = PlanItem(TYPE_PLAN_ITEM)
            item.price = plan.quarter_price
            item.period = "quarter_price"
            item.periodStr = getString(R.string.quarter_price)
            showItems.add(item)
        }
        if (plan.half_year_price > 0) {
            item = PlanItem(TYPE_PLAN_ITEM)
            item.price = plan.half_year_price
            item.period = "half_year_price"
            item.periodStr = getString(R.string.half_year_price)
            showItems.add(item)
        }
        if (plan.year_price > 0) {
            item = PlanItem(TYPE_PLAN_ITEM)
            item.price = plan.year_price
            item.period = "year_price"
            item.periodStr = getString(R.string.year_price)
            showItems.add(item)
        }
        if (plan.two_year_price > 0) {
            item = PlanItem(TYPE_PLAN_ITEM)
            item.price = plan.two_year_price
            item.period = "two_year_price"
            item.periodStr = getString(R.string.two_year_price)
            showItems.add(item)
        }
        if (plan.three_year_price > 0) {
            item = PlanItem(TYPE_PLAN_ITEM)
            item.price = plan.three_year_price
            item.period = "three_year_price"
            item.periodStr = getString(R.string.three_year_price)
            showItems.add(item)
        }
        if (plan.onetime_price > 0) {
            item = PlanItem(TYPE_PLAN_ITEM)
            item.price = plan.onetime_price
            item.period = "onetime_price"
            item.periodStr = getString(R.string.onetime_price)
            showItems.add(item)
        }

    }

    inner class PlanHolder(private var customView: View) : BaseHolder<PlanItem>(customView) {
        private val tvPeriod: TextView = customView.findViewById(R.id.tv_plan_peroid)
        private val tvPrice: TextView = customView.findViewById(R.id.tv_plan_price)
        private val tvDiscount: TextView = customView.findViewById(R.id.tv_discount)
        override fun onHolderBind(t: PlanItem) {
            var destPrice = t.price
            var discount = 0L
            if (couponBean.isSuccess && couponBean.data?.show == 1) {
                val coupon = couponBean.data!!
                if (coupon.type == 1) {
                    destPrice = t.price - coupon.value
                    discount = coupon.value
                } else if (coupon.type == 2) {
                    discount = (t.price.toDouble()*coupon.value.toDouble()/100.00).roundToLong()
                    destPrice = t.price - discount
                }
            }
            if (destPrice < 0) {
                destPrice = 0
            }
            if (discount > t.price) {
                discount = t.price
            }
            tvPeriod.text = t.periodStr
            tvPrice.text = "${conf.currency_symbol} ${centToYuan(destPrice)}"
            if (discount > 0) {
                tvDiscount.visibility = View.VISIBLE
                tvDiscount.text = "${conf.currency_symbol} ${centToYuan(t.price)} ${getString(R.string.coupon_discount)} ${centToYuan(discount)}"
            } else {
                tvDiscount.visibility = View.GONE
            }
            customView.setOnClickListener {
                genOrder(t.period, t.price)
            }
        }

    }

    inner class ContentHolder(private val textView: View) : BaseHolder<PlanItem>(textView) {
        override fun onHolderBind(t: PlanItem) {
            (textView as TextView).text = t.content
        }

    }

    inner class MyAdapter : Adapter<BaseHolder<PlanItem>>() {
        override fun getItemViewType(position: Int): Int {
            return showItems[position].type
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<PlanItem> {
            return if (viewType == TYPE_CONENT) {
                ContentHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.view_item_plan_content, parent, false)
                )
            } else {
                PlanHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.view_item_plan_meta, parent, false)
                )
            }
        }

        override fun onBindViewHolder(holder: BaseHolder<PlanItem>, position: Int) {
            holder.onHolderBind(showItems[position])
        }

        override fun getItemCount(): Int {
            return showItems.size
        }

    }

    class Lookup : SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            return if (position == 0) 2 else 1
        }

    }

    class MyDecoration(private val context: Context, orientation: Int) : ItemDecoration() {
        private val topSize = dp2px(12f)
        private val lrSize = dp2px(8f)
        override fun getItemOffsets(outRect: Rect, itemPosition: Int, parent: RecyclerView) {
            if (itemPosition > 0) {
                if (itemPosition % 2 == 1) {
                    outRect.right = lrSize
                } else {
                    outRect.left = lrSize
                }
            }
            outRect.top = topSize

        }

    }

    companion object {
        @JvmStatic
        fun newInstance(param1: WindPlan, param2: CommConfig) =
            PlanFragment(param1, param2)
    }

    class PlanItem(val type: Int) {
        var price = 0L;
        var content: String? = ""
        var period: String = ""
        var periodStr: String = ""
    }

}