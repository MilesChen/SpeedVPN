package com.wind.vpn.widget

import android.app.Dialog
import android.content.Context
import android.widget.TextView
import com.github.kr328.clash.R

class CommDialog @JvmOverloads constructor(context: Context, themId: Int = 0) :
    Dialog(context, themId) {
        private var msgView: TextView
        private var btnView: TextView

    init {
        setCanceledOnTouchOutside(false)
        setCancelable(false)
        setContentView(R.layout.view_dialog_error)
        msgView = findViewById(R.id.tv_dialog_content)
        btnView = findViewById(R.id.btn_ok)
    }

    fun setMessage(msg: String) {
        msgView.text = msg
    }

    fun setButton(text: String, onClick: () -> Unit) {
        btnView.text = text
        btnView.setOnClickListener {
            dismiss()
            onClick()
        }
    }
}