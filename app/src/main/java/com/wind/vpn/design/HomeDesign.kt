package com.wind.vpn.design

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.view.GravityCompat
import com.github.kr328.clash.R
import com.github.kr328.clash.common.Global
import com.github.kr328.clash.core.model.ProxySort
import com.github.kr328.clash.databinding.DesignHomeBinding
import com.github.kr328.clash.design.MainDesign
import com.github.kr328.clash.design.store.UiStore
import com.github.kr328.clash.design.util.layoutInflater
import com.github.kr328.clash.design.util.root
import com.github.kr328.clash.util.withClash
import com.wind.vpn.WindGlobal
import com.wind.vpn.util.dp2px
import com.wind.vpn.widget.TopBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToLong

val noticeIcon: IntArray = intArrayOf(
    R.drawable.icon_top_0,
    R.drawable.icon_top_1,
    R.drawable.icon_top_2,
    R.drawable.icon_top_3,
    R.drawable.icon_top_4,
    R.drawable.icon_top_5,
    R.drawable.icon_top_6
)
const val one_minute = 60 * 1000L
const val one_hour = 60 * one_minute
const val one_day = 24 * one_hour

class HomeDesign(context: Context) : WindDesign<HomeDesign.Request>(context) {
    enum class Request {
        ToggleStatus,
        OpenCharge
    }

    private val binding = DesignHomeBinding.inflate(context.layoutInflater, context.root, false)
    override val topBarView: TopBar
        get() = binding.topBarView
    override val root: View
        get() = binding.root
    val uiStore = UiStore(Global.application)


    suspend fun setClashRunning(running: Boolean) {
        withContext(Dispatchers.Main) {
            binding.clashRunning = running
        }
    }

    override fun initDesign() {
        super.initDesign()
        title = R.string.home_title
        topBarIcon = R.drawable.icon_menu
        val uniOffset = dp2px(19f)
        val imageSize = dp2px(24f)
        for ((i, id) in noticeIcon.withIndex()) {
            val icon = ImageView(context)
            icon.setImageResource(id)
            val params = RelativeLayout.LayoutParams(imageSize, imageSize)
            params.leftMargin = i * uniOffset
            binding.icons.addView(icon, params)
        }
    }

    private fun setRemainTime(expireTime: Long) {
        val timeSpace = expireTime - System.currentTimeMillis()
        val result = 0
        if (timeSpace < 0) {
            binding.tvRemainTime.text = "0"
            binding.tvRemainDesc.setText(R.string.account_minutes);
            binding.tvRemainState.setText(R.string.account_expired)
            result
        } else {
            if (timeSpace > one_day) {
                binding.tvRemainTime.text = "${(timeSpace / one_day.toDouble()).roundToLong()}"
                binding.tvRemainDesc.setText(R.string.account_days)
            } else if (timeSpace > one_hour) {
                binding.tvRemainTime.text = "${(timeSpace / one_hour.toDouble()).roundToLong()}"
                binding.tvRemainDesc.setText(R.string.account_hours)
            } else {
                binding.tvRemainTime.text = "${timeSpace / one_minute + 1}"
                binding.tvRemainDesc.setText(R.string.account_minutes)
            }
            binding.tvRemainState.text = WindGlobal.subscribe?.plan?.name
        }
        binding.leftDrawer.setExpire(expireTime)
    }

    fun updateUI() {
        binding.leftDrawer.updateAccount()
        var suffix =
            if (uiStore.proxyLastName.isNullOrEmpty()) context.getString(R.string.network_suffix) else uiStore.proxyLastName
        if (binding.clashRunning) {
            launch {
                withClash {
                    delay(1000)
                    val names = queryProxyGroupNames(false)
                    if (!names.isNullOrEmpty()) {
                        val selectedProxy = queryProxyGroup(names[0], ProxySort.Default)
                        suffix = selectedProxy.now
                        uiStore.proxyLastName = suffix
                    }

                }
            }
        }

        binding.tvConnectInfo.text = "${context.getString(R.string.network_prefix)}$suffix"
        setRemainTime(WindGlobal.userInfo.expired_at * 1000)
    }

    override fun onTopBarClick() {
        binding.drawerLayout.openDrawer(GravityCompat.START)
    }

    init {
        binding.self = this
    }

    fun request(request: Request) {
        val  result = requests.trySend(request)
        Log.d("chenchao", "result of request $result")
    }


    fun overrideBackPress(): Boolean {
        if (binding.drawerLayout.isDrawerOpen(binding.leftDrawer)) {
            binding.drawerLayout.closeDrawer(binding.leftDrawer)
            return true
        } else if(binding.clashRunning){
            (context as Activity).moveTaskToBack(true)
            return true
        }
        return false
    }
}