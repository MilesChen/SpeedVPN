package com.wind.vpn.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.github.kr328.clash.R
import com.wind.vpn.WindGlobal
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
import java.text.SimpleDateFormat
import java.util.*

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

class DrawerView : ConstraintLayout, OnClickListener {
    lateinit var tvUserName: TextView

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
        val header = findViewById<View>(R.id.drawer_header)
        tvUserName = findViewById(R.id.tv_name)
        header.setOnClickListener {
            if (true) {
                goTargetClass(context, RegisterActivity::class.java)
            } else {
                goTargetClass(context, UserCenterActivity::class.java)
            }

        }
        val layoutMenu: LinearLayout = findViewById(R.id.layout_menu)
        for ((i, id) in MENU_ICONS.withIndex()) {
            val menuInfo = MenuInfo(id, MENU_TEXTS[i])
            val menuView = MenuView(context)
            menuView.setMenu(menuInfo)
            menuView.setOnClickListener(this)
            menuView.tag = i
            layoutMenu.addView(menuView)
        }
        updateAccount()

    }

    fun setExpire(expire: Long) {
        val tvExpire: TextView = findViewById(R.id.tv_expire)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        tvExpire.text = context.getString(R.string.account_expire_time, dateFormat.format(expire))
    }

    override fun onClick(v: View?) {
        var i = v?.tag as Int
        goTargetClass(context, TARGET_CLASS[i])
    }

    fun updateAccount() {
        if (WindGlobal.account.isLogin()) {
            tvUserName.text = WindGlobal.account.email
        } else {
            tvUserName.setText(R.string.account_register_login)
        }
    }
}