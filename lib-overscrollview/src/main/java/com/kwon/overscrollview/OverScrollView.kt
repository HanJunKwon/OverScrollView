package com.kwon.overscrollview

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_CANCEL
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_MOVE
import android.view.MotionEvent.ACTION_UP
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ScrollView
import com.kwon.overscrollview.OverScrollView.SCROLL_DIRECTION.Companion.SCROLL_DIRECTION_BOTTOM
import com.kwon.overscrollview.OverScrollView.SCROLL_DIRECTION.Companion.SCROLL_DIRECTION_TOP
import kotlin.math.abs

class OverScrollView(context: Context, attrs: AttributeSet): ScrollView(context, attrs) {
    private var isOverScroll = false

    @SCROLL_DIRECTION
    private var overScrollDirection = SCROLL_DIRECTION_TOP

    private var scrollHeight: Int = -1

    private var overScrollStartY = -1f

    private var overScrollPadding = 0

    private var overScrollRecoveredValueAnimator = ValueAnimator.ofInt().apply {
        duration = 250L
        interpolator = LinearInterpolator()
    }

    init {
        overScrollMode = OVER_SCROLL_NEVER
    }

    override fun onGenericMotionEvent(event: MotionEvent?): Boolean {
        Log.d(">>>", "onGenericMotionEvent() :: ${event?.action}")
        return super.onGenericMotionEvent(event)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        Log.d(">>>", "onInterceptTouchEvent() :: ${ev?.action}")
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
//        Log.d(">>>", "onTouchEvent() :: action: ${ev?.action}, rawY: ${ev?.rawY}, y: ${ev?.y}")

        if (isOverScroll && ev?.action == ACTION_MOVE) {
            if (overScrollStartY == -1f) {
                overScrollStartY = ev.y
            }

            when (overScrollDirection) {
                SCROLL_DIRECTION_TOP -> {
                    overScrollPadding = (abs(overScrollStartY - ev.y).toInt())/2
                    Log.d(">>>", "overScrollPadding: ${overScrollPadding}")

                    setPadding(0, overScrollPadding, 0, 0)
//                    Log.d(">>>", "onTouchEvent() :: SCROLL DIRECTION TOP")
                }
                SCROLL_DIRECTION_BOTTOM -> {
                    overScrollPadding = (abs(overScrollStartY - ev.y).toInt())/2
                    setPadding(0, 0, 0, overScrollPadding)
//                    Log.d(">>>", "onTouchEvent() :: SCROLL DIRECTION BOTTOM")
                }
            }

            return false
        } else if (isOverScroll && (ev?.action == ACTION_CANCEL || ev?.action == ACTION_UP)) {
            overScrollRecoveredValueAnimator.apply {
                setIntValues(overScrollPadding, 0)
                addUpdateListener { animation ->
                    if (overScrollDirection == SCROLL_DIRECTION_TOP) {
                        setPadding(0, animation.animatedValue as Int, 0, 0)
                    } else {
                        setPadding(0, 0, 0, animation.animatedValue as Int)
                    }

                    if (animation.animatedValue as Int == 0) {
                        isOverScroll = false
                        overScrollStartY = -1f
                        overScrollPadding = 0
                    }
                }
                start()
            }
        } else if (ev?.action == ACTION_DOWN) {
            if (overScrollRecoveredValueAnimator.isRunning) {
                overScrollPadding = overScrollRecoveredValueAnimator.animatedValue as Int
                overScrollRecoveredValueAnimator.pause()
            }
        }

        return super.onTouchEvent(ev)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return super.dispatchTouchEvent(ev)
    }

    override fun onOverScrolled(scrollX: Int, scrollY: Int, clampedX: Boolean, clampedY: Boolean) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY)
//        Log.d(">>>", "onOverScrolled() :: scrollX: $scrollX, scrollY: $scrollY, clampedX: ${clampedX}, clampedY: ${clampedY}")

        if (scrollHeight == -1) return

        if (clampedY) {
            isOverScroll = (scrollY == 0 || scrollY == scrollHeight)
            overScrollDirection = if (scrollY == 0) SCROLL_DIRECTION_TOP else SCROLL_DIRECTION_BOTTOM
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        if (heightMode == MeasureSpec.EXACTLY) {
            scrollHeight = MeasureSpec.getSize(heightMeasureSpec)
        }
    }

    override fun onNestedFling(
        target: View,
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean
    ): Boolean {
        Log.d(">>>", "onNestedFling() :: velocityX: $velocityX, velocityY: $velocityY")
        return super.onNestedFling(target, velocityX, velocityY, consumed)
    }

    annotation class SCROLL_DIRECTION {
        companion object {
            const val SCROLL_DIRECTION_TOP = 0
            const val SCROLL_DIRECTION_BOTTOM = 1
        }
    }
}