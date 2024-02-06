package com.wind.vpn.activity

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.kr328.clash.R
import com.wind.vpn.widget.TopBar
import com.wind.vpn.widget.TopBarListener

open abstract class BaseActivity : AppCompatActivity(), TopBarListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.getDecorView()
            .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setBackgroundDrawable(getDrawable(R.drawable.bg_b))
        setContentView(getLayoutResId())
        var topBar: TopBar = findViewById(R.id.top_bar_view)
        topBar.setTopBarListener(this)
        topBar.setIcon(getToBarIcon())
        topBar.setTitle(getTopTitle())
        initView()
    }

    open fun initView() {

    }

    abstract fun getLayoutResId(): Int

    open fun getToBarIcon(): Int {
        return R.drawable.icon_title_back
    }

    abstract fun getTopTitle(): Int

    open fun onTopBarIconClick() {
        finish()
    }

    override fun onIconClick() {
        onTopBarIconClick()
    }
}