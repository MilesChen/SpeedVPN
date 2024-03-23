package com.wind.vpn.design

import android.content.Context
import android.os.Build
import android.view.View
import com.github.kr328.clash.R
import com.github.kr328.clash.databinding.DesignAppControllerBinding
import com.github.kr328.clash.design.NetworkSettingsDesign
import com.github.kr328.clash.design.preference.OnChangedListener
import com.github.kr328.clash.design.preference.Preference
import com.github.kr328.clash.design.preference.category
import com.github.kr328.clash.design.preference.clickable
import com.github.kr328.clash.design.preference.preferenceScreen
import com.github.kr328.clash.design.preference.selectableList
import com.github.kr328.clash.design.preference.switch
import com.github.kr328.clash.design.store.UiStore
import com.github.kr328.clash.design.ui.ToastDuration
import com.github.kr328.clash.design.util.applyFrom
import com.github.kr328.clash.design.util.bindAppBarElevation
import com.github.kr328.clash.design.util.layoutInflater
import com.github.kr328.clash.design.util.root
import com.github.kr328.clash.service.model.AccessControlMode
import com.github.kr328.clash.service.store.ServiceStore
import com.wind.vpn.widget.TopBar
import kotlinx.coroutines.launch

class NetworkControllerDesign(
    context: Context,
    uiStore: UiStore,
    srvStore: ServiceStore,
    running: Boolean,
) : WindDesign<NetworkSettingsDesign.Request>(context) {
    private val binding =
        DesignAppControllerBinding.inflate(context.layoutInflater, context.root, false)
    override val topBarView: TopBar
        get() = binding.topBarView
    override val root: View
        get() = binding.root

    override fun initDesign() {
        super.initDesign()
        title = R.string.act_title_network_setting
    }

    init {
        binding.surface = surface

        val screen = preferenceScreen(context) {
            val vpnDependencies: MutableList<Preference> = mutableListOf()

//            val vpn = switch(
//                value = uiStore::enableVpn,
//                icon = com.github.kr328.clash.design.R.drawable.ic_baseline_vpn_lock,
//                title = com.github.kr328.clash.design.R.string.route_system_traffic,
//                summary = com.github.kr328.clash.design.R.string.routing_via_vpn_service
//            ) {
//                listener = OnChangedListener {
//                    vpnDependencies.forEach {
//                        it.enabled = uiStore.enableVpn
//                    }
//                }
//            }

//            category(com.github.kr328.clash.design.R.string.vpn_service_options)

//            switch(
//                value = srvStore::bypassPrivateNetwork,
//                title = com.github.kr328.clash.design.R.string.bypass_private_network,
//                summary = com.github.kr328.clash.design.R.string.bypass_private_network_summary,
//                configure = vpnDependencies::add,
//            )
//
//            switch(
//                value = srvStore::dnsHijacking,
//                title = com.github.kr328.clash.design.R.string.dns_hijacking,
//                summary = com.github.kr328.clash.design.R.string.dns_hijacking_summary,
//                configure = vpnDependencies::add,
//            )
//
//            switch(
//                value = srvStore::allowBypass,
//                title = com.github.kr328.clash.design.R.string.allow_bypass,
//                summary = com.github.kr328.clash.design.R.string.allow_bypass_summary,
//                configure = vpnDependencies::add,
//            )

//            if (Build.VERSION.SDK_INT >= 29) {
//                switch(
//                    value = srvStore::systemProxy,
//                    title = com.github.kr328.clash.design.R.string.system_proxy,
//                    summary = com.github.kr328.clash.design.R.string.system_proxy_summary,
//                    configure = vpnDependencies::add,
//                )
//            }

            selectableList(
                value = srvStore::accessControlMode,
                values = AccessControlMode.values(),
                valuesText = arrayOf(
                    com.github.kr328.clash.design.R.string.allow_all_apps,
                    com.github.kr328.clash.design.R.string.allow_selected_apps,
                    com.github.kr328.clash.design.R.string.deny_selected_apps
                ),
                title = com.github.kr328.clash.design.R.string.access_control_mode,
                configure = vpnDependencies::add,
            )

            clickable(
                title = com.github.kr328.clash.design.R.string.access_control_packages,
                summary = com.github.kr328.clash.design.R.string.access_control_packages_summary,
            ) {
                clicked {
                    requests.trySend(NetworkSettingsDesign.Request.StartAccessControlList)
                }

                vpnDependencies.add(this)
            }

            if (running) {
//                vpn.enabled = false

                vpnDependencies.forEach {
                    it.enabled = false
                }
            } else {
//                vpn.listener?.onChanged()
            }
        }

        binding.content.addView(screen.root)

        if (running) {
            launch {
                showToast(com.github.kr328.clash.design.R.string.options_unavailable, ToastDuration.Indefinite)
            }
        }
    }
}