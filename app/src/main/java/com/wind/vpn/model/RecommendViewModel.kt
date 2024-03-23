package com.wind.vpn.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.wind.vpn.data.bean.InviteResp

class RecommendViewModel(application: Application):AndroidViewModel(application) {

    private val inviteResp = MutableLiveData<InviteResp>()

    val currentInvite: LiveData<InviteResp>
        get() = inviteResp
}