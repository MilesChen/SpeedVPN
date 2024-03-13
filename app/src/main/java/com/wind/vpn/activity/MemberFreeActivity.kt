package com.wind.vpn.activity

import android.view.View
import com.github.kr328.clash.R
import com.wind.vpn.util.goTargetClass

class MemberFreeActivity:BaseActivity() {
    override fun getLayoutResId(): Int {
        return R.layout.act_member_free
    }

    override fun getTopTitle(): Int {
        return R.string.act_title_membership
    }

    override fun initView() {
        super.initView()
        val btn = findViewById<View>(R.id.btn_try_luck)
        btn.setOnClickListener{
            goTargetClass(this, FillFriendIdActivity::class.java)
        }
    }
}