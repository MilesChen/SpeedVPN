package com.wind.vpn.holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder

abstract class BaseHolder<T>(view: View):ViewHolder(view) {
    abstract fun onHolderBind(t:T)
}