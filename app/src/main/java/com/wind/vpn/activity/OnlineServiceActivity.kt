package com.wind.vpn.activity

import com.github.kr328.clash.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class OnlineServiceActivity:BaseActivity() {
    override fun getLayoutResId(): Int {
        return R.layout.act_recommend
    }

    override fun getTopTitle(): Int {
        return R.string.act_title_recommend
    }
}