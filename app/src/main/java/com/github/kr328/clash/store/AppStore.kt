package com.github.kr328.clash.store

import android.content.Context
import com.github.kr328.clash.common.store.Store
import com.github.kr328.clash.common.store.asStoreProvider
import com.github.kr328.clash.core.model.TunnelState

class AppStore(context: Context) {
    private val store = Store(
        context
            .getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
            .asStoreProvider()
    )

    var updatedAt: Long by store.long(
        key = "updated_at",
        defaultValue = -1,
    )

    var lastSSO: String by store.string(key = "last_sso", defaultValue = "")
    var lastHost: String by store.string(key = "last_host", defaultValue = "")
    var account: String by store.string(key = "wind_account", defaultValue = "{}")
    var userInfo: String by store.string(key = "wind_user", defaultValue = "{}")
    var commConfig: String by store.string(key = "comm_conf", defaultValue = "{}")
    var subscribe: String by store.string(key = "subscribe", defaultValue = "{}")
    companion object {
        private const val FILE_NAME = "app"
    }
}