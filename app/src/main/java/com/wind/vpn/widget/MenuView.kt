package com.wind.vpn.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.github.kr328.clash.R

class MenuView : RelativeLayout {
    private lateinit var menuIcon: ImageView
    private lateinit var menuText: TextView
    private lateinit var menuInfo: MenuInfo
    private lateinit var menuRight: TextView

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    private fun init(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.view_menu_item, this, true)
        menuIcon = findViewById(R.id.icon_menu)
        menuText = findViewById(R.id.tv_menu)
        menuRight = findViewById(R.id.tv_right)
    }

    fun setMenu(info: MenuInfo) {
        menuInfo = info
        menuIcon?.setImageResource(menuInfo.iconId)
        menuText?.setText(menuInfo.text)
    }

    fun setRight(text: String) {
        menuRight.text = text
    }

}

data class MenuInfo(var iconId: Int, var text: Int)