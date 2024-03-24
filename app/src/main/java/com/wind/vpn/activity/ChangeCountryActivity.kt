package com.wind.vpn.activity

import android.graphics.Rect
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.kr328.clash.R
import com.github.kr328.clash.common.Global
import com.github.kr328.clash.core.Clash
import com.github.kr328.clash.core.model.ProxySort
import com.github.kr328.clash.core.model.TunnelState
import com.github.kr328.clash.databinding.DesignCountryChangeBinding
import com.github.kr328.clash.design.store.UiStore
import com.github.kr328.clash.util.withClash
import com.wind.vpn.adapter.CountryAdapter
import com.wind.vpn.util.dp2px
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ChangeCountryActivity : BaseActivity() {
    private lateinit var binding: DesignCountryChangeBinding
    private val uiStore = UiStore(Global.application)
    private lateinit var name: String

    override fun genCustomView(): View {
        binding = DesignCountryChangeBinding.inflate(
            layoutInflater,
            findViewById(android.R.id.content),
            false
        )
        return binding.root
    }

    override fun getTopTitle(): Int {
        return R.string.act_title_change_country
    }

    override fun initView() {
        super.initView()
        initData()
    }

    private fun initData() {
        binding.speedModeSwitch.isChecked = uiStore.tunnelMode== TunnelState.Mode.Global
        binding.tvLeftSafe.isSelected = binding.speedModeSwitch.isChecked
        binding.tvRightSpeed.isSelected = !binding.speedModeSwitch.isChecked
        binding.speedModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            run {
                binding.tvLeftSafe.isSelected = isChecked
                binding.tvRightSpeed.isSelected = !isChecked
                launch(Dispatchers.IO) {
                    withClash {
                        val o = queryOverride(Clash.OverrideSlot.Session)
                        val targetMode =
                            if (isChecked) TunnelState.Mode.Global else TunnelState.Mode.Rule
                        o.mode = targetMode
                        patchOverride(Clash.OverrideSlot.Session, o)
                        uiStore.tunnelMode = targetMode
                        delay(1000)
                        val names = withClash { queryProxyGroupNames(false) }
                        name = names[0]
                        patchSelector(name, uiStore.proxyLastName)
                    }
                }

            }

        }
        launch(Dispatchers.IO) {
            val names = withClash { queryProxyGroupNames(false) }
            name = names[0]
            val queryName = if (uiStore.tunnelMode == TunnelState.Mode.Global) names[1] else names[0]
            val group = withClash { queryProxyGroup(queryName, ProxySort.Default) }
            withContext(Dispatchers.Main) {
                binding.countryRecycler.layoutManager =
                    LinearLayoutManager(this@ChangeCountryActivity)
                binding.countryRecycler.addItemDecoration(SpaceItemDecoration())
                binding.countryRecycler.adapter = CountryAdapter(group, ::onItemClick)
            }

        }
    }


    private fun onItemClick(proxyName: String) {
        launch {
            withClash {
                patchSelector(name, proxyName)
                uiStore.proxyLastName = proxyName
            }
        }

    }
}

class SpaceItemDecoration() : RecyclerView.ItemDecoration() {
    private val topSize = dp2px(12f)
    private val divider = dp2px(8f)
    private val bottomSize = dp2px(44f)
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
        } else if (position == parent.adapter!!.itemCount - 1) {
            outRect.top = 0
            outRect.bottom = bottomSize
        } else {
            outRect.top = 0;
            outRect.bottom = divider
        }
    }
}