package com.wind.vpn.design

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.kr328.clash.MainApplication
import com.github.kr328.clash.R
import com.github.kr328.clash.common.Global
import com.github.kr328.clash.core.model.ProxyGroup
import com.github.kr328.clash.core.model.TunnelState
import com.github.kr328.clash.databinding.DesignCountryChangeBinding
import com.github.kr328.clash.databinding.DesignHomeBinding
import com.github.kr328.clash.design.store.UiStore
import com.github.kr328.clash.design.util.layoutInflater
import com.github.kr328.clash.design.util.root
import com.github.kr328.clash.util.withClash
import com.wind.vpn.adapter.CountryAdapter
import com.wind.vpn.util.dp2px
import com.wind.vpn.widget.TopBar
import kotlinx.coroutines.launch

class CountryChangeDesign(
    context: Context, private val groupName:String ,private val group: ProxyGroup,
    private val uiStore: UiStore,
) : WindDesign<CountryChangeDesign.Request>(context) {
    class Request {}

    private val binding =
        DesignCountryChangeBinding.inflate(context.layoutInflater, context.root, false)
    override val topBarView: TopBar
        get() = binding.topBarView
    override val root: View
        get() = binding.root

    override fun initDesign() {
        super.initDesign()
        title = R.string.act_title_change_country
        binding.countryRecycler.layoutManager = LinearLayoutManager(context)
        binding.countryRecycler.addItemDecoration(SpaceItemDecoration(group.proxies.size))
        binding.countryRecycler.adapter = CountryAdapter(group) {proxyName ->
            run {
                launch { withClash { patchSelector(groupName, proxyName) } }
            }
        }
    }

    init {
        binding.speedModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            run {
                binding.tvLeftSafe.isSelected = !isChecked
                binding.tvRightSpeed.isSelected = isChecked
            }
        }

        binding.tvLeftSafe.isSelected = !binding.speedModeSwitch.isChecked
        binding.tvRightSpeed.isSelected = binding.speedModeSwitch.isChecked
    }

    class SpaceItemDecoration(private val length: Int) : RecyclerView.ItemDecoration() {
        private val topSize = dp2px(Global.application, 12f)
        private val divider = dp2px(Global.application, 8f)
        private val bottomSize = dp2px(Global.application, 44f)
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view)
            if (position == 0) {
                outRect.top = topSize
                outRect.bottom = divider
            } else if (position == length - 1) {
                outRect.top = 0
                outRect.bottom = bottomSize
            } else {
                outRect.top = 0;
                outRect.bottom = divider
            }
        }
    }
}