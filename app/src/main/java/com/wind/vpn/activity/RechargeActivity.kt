package com.wind.vpn.activity

import android.graphics.Color
import android.graphics.Typeface
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.github.kr328.clash.R
import com.github.kr328.clash.databinding.ActMemberSelectBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.wind.vpn.PlanFragment
import com.wind.vpn.bean.BaseBean
import com.wind.vpn.data.CommConfMgr
import com.wind.vpn.data.WindApi
import com.wind.vpn.data.bean.CommConfig
import com.wind.vpn.data.bean.WindPlan
import com.wind.vpn.util.buildSupperLinkSpan
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.concurrent.thread


class RechargeActivity:BaseActivity() {
    private lateinit var binding: ActMemberSelectBinding
    private lateinit var planList:List<WindPlan>
    private lateinit var commConfig: CommConfig
    override fun genCustomView(): View {
        binding = ActMemberSelectBinding.inflate(layoutInflater, findViewById(android.R.id.content), false)
        return binding.root
    }

    override fun getTopTitle(): Int {
        return R.string.act_title_member_select
    }

    override fun initView() {
        super.initView()
        loadData()
        binding.tvBottomTips.buildSupperLinkSpan(
            "${getString(R.string.charge_bottom_tips_prefix)} ${
                getString(
                    R.string.charge_bottom_tips_suffix
                )
            }", getString(R.string.charge_bottom_tips_suffix), "crisp://", true
        )
        binding.tabBarPlan.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                launch(Dispatchers.IO){
                    delay(100)
                    withContext(Dispatchers.Main) {
                        val tabText = (tab!!.view as ViewGroup).getChildAt(1) as TextView
                        tabText.typeface = Typeface.DEFAULT_BOLD
                        tabText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f)
                        tabText.setTextColor(Color.parseColor("#198CFF"))
                    }
                }

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                launch(Dispatchers.IO) {
                    delay(100)
                    withContext(Dispatchers.Main) {
                        val tabText = (tab!!.view as ViewGroup).getChildAt(1) as TextView
                        tabText.typeface = Typeface.DEFAULT
                        tabText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12f)
                        tabText.setTextColor(Color.parseColor("#9C9C9C"))
                    }
                }

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })
    }

    private fun fillData() {
        binding.viewPagerPlan.adapter = MyAdapter(supportFragmentManager)
        binding.tabBarPlan.setupWithViewPager(binding.viewPagerPlan)
    }

    private fun loadData() {
        launch(Dispatchers.IO) {
            var plan: BaseBean<List<WindPlan>>? = null
            var planConf: CommConfig? = null
            var task1 = thread { plan = WindApi.fetchWindPackage() }
            var task2 = thread { planConf = CommConfMgr.loadCommConf() }
            task1.join()
            task2.join()
            withContext(Dispatchers.Main) {
                if (plan?.isSuccess == true && !planConf?.currency_symbol.isNullOrEmpty()) {
                    planList = plan!!.data!!
                    commConfig = planConf!!
                    fillData()
                } else {
                    showToast(getString(R.string.toast_error))
                }
            }

        }

    }

    inner class MyAdapter(fragmentManager: FragmentManager) :
        FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            private val fragments = HashMap<Int, Fragment>()
        override fun getCount(): Int {
            return planList.size
        }

        override fun getItem(position: Int): Fragment {
            if (fragments[position] == null) {
                val fragment = PlanFragment.newInstance(planList[position], commConfig)
                fragments[position] = fragment
            }
            return fragments[position]!!
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return planList[position].name
        }
    }
}
