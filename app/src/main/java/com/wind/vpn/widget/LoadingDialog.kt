package com.wind.vpn.widget

import android.app.Dialog
import android.content.Context
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import com.github.kr328.clash.R

class LoadingDialog @JvmOverloads constructor(context: Context, themId: Int = 0) :
    Dialog(context, themId) {
    private var msgView: TextView
    private var loadingView: ImageView

    init {
        setContentView(R.layout.view_dialog_loading)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        msgView = findViewById(R.id.tv_loading_dialog_msg)
        loadingView = findViewById(R.id.iv_loading)
    }

    override fun show() {
        val rotateAnimation = RotateAnimation(
            0f,
            360f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        ).apply {
            duration = 1000
            interpolator = LinearInterpolator()
            repeatCount = Animation.INFINITE
        }

        loadingView.startAnimation(rotateAnimation)
        super.show()
    }

    override fun dismiss() {
        loadingView.clearAnimation()
        super.dismiss()
    }

    fun setMessage(msg: String) {
        msgView.text = msg
    }
}