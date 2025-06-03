package com.kwon.overscrollview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat

class RefreshOverScrollView(context: Context, attrs: AttributeSet): FrameLayout(context, attrs) {
    private val refreshLayout: RefreshLayout = RefreshLayout(context).apply {
        layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            0
        )
        setBackgroundColor(ContextCompat.getColor(context, com.google.android.material.R.color.design_default_color_error))
    }
    private val overScrollView: OverScrollView = OverScrollView(context).apply {
        layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
    }

    init {
        super.addView(overScrollView)
        super.addView(refreshLayout)

        overScrollView.setOverScrollListener(object : OverScrollView.OverScrollListener {
            override fun onOverScrolling(translationY: Float) {
                refreshLayout.layoutParams = refreshLayout.layoutParams.apply {
                    height = translationY.toInt()
                }
            }
        })
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        // 자신(RefreshOverScrollView)의 자식 중 OverScrollView와 RefreshLayout을 제외한 나머지를 OverScrollView에 이관
        val viewsToMove = mutableListOf<View>()

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child !== overScrollView && child !== refreshLayout) {
                viewsToMove.add(child)
            }
        }

        viewsToMove.forEach { child ->
            removeView(child)
            overScrollView.addView(child)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

//        if (widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {
//            refreshLayout.translationY = (-refreshLayout.height).toFloat()
//        }
    }

    override fun addView(child: View?) {
        if (child === overScrollView || child === refreshLayout) {
            super.addView(child)
        } else {
            overScrollView.addView(child)
        }
    }

    override fun addView(child: View?, index: Int) {
        if (child === overScrollView || child === refreshLayout) {
            super.addView(child, index)
        } else {
            overScrollView.addView(child, index)
        }
    }

//    override fun addView(child: View?, params: LayoutParams?) {
//        if (child === overScrollView || child === refreshLayout) {
//            super.addView(child, params)
//        } else {
//            overScrollView.addView(child, params)
//        }
//    }
//
//    override fun addView(child: View?, index: Int, params: LayoutParams?) {
//        if (child === overScrollView || child === refreshLayout) {
//            super.addView(child, index, params)
//        } else {
//            overScrollView.addView(child, index, params)
//        }
//    }
}