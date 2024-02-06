package com.wind.vpn.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.github.kr328.clash.R
import com.wind.vpn.activity.ChangeCountryActivity
import com.wind.vpn.activity.MemberFreeActivity
import com.wind.vpn.activity.MessageActivity
import com.wind.vpn.activity.OnlineServiceActivity
import com.wind.vpn.activity.PlayingActivity
import com.wind.vpn.activity.RecommendActivity
import com.wind.vpn.activity.RegisterActivity
import com.wind.vpn.activity.SafeLossActivity
import com.wind.vpn.activity.UploadLogActivity
import com.wind.vpn.activity.UserCenterActivity
import com.wind.vpn.util.goTargetClass

val MENU_ICONS = intArrayOf(
    R.drawable.icon_menu_1_country,
    R.drawable.icon_menu_2_reward,
    R.drawable.icon_menu_3_membership,
    R.drawable.icon_menu_4_play,
    R.drawable.icon_menu_5_center,
    R.drawable.icon_menu_6_safe,
    R.drawable.icon_menu_7_service,
    R.drawable.icon_menu_8_log
)

val MENU_TEXTS = intArrayOf(
    R.string.menu_1,
    R.string.menu_2,
    R.string.menu_3,
    R.string.menu_4,
    R.string.menu_5,
    R.string.menu_6,
    R.string.menu_7,
    R.string.menu_8
)

val TARGET_CLASS = arrayListOf<Class<*>>(
    ChangeCountryActivity::class.java,
    RecommendActivity::class.java,
    MemberFreeActivity::class.java,
    PlayingActivity::class.java,
    MessageActivity::class.java,
    SafeLossActivity::class.java,
    OnlineServiceActivity::class.java,
    UploadLogActivity::class.java
)

class DrawerView : ConstraintLayout,OnClickListener{
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
        LayoutInflater.from(context).inflate(R.layout.nav_header, this, true)
        val headView = findViewById<View>(R.id.drawer_header)
        headView.setOnClickListener {
            if (true) {
                goTargetClass(context, RegisterActivity::class.java)
            } else {
                goTargetClass(context, UserCenterActivity::class.java)
            }

        }
        var menuContainer = findViewById<LinearLayout>(R.id.layout_menu)
        for ((i, id) in MENU_ICONS.withIndex()) {
            val menuInfo = MenuInfo(id, MENU_TEXTS[i])
            val menuView = MenuView(context)
            menuView.setMenu(menuInfo)
            menuView.setOnClickListener(this)
            menuView.tag = i
            menuContainer.addView(menuView)
        }
    }


    override fun onClick(v: View?) {
        var i = 0;
        i = v?.tag as Int
        goTargetClass(context, TARGET_CLASS[i])
    }
}