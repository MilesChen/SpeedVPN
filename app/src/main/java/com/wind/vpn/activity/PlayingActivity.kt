package com.wind.vpn.activity

import com.github.kr328.clash.R

class PlayingActivity:BaseActivity() {
    override fun getLayoutResId(): Int {
        return R.layout.act_recommend
    }

    override fun getTopTitle(): Int {
        return R.string.act_title_recommend
    }
}