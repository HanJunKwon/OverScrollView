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
import androidx.core.content.withStyledAttributes
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

    private var overScrollTranslationY = 0f

    private var overScrollTranslationFactor = 0.33f

    private var overScrollRecoveredValueAnimator = ValueAnimator.ofInt().apply {
        duration = 250L
        interpolator = LinearInterpolator()
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                super.onAnimationEnd(animation)
                isOverScroll = false
                overScrollStartY = -1f
                setOverScrollTranslationYInternal(0f)
            }
        })
        addUpdateListener { animation ->
            val value = animation.animatedValue as Float
            if (overScrollDirection == SCROLL_DIRECTION_TOP) {
                getChildAt(0).translationY = value
            } else {
                getChildAt(0).translationY = -value
            }
        }
    }

    init {
        overScrollMode = OVER_SCROLL_NEVER

        context.withStyledAttributes(attrs, R.styleable.OverScrollView) {
            setOverScrollTranslationFactorInternal(getFloat(R.styleable.OverScrollView_overScrollTranslationFactor, 0.33f))

        }
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        when (ev?.action) {
            ACTION_DOWN -> {
                if (overScrollRecoveredValueAnimator.isRunning) {
                    setOverScrollTranslationYInternal(overScrollRecoveredValueAnimator.animatedValue as Float)
                    overScrollRecoveredValueAnimator.pause()
                }
            }

            ACTION_POINTER_UP, ACTION_UP, ACTION_CANCEL -> {
                // 오버 스크롤을 원래 상태로 되돌리는 애니메이션 시작
                setScrollRecoverValueAnimation(overScrollTranslationY, 0f)
                startOverScrollRecoverAnimation()
            }

            ACTION_MOVE -> {
                if (isOverScroll) {
                    if (overScrollStartY == -1f) {
                        overScrollStartY = ev.y
                    }

                    val distance = abs(overScrollStartY - ev.y).toInt()
                    overScrollTranslationY = distance * overScrollTranslationFactor

                    when (overScrollDirection) {
                        SCROLL_DIRECTION_TOP -> {
                            getChildAt(0).translationY = overScrollTranslationY
                        }

                        SCROLL_DIRECTION_BOTTOM -> {
                            getChildAt(0).translationY = -overScrollTranslationY
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

    private fun setOverScrollTranslationFactorInternal(factor: Float) {
        check(factor > 0 && factor <= 1f) {
            throw IllegalArgumentException("Invalid overScrollTranslationFactor: must be between 0 and 1")
        }

        this.overScrollTranslationFactor = factor
    }

    fun setOverScrollTranslationFactor(factor: Float) {
        if (overScrollRecoveredValueAnimator.isRunning) {
            Log.e("OverScrollView", "Cannot change overScrollTranslationFactor while translation animation is running")
            return
        }

        setOverScrollTranslationFactorInternal(factor)
    }

    private fun setOverScrollTranslationYInternal(padding: Float) {
        this.overScrollTranslationY = padding
    }

    private fun setScrollRecoverValueAnimation(vararg values: Float) {
        overScrollRecoveredValueAnimator.setFloatValues(*values)
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