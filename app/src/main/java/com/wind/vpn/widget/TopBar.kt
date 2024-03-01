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
    private lateinit var titleView: TextView
    private lateinit var icon: ImageView
    private lateinit var listener: () -> Unit
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
        icon.setImageResource(R.drawable.icon_title_back)
        icon.setOnClickListener { listener?.invoke() }
    }

    fun setIcon(resId: Int) {
        icon.setImageResource(resId)
    }

    fun setTitle(resId: Int) {
        if (resId <=0 ) return
        titleView.setText(resId)
    }

    fun setTopBarListener(topBarListener: ()->Unit) {
        listener = topBarListener
    }
}

interface TopBarListener {
    fun onIconClick();
}

