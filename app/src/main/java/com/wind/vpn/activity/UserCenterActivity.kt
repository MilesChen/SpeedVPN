package com.wind.vpn.activity

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.github.kr328.clash.R
import com.github.kr328.clash.databinding.ActUserCenterBinding
import com.github.kr328.clash.databinding.ActUserCenterBindingImpl
import com.wind.vpn.WindGlobal
import com.wind.vpn.data.CommConfMgr
import com.wind.vpn.design.one_day
import com.wind.vpn.design.one_hour
import com.wind.vpn.design.one_minute
import com.wind.vpn.util.centToYuan
import com.wind.vpn.widget.MenuInfo
import com.wind.vpn.widget.MenuView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserCenterActivity : BaseActivity() {
    private lateinit var binding: ActUserCenterBinding
    private var isPwdShow = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun genCustomView(): View {
        binding =
            ActUserCenterBinding.inflate(layoutInflater, findViewById(android.R.id.content), false)
        return binding.root
    }

    override fun initView() {
        super.initView()
        binding.viewMemberLevel.setMenu(MenuInfo(R.drawable.icon_vip, R.string.user_center_level))
        binding.viewMemberLevel.setRight(WindGlobal.subscribe?.plan?.name?:"")
        binding.viewRemainTime.setMenu(
            MenuInfo(
                R.drawable.icon_remain_time,
                R.string.user_center_remain
            )
        )
        binding.viewRemainTime.setRight(formatRemainTime())
        binding.device.setMenu(MenuInfo(R.drawable.icon_device, R.string.user_center_device))
        binding.device.setRight("Android")
        updateBalance()
        binding.tvName.text = WindGlobal.account.email
        binding.tvUserPwd.text = WindGlobal.account.pwd
        setShowPwd()
        binding.ivPwdShow.setOnClickListener {
            isPwdShow = !isPwdShow
            setShowPwd()
        }
    }

    private fun updateBalance() {
        binding.balance.setMenu(MenuInfo(R.drawable.icon_balance, R.string.user_center_balance))
        launch(Dispatchers.IO) {
            val commConfig = CommConfMgr.loadCommConf(false)
            withContext(Dispatchers.Main) {
                binding.balance.setRight(
                    if (!commConfig?.currency_symbol.isNullOrEmpty()) "${commConfig!!.currency_symbol} ${
                        centToYuan(
                            WindGlobal.userInfo.balance
                        )
                    }" else getString(R.string.toast_error)
                )
            }
        }
    }

    private fun formatRemainTime(): String {
        val expire = WindGlobal.userInfo.expired_at * 1000L
        var timeSpace = expire - System.currentTimeMillis()
        timeSpace = if (timeSpace < 0) 0L else timeSpace
        if (timeSpace > one_day) {
            return "${timeSpace / one_day} ${getString(R.string.days)}"
        } else if (timeSpace > one_hour) {
            return "${timeSpace / one_hour} ${getString(R.string.hours)}"
        } else {
            return "${timeSpace / one_minute} ${getString(R.string.minutes)}"
        }
    }

    private fun setShowPwd() {
        if (isPwdShow) {
            binding.tvUserPwd.transformationMethod = HideReturnsTransformationMethod.getInstance()
            binding.ivPwdShow.setImageResource(R.drawable.icon_pw_show)
        } else {
            binding.tvUserPwd.transformationMethod = PasswordTransformationMethod.getInstance()
            binding.ivPwdShow.setImageResource(R.drawable.icon_pw_hide)
        }
    }

    override fun getTopTitle(): Int {
        return R.string.act_title_user_center
    }
}