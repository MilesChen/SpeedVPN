package com.wind.vpn.widget

import android.content.Context
import android.util.AttributeSet
import com.github.kr328.clash.R

class TopBarHome:TopBar {
    constructor(context: Context) : super(context) {

    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    }

    override fun getLayoutId(): Int {
        return R.layout.view_top_bar_home
    }
}