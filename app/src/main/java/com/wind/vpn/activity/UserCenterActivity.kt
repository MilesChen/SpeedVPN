package com.wind.vpn.activity

import android.os.Bundle
import com.github.kr328.clash.R
import com.wind.vpn.widget.MenuInfo
import com.wind.vpn.widget.MenuView

class UserCenterActivity:BaseActivity() {
    private lateinit var levelView: MenuView;
    private lateinit var remainView: MenuView;
    private lateinit var deviceView: MenuView;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        super.initView()
        levelView = findViewById(R.id.view_member_level)
        remainView = findViewById(R.id.view_remain_time)
        deviceView = findViewById(R.id.device)
        levelView.setMenu(MenuInfo(R.drawable.icon_vip, R.string.user_center_level))
        levelView.setRight("Bronze")
        remainView.setMenu(MenuInfo(R.drawable.icon_remain_time, R.string.user_center_remain))
        remainView.setRight("60 min")
        deviceView.setMenu(MenuInfo(R.drawable.icon_device, R.string.user_center_device))
        deviceView.setRight("Android")
    }

    override fun getLayoutResId(): Int {
        return R.layout.act_user_center
    }

    override fun getTopTitle(): Int {
        return R.string.act_title_user_center
    }
}