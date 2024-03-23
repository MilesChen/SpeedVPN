package com.wind.vpn.activity

import com.github.kr328.clash.AccessControlActivity
import com.wind.vpn.design.NetworkControllerDesign
import com.github.kr328.clash.BaseActivity
import com.github.kr328.clash.common.util.intent
import com.github.kr328.clash.design.NetworkSettingsDesign
import com.github.kr328.clash.service.store.ServiceStore
import kotlinx.coroutines.isActive
import kotlinx.coroutines.selects.select

class AppSettingsActivity:BaseActivity<NetworkControllerDesign>() {
    override suspend fun main() {
        val design = NetworkControllerDesign(
            this,
            uiStore,
            ServiceStore(this),
            clashRunning,
        )
        setContentDesign(design)
        while (isActive) {
            select<Unit> {
                events.onReceive {
                    when (it) {
                        Event.ClashStart, Event.ClashStop, Event.ServiceRecreated ->
                            recreate()
                        else -> Unit
                    }
                }
                design.requests.onReceive {
                    when (it) {
                        NetworkSettingsDesign.Request.StartAccessControlList ->
                            startActivity(AccessControlActivity::class.intent)
                    }
                }
            }
        }
    }
}