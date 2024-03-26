package com.wind.vpn.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.kr328.clash.R
import com.github.kr328.clash.common.Global
import com.github.kr328.clash.core.model.ProxyGroup
import com.github.kr328.clash.design.store.UiStore
import com.github.kr328.clash.util.withClash
import com.wind.vpn.holder.CountryHolder

class CountryAdapter(private val group: ProxyGroup, private val clicked: (String)->Unit):RecyclerView.Adapter<CountryHolder>() {
    val uiStore = UiStore(Global.application)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_country_select_item, parent, false)
        return CountryHolder(view)
    }

    override fun getItemCount(): Int {
        return group.proxies.size
    }

    override fun onBindViewHolder(holder: CountryHolder, position: Int) {
        holder.itemView.apply {
            setOnClickListener{
                clicked(group.proxies[position].name)
                group.now = group.proxies[position].name
                notifyDataSetChanged()
            }
        }
        holder.onBind(uiStore.proxyLastName, group.proxies[position])
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }
}