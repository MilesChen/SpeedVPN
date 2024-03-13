package com.wind.vpn.design

import android.app.Activity
import android.content.Context
import com.github.kr328.clash.design.Design
import com.wind.vpn.widget.TopBar

abstract class WindDesign<R>(context: Context): Design<R>(context) {
    protected var title:Int? = 0
        set(value) {
            field = value
            value?.let { if (value > 0) {
                topBarView.setTitle(value)
            } }
        }
    protected var topBarIcon: Int? = null
        set(value) {
            field = value
            value?.let { if (value > 0) {
                topBarView.setIcon(value)
            } }
        }
    abstract val topBarView:TopBar
    open fun onTopBarClick() {
        (context as? Activity)?.finish()
    }
    override fun initDesign() {
        super.initDesign()
        topBarView.setTopBarListener{
            onTopBarClick()
        }
    }
}