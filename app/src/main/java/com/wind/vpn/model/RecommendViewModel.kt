package com.wind.vpn.model

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.wind.vpn.data.CommConfMgr
import com.wind.vpn.data.WindApi
import com.wind.vpn.data.bean.InviteResp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecommendViewModel(application: Application) : AndroidViewModel(application),
    CoroutineScope by CoroutineScope(Dispatchers.IO) {

    private val modelState = MutableLiveData<ModelState>()
    private val inviteResp = MutableLiveData<InviteResp>()
    private val currentSymbol = MutableLiveData<String>()

    val currentInvite: LiveData<InviteResp>
        get() = inviteResp
    val currentState: LiveData<ModelState>
        get() = modelState
    val symbol:LiveData<String>
        get() = currentSymbol

    fun loadInviteResp() {
        launch {
            notifyModelState(ModelState.Loading)
            val resp = WindApi.getInviteInfo()
            if (resp.isSuccess) {
                inviteResp.postValue(resp.data)
                if (resp.data == null || !resp.data!!.hasValidCode()) {
                    val getCodeResult = WindApi.getInviteCode()
                    if (getCodeResult.isSuccess && getCodeResult.data!!) {
                        val retryResp = WindApi.getInviteInfo()
                        if (retryResp.isSuccess && retryResp.data != null) {
                            inviteResp.postValue(retryResp.data)
                        } else {
                            notifyModelState(ModelState.Error.apply { message = retryResp.showMsg })
                        }
                    } else {
                        notifyModelState(ModelState.Error.apply { message = getCodeResult.showMsg })
                    }
                }
            } else {
                notifyModelState(ModelState.Error.apply { message = resp.showMsg })
            }
        }
        launch {
            val commConfig = CommConfMgr.getUserCommConfig()
            currentSymbol.postValue(commConfig.currency)
        }
    }

    private fun notifyModelState(state: ModelState) {
        modelState.postValue(state)
    }
}