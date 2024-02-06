package com.wind.vpn.activity

import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.kr328.clash.R

class ChangeCountryActivity:BaseActivity() {
    private lateinit var switchBox: Switch
    private lateinit var leftText: TextView
    private lateinit var rightText: TextView
    private lateinit var recyclerView: RecyclerView
    override fun getLayoutResId(): Int {
        return R.layout.act_change_country
    }

    override fun getTopTitle(): Int {
        return R.string.act_title_change_country
    }

    override fun initView() {
        super.initView()
        switchBox = findViewById(R.id.speed_mode_switch)
        leftText = findViewById(R.id.tv_left_safe)
        rightText = findViewById(R.id.tv_right_speed)
        recyclerView = findViewById(R.id.country_recycler)
        switchBox.setOnCheckedChangeListener { buttonView, isChecked ->
            run {
                leftText.isSelected = !isChecked
                rightText.isSelected = isChecked
            }
        }
    }
}