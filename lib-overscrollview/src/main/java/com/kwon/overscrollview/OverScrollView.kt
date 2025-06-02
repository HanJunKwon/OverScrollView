package com.kwon.overscrollview

import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_CANCEL
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_MOVE
import android.view.MotionEvent.ACTION_POINTER_UP
import android.view.MotionEvent.ACTION_UP
import android.view.animation.LinearInterpolator
import android.widget.ScrollView
import com.kwon.overscrollview.OverScrollView.SCROLL_DIRECTION.Companion.SCROLL_DIRECTION_BOTTOM
import com.kwon.overscrollview.OverScrollView.SCROLL_DIRECTION.Companion.SCROLL_DIRECTION_TOP
import kotlin.math.abs


class OverScrollView(context: Context, attrs: AttributeSet): ScrollView(context, attrs) {

    /**
     * 사용자가 스크롤을 할 때, 스크롤뷰의 상단 또는 하단에서 오버스크롤이 발생했는지 여부
     */
    private var isOverScroll = false

    /**
     * 오버스크롤 이벤트가 발생할 때 스크롤 중인 방향
     * - [SCROLL_DIRECTION_TOP] : 스크롤뷰의 상단에서 오버스크롤이 발생
     * - [SCROLL_DIRECTION_BOTTOM] : 스크롤뷰의 하단에서 오버스크롤이 발생
     */
    @SCROLL_DIRECTION
    private var overScrollDirection = SCROLL_DIRECTION_TOP

    /**
     * 스크롤뷰의 높이.
     *
     * [onMeasure] 메소드에서 정확한 높이가 계산 됐을 때 설정됨.
     */
    private var scrollViewHeight: Int = -1

    private var overScrollStartY = -1f

    private var overScrollPadding = 0

    private var overScrollRecoveredValueAnimator = ValueAnimator.ofInt().apply {
        duration = 250L
        interpolator = LinearInterpolator()
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                super.onAnimationEnd(animation)
                isOverScroll = false
                overScrollStartY = -1f
                setOverScrollPaddingInternal(0)
            }
        })
        addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            if (overScrollDirection == SCROLL_DIRECTION_TOP) {
                getChildAt(0).translationY = value.toFloat()
            } else {
                getChildAt(0).translationY = -value.toFloat()
            }
        }
    }

    init {
        overScrollMode = OVER_SCROLL_NEVER
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        when (ev?.action) {
            ACTION_DOWN -> {
                if (overScrollRecoveredValueAnimator.isRunning) {
                    setOverScrollPaddingInternal(overScrollRecoveredValueAnimator.animatedValue as Int)
                    overScrollRecoveredValueAnimator.pause()
                }
            }

            ACTION_POINTER_UP, ACTION_UP, ACTION_CANCEL -> {
                // 오버 스크롤을 원래 상태로 되돌리는 애니메이션 시작
                setScrollRecoverValueAnimation(overScrollPadding, 0)
                startOverScrollRecoverAnimation()
            }

            ACTION_MOVE -> {
                if (isOverScroll) {
                    if (overScrollStartY == -1f) {
                        overScrollStartY = ev.y
                    }

                    val distance = abs(overScrollStartY - ev.y).toInt()
                    overScrollPadding = distance / 2

                    when (overScrollDirection) {
                        SCROLL_DIRECTION_TOP -> {
                            getChildAt(0).translationY = overScrollPadding.toFloat()
                        }

                        SCROLL_DIRECTION_BOTTOM -> {
                            getChildAt(0).translationY = -overScrollPadding.toFloat()
                        }
                    }

                    return false
                }
            }
        }

        return super.onTouchEvent(ev)
    }

    override fun onOverScrolled(scrollX: Int, scrollY: Int, clampedX: Boolean, clampedY: Boolean) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY)

        if (scrollViewHeight == -1) return

        isOverScroll = clampedY

        if (clampedY) {
            overScrollDirection = if (scrollY == 0) SCROLL_DIRECTION_TOP else SCROLL_DIRECTION_BOTTOM
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        if (heightMode == MeasureSpec.EXACTLY) {
            scrollViewHeight = MeasureSpec.getSize(heightMeasureSpec)
        }
    }

    private fun setOverScrollPaddingInternal(padding: Int) {
        this.overScrollPadding = padding
    }

    private fun setScrollRecoverValueAnimation(vararg values: Int) {
        overScrollRecoveredValueAnimator.setIntValues(*values)
    }

    private fun startOverScrollRecoverAnimation() {
        overScrollRecoveredValueAnimator.start()
    }

    annotation class SCROLL_DIRECTION {
        companion object {
            const val SCROLL_DIRECTION_TOP = 0
            const val SCROLL_DIRECTION_BOTTOM = 1
        }
    }
}