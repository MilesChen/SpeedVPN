package com.wind.vpn.activity

import com.github.kr328.clash.R

class SafeLossActivity:BaseActivity() {
    override fun getLayoutResId(): Int {
        return R.layout.act_safe_loss
    }

    override fun getTopTitle(): Int {
        return R.string.act_title_safe_loss
    }
}