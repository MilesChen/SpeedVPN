package com.wind.vpn.activity

import android.view.View
import com.github.kr328.clash.databinding.ActMessageDetailBinding
import com.wind.vpn.bean.toBean
import com.wind.vpn.data.bean.NoticeBean

const val KEY_MESSAGE_DETAIL = "key_message_detail"
class MessageDetailActivity:BaseActivity() {
    private lateinit var binding: ActMessageDetailBinding
    override fun genCustomView(): View {
        binding = ActMessageDetailBinding.inflate(layoutInflater, findViewById(android.R.id.content), false)
        return binding.root
    }

    override fun initView() {
        super.initView()
        val noticeBean = intent.getStringExtra(KEY_MESSAGE_DETAIL)?.toBean<NoticeBean>()
        if (noticeBean == null) {
            finish()
            return
        }
        binding.message = noticeBean
        binding.executePendingBindings()

    }

    override fun getTopTitle(): Int {
        return 0
    }
}