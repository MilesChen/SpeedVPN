package com.wind.vpn.holder

import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.kr328.clash.R
import com.github.kr328.clash.core.model.Proxy
import com.github.kr328.clash.core.model.ProxyGroup

class CountryHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val title: TextView = view.findViewById(R.id.tv_country_name)
    private val checkBox: CheckBox = view.findViewById(R.id.cb_country)
    fun onBind(group: ProxyGroup, proxy: Proxy) {
        checkBox.isChecked = group.now == proxy.name
        title.text = proxy.title
    }

}