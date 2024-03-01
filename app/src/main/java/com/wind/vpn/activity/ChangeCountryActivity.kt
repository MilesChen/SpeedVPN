package com.wind.vpn.activity

import com.github.kr328.clash.BaseActivity
import com.github.kr328.clash.core.Clash
import com.github.kr328.clash.core.model.ProxySort
import com.github.kr328.clash.design.model.ProxyState
import com.github.kr328.clash.util.withClash
import com.wind.vpn.design.CountryChangeDesign
import kotlinx.coroutines.isActive
import kotlinx.coroutines.selects.select

class ChangeCountryActivity:BaseActivity<CountryChangeDesign>() {
    override suspend fun main() {
        val mode = withClash { queryOverride(Clash.OverrideSlot.Session).mode }
        val names = withClash { queryProxyGroupNames(false) }
        val group = withClash { queryProxyGroup(names[0], ProxySort.Default) }
        val states = List(names.size) { ProxyState("?") }
        var design = CountryChangeDesign(this, names[0], group, uiStore)
        setContentDesign(design)
        while (isActive) {
            select<Unit> {  }
        }
    }
}