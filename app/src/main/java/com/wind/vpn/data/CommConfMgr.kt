package com.wind.vpn.data

import android.util.Log
import com.github.kr328.clash.common.Global
import com.github.kr328.clash.store.AppStore
import com.wind.vpn.bean.toBean
import com.wind.vpn.data.bean.CommConfig
import com.wind.vpn.toJson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object CommConfMgr:CoroutineScope by CoroutineScope(Dispatchers.IO) {
    private val store = AppStore(Global.application)
    private var _commConfig: CommConfig? = store.commConfig.toBean<CommConfig>()
    fun init() {
        launch {
            loadCommConf(true)
        }

    }

    fun loadCommConf(force:Boolean = false):CommConfig? {
        if (!force) {
            if (!commConfig?.currency_symbol.isNullOrEmpty()) {
                Log.d("Wind", "get commConfig from cache: ${commConfig.toJson()}")
                return commConfig
            }
        }
        val result = WindApi.getCommConf()
        if (result.isSuccess) {
            commConfig = result.data
        }
        if (commConfig == null) {}
        return commConfig
    }

    fun getUserCommConfig():CommConfig {
        var config = loadCommConf()
        if (config == null) {
            config = CommConfig()
        }
        return config!!
    }

    private var commConfig: CommConfig?
        get() = _commConfig
        set(value) {
            _commConfig = value
            if (value != null) {
                store.commConfig = value.toJson()
            }
        }
}