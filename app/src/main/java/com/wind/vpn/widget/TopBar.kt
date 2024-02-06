package com.wind.vpn.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.github.kr328.clash.R
import com.wind.vpn.util.dp2px
import com.wind.vpn.util.getStatusBarHeight

open class TopBar:RelativeLayout {
    lateinit var titleView: TextView
    lateinit var icon: ImageView
    lateinit var listener: TopBarListener
    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    open fun getLayoutId():Int {
        return R.layout.view_top_bar
    }

    private fun init(context: Context) {
        LayoutInflater.from(context).inflate(getLayoutId(), this, true)
        setPadding(paddingLeft, getStatusBarHeight(context), paddingRight, paddingBottom)
        titleView = findViewById(R.id.top_bar_text);
        icon = findViewById(R.id.top_bar_icon);
        icon.setOnClickListener { listener?.onIconClick() }
    }

    fun setIcon(resId: Int) {
        icon.setImageResource(resId)
    }

    fun setTitle(resId: Int) {
        if (resId <=0 ) return
        titleView.setText(resId)
    }

    fun setTopBarListener(topBarListener: TopBarListener) {
        listener = topBarListener
    }
}

interface TopBarListener {
    fun onIconClick();
}

