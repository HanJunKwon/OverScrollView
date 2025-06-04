package com.kwon.overscrollview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.core.view.isEmpty

class RefreshOverScrollView(context: Context, attrs: AttributeSet): FrameLayout(context, attrs) {
    private val refreshLayout: RefreshLayout = RefreshLayout(context).apply {
        layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            0
        )
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

    fun setCustomHeader(view: View) {
        check (refreshLayout.isEmpty()) {
            throw IllegalArgumentException("")
        }

        refreshLayout.addView(view)
    }

    fun setCustomHeader(@LayoutRes viewRes: Int) {
        check (refreshLayout.isEmpty()) {
            throw IllegalArgumentException("")
        }

        refreshLayout.addViewResource(viewRes)
    }
}