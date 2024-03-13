package com.wind.vpn.activity

import android.view.View
import com.github.kr328.clash.databinding.ActBrowserBinding

const val KEY_TARGET_URL = "key_target_url"
class BrowserActivity:BaseActivity() {
    private lateinit var binding:ActBrowserBinding
    private var url:String? = ""
    override fun genCustomView(): View {
        binding = ActBrowserBinding.inflate(layoutInflater, findViewById(android.R.id.content), false)
        return binding.root
    }

    override fun initView() {
        super.initView()
        url = intent.getStringExtra(KEY_TARGET_URL)
        if (url.isNullOrEmpty()) {
            finish()
            return
        }
        binding.webviewWind.loadUrl(url!!)
    }

    override fun getTopTitle(): Int {
        return 0
    }
}
