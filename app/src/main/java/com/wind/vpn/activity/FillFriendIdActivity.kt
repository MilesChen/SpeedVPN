package com.wind.vpn.activity

import android.view.View
import com.github.kr328.clash.R
import com.wind.vpn.util.goTargetClass

class FillFriendIdActivity:BaseActivity() {
    override fun getLayoutResId(): Int {
        return R.layout.act_fill_id
    }

    override fun getTopTitle(): Int {
        return R.string.act_title_membership
    }

    override fun initView() {
        super.initView()
        val btn = findViewById<View>(R.id.btn_fill_id)
        btn.setOnClickListener{

        }
    }
}