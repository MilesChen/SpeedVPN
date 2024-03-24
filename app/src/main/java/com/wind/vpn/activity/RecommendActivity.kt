package com.wind.vpn.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.kr328.clash.R
import com.github.kr328.clash.databinding.ActRecommendBinding
import com.wind.vpn.data.DomainManager
import com.wind.vpn.data.bean.InviteResp
import com.wind.vpn.model.ModelState
import com.wind.vpn.model.RecommendViewModel
import com.wind.vpn.toJson

class RecommendActivity:BaseActivity(),Observer<InviteResp> {
    private lateinit var binding:ActRecommendBinding
    private val viewModel by lazy { ViewModelProvider(this)[RecommendViewModel::class.java] }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun genCustomView(): View {
        binding = ActRecommendBinding.inflate(layoutInflater, findViewById(android.R.id.content), false)
        return binding.root
    }

    override fun initView() {
        super.initView()
        viewModel.currentInvite.observe(this, this)
        viewModel.currentState.observe(this){
            when(it){
                ModelState.Loading->{
                    showLoading()
                }
                ModelState.Error->{
                    hideLoading()
                }
                ModelState.Success->{

                }
            }
        }
        viewModel.symbol.observe(this){
            binding.symbol = it
        }
        viewModel.loadInviteResp()
        binding.btnShareLink.setOnClickListener {
            gotoShare()
        }
    }

    private fun gotoShare(){
        val inviteResp = viewModel.currentInvite.value
        inviteResp?.let {
            if (it.hasValidCode()){
                val uri = Uri.parse(DomainManager.ossBean.registerUrl)
                val url = uri.buildUpon().appendQueryParameter("code", it.getValidCode()).build().toString()
                copyToClipboard(url)
                showToast(getString(R.string.copy_to_clipboard))
                val intent = Intent(Intent.ACTION_SEND).apply {
                    putExtra(Intent.EXTRA_TEXT, url)
                    type = "text/plain"
                }
                startActivity(Intent.createChooser(intent, getString(R.string.choose_and_share)))
                return
            }
        }
        showToast(getString(R.string.toast_error))
    }

    private fun copyToClipboard(text: String) {
        val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText(text, text)
        clipboardManager.setPrimaryClip(clipData)
    }

    override fun getTopTitle(): Int {
        return R.string.act_title_recommend
    }

    override fun onChanged(value: InviteResp) {
        hideLoading()
        binding.inviteInfo = value
    }
}