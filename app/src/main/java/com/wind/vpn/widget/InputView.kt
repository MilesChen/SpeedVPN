package com.wind.vpn.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.github.kr328.clash.R

class InputView : LinearLayout {
    lateinit var prefixIcon: ImageView
    lateinit var suffixIcon: ImageView
    lateinit var inputText: EditText

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    private fun init(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.view_input, this, true)
        prefixIcon = findViewById(R.id.iv_prefix)
        suffixIcon = findViewById(R.id.iv_suffix)
        inputText = findViewById(R.id.et_input)
    }



}
