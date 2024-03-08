package com.github.kr328.clash.util

import android.content.Context
import android.content.Intent
import android.net.VpnService
import com.github.kr328.clash.common.compat.startForegroundServiceCompat
import com.github.kr328.clash.common.constants.Intents
import com.github.kr328.clash.common.util.intent
import com.github.kr328.clash.core.Clash
import com.github.kr328.clash.design.store.UiStore
import com.github.kr328.clash.service.ClashService
import com.github.kr328.clash.service.TunService
import com.github.kr328.clash.service.util.sendBroadcastSelf
import com.github.kr328.clash.store.AppStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun Context.startClashService(): Intent? {
    val uiStore = UiStore(this)
    val startTun = uiStore.enableVpn
    val tunnel = uiStore.tunnelMode

    if (startTun) {
        val vpnRequest = VpnService.prepare(this)
        if (vpnRequest != null)
            return vpnRequest

        startForegroundServiceCompat(TunService::class.intent)
    } else {
        startForegroundServiceCompat(ClashService::class.intent)
    }
    CoroutineScope(Dispatchers.IO).launch {
        withClash {
            delay(200)
            val mode = queryOverride(Clash.OverrideSlot.Session)
            mode.mode = tunnel
            patchOverride(Clash.OverrideSlot.Session, mode)
        }
    }

    return null
}

fun Context.stopClashService() {
    sendBroadcastSelf(Intent(Intents.ACTION_CLASH_REQUEST_STOP))
}