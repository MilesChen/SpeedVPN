package com.wind.vpn.activity

import android.os.Bundle
import android.view.Gravity
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.github.kr328.clash.R
import com.wind.vpn.util.dp2px

val noticeIcon: IntArray = intArrayOf(
    R.drawable.icon_top_0,
    R.drawable.icon_top_1,
    R.drawable.icon_top_2,
    R.drawable.icon_top_3,
    R.drawable.icon_top_4,
    R.drawable.icon_top_5,
    R.drawable.icon_top_6
)

class HomeActivity : BaseActivity() {
    private lateinit var drawerLayout: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
    }

    override fun initView() {
        drawerLayout = findViewById(R.id.drawer_layout)
        val iconsView = findViewById<RelativeLayout>(R.id.icons)
        val uniOffset = dp2px(this, 19f)
        for ((i, id) in noticeIcon.withIndex()) {
            val icon = ImageView(this)
            icon.setImageResource(id)
            val params = RelativeLayout.LayoutParams(dp2px(this, 24f), dp2px(this, 24f))
            params.leftMargin = i * uniOffset
            iconsView.addView(icon, params)
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers()
        } else {
            super.onBackPressed()
        }
    }

    override fun onTopBarIconClick() {
        drawerLayout.openDrawer(GravityCompat.START)
    }

    override fun getLayoutResId(): Int {
        return R.layout.act_main
    }

    override fun getToBarIcon(): Int {
        return R.drawable.icon_menu
    }

    override fun getTopTitle(): Int {
        return R.string.home_title
    }
}