package com.wind.vpn.activity

import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import com.github.kr328.clash.BaseActivity
import com.github.kr328.clash.ProfilesActivity
import com.github.kr328.clash.R
import com.github.kr328.clash.common.constants.Intents
import com.github.kr328.clash.common.util.intent
import com.github.kr328.clash.design.ui.ToastDuration
import com.github.kr328.clash.service.util.sendBroadcastSelf
import com.github.kr328.clash.util.startClashService
import com.github.kr328.clash.util.stopClashService
import com.github.kr328.clash.util.withClash
import com.github.kr328.clash.util.withProfile
import com.wind.vpn.WindGlobal
import com.wind.vpn.data.WindApi
import com.wind.vpn.design.HomeDesign
import com.wind.vpn.util.goTargetClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select

class HomeAct : BaseActivity<HomeDesign>() {
    override suspend fun main() {
        val design = HomeDesign(this)
        setContentDesign(design)
        while (isActive) {
            select<Unit> {
                events.onReceive {
                    when (it) {
                        Event.ServiceRecreated,
                        Event.ClashStop, Event.ClashStart,
                        Event.ProfileLoaded, Event.ProfileChanged, Event.UserInfoChanged -> design.fetch()

                        Event.LoginSuccess -> loadUserInfo()
                        Event.ActivityStart -> {
                            loadUserInfo()
                            design.fetch()
                        }

                        else -> Unit
                    }
                }
                design.requests.onReceive {
                    when (it) {
                        HomeDesign.Request.ToggleStatus -> {
                            if (clashRunning)
                                stopClashService()
                            else
                                design.startClash()
                        }
                        HomeDesign.Request.OpenCharge -> {
                            goTargetClass(this@HomeAct, RechargeActivity::class.java)
                        }
                    }
                }
            }

        }
    }

    private suspend fun HomeDesign.startClash() {
        val active = withProfile { queryActive() }

        if (active == null || !active.imported) {
            showToast(R.string.no_profile_selected, ToastDuration.Long) {
                setAction(R.string.profiles) {
                    startActivity(ProfilesActivity::class.intent)
                }
            }

            return
        }

        val vpnRequest = startClashService()

        try {
            if (vpnRequest != null) {
                val result = startActivityForResult(
                    ActivityResultContracts.StartActivityForResult(),
                    vpnRequest
                )

                if (result.resultCode == RESULT_OK)
                    startClashService()
            }
        } catch (e: Exception) {
            design?.showToast(R.string.unable_to_start_vpn, ToastDuration.Long)
        }
    }

    private suspend fun HomeDesign.fetch() {
        setClashRunning(clashRunning)

        val state = withClash {
            queryTunnelState()
        }
        val providers = withClash {
            queryProviders()
        }

//        val active = withProfile { queryActive() }
//        active?.let { setRemainTime(active.expire) }
        updateUI()
    }

    override fun overrideBackPress(): Boolean {
        return design?.overrideBackPress() == true
    }

    private fun loadUserInfo() {
        if (!WindGlobal.account.isLogin()) return
        WindGlobal.loadUserInfoFromServer()
    }
}