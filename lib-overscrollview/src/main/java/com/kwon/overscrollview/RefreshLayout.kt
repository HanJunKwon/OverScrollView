package com.kwon.overscrollview

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.core.view.children

/**
 * [RefreshOverScrollView]에서 사용되는 자식뷰이다.
 * 오버스크롤 이벤트가 발생하여 보여질 새로고침 영역을 표시한다.
 */
class RefreshLayout(context: Context): ViewGroup(context) {
    private var maxHeightPixel: Int = 0

    init {
        val density = context.resources.displayMetrics.density
        maxHeightPixel = (density * MAX_HEIGHT_DP).toInt()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        var heightSize = MeasureSpec.getSize(heightMeasureSpec)

        if (heightMode == MeasureSpec.EXACTLY && heightSize > maxHeightPixel) {
            heightSize = maxHeightPixel
        }

        val exactHeightSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY)
        measureChildren(widthSize, exactHeightSpec)

        setMeasuredDimension(widthSize, heightSize)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for (view in children) {
            view.layout(l, t, r, b)
        }
    }

    fun addViewResource(@LayoutRes resId: Int) {
        LayoutInflater.from(context).inflate(resId, this, true)
    }

    companion object {
        const val MAX_HEIGHT_DP = 75f
    }
}