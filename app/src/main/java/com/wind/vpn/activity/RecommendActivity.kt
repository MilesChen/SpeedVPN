package com.wind.vpn.activity

import android.os.Bundle
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.kr328.clash.R
import com.github.kr328.clash.databinding.ActRecommendBinding
import com.wind.vpn.data.bean.InviteResp
import com.wind.vpn.model.RecommendViewModel

class RecommendActivity:BaseActivity(),Observer<InviteResp> {
    private lateinit var binding:ActRecommendBinding
    private val viewModel by lazy { ViewModelProvider(this)[RecommendViewModel::class.java] }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun genCustomView(): View {
        binding = ActRecommendBinding.inflate(layoutInflater, findViewById(android.R.id.content), false)
        viewModel.currentInvite.observe(this, this)
        return binding.root
    }

    override fun getTopTitle(): Int {
        return R.string.act_title_recommend
    }

    override fun onChanged(value: InviteResp) {
        TODO("Not yet implemented")
    }
}