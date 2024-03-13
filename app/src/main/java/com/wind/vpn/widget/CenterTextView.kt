package com.wind.vpn.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.annotation.AttrRes
import androidx.appcompat.widget.AppCompatTextView

class CenterTextView@JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
) : AppCompatTextView(context, attributeSet, defStyleAttr)  {
    override fun onDraw(canvas: Canvas?) {

        // 当左边Drawable的不为空时
        compoundDrawables[0]?.run {
            horizontalDrawable(this)
        }
        // 如果上边的Drawable不为空时
        compoundDrawables[1]?.run {
            verticalDrawable(this)
        }
        // 当右边Drawable的不为空时
        compoundDrawables[2]?.run {
            horizontalDrawable(this)
        }
        // 如果下边的Drawable不为空时
        compoundDrawables[3]?.run {
            verticalDrawable(this)
        }
        super.onDraw(canvas)
    }

    /**
     * 水平方向的重绘
     */
    private fun horizontalDrawable(drawable: Drawable) {
        val textWidth: Float = paint.measureText(text.toString())
        // 计算总宽度&#xff08;文本宽度 &#43; drawablePadding &#43; drawableWidth&#xff09; minimumWidth或者intrinsicWidth
        val bodyWidth = textWidth + compoundDrawablePadding + drawable.minimumWidth
        if (layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            //不用处理
        } else {
            //这里判断主要是因为每次setPadding的时候会重新执行OnDraw(),所以进行判断&#xff0c;避免死循环
            if (paddingRight != (width - bodyWidth).toInt() / 2) {
            //设置边距
            setPadding(
                (width - bodyWidth).toInt() / 2,
                paddingTop,
                (width - bodyWidth).toInt() / 2,
                paddingBottom
            )
        }
        }
    }

    /**
     * 垂直方向的重绘
     */
    private fun verticalDrawable(drawable: Drawable) {
        var textHeight: Float = (paint.fontMetrics.bottom - paint.fontMetrics.top)
//        var textHeight: Float &#61; (paint.fontMetrics.descent - paint.fontMetrics.ascent)
        textHeight *= lineCount
        // 计算总高度&#xff08;文本高度 &#43; drawablePadding &#43; drawableHeight&#xff09;
        val bodyHeight = textHeight + compoundDrawablePadding + drawable.minimumHeight

        if (layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            //不用处理
        } else {
            //这里判断主要是因为每次setPadding的时候会重新执行OnDraw(),所以进行判断&#xff0c;避免死循环
            if (paddingBottom != (height - bodyHeight).toInt() / 2) {
            //设置边距
            setPadding(
                paddingLeft,
                (height - bodyHeight).toInt() / 2,
                paddingRight,
                (height - bodyHeight).toInt() / 2,
            )
        }
        }
    }
}