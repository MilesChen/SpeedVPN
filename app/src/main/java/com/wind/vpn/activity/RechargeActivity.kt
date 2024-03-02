package com.wind.vpn.activity

import com.github.kr328.clash.R

class RechargeActivity:BaseActivity() {
    override fun getLayoutResId(): Int {
        return R.layout.act_member_select
    }

    override fun getTopTitle(): Int {
        return R.string.act_title_member_select
    }
}