package com.kwon.overscrollview

import android.content.Context
import android.view.ViewGroup

/**
 * [RefreshOverScrollView]에서 사용되는 자식뷰이다.
 * 오버스크롤 이벤트가 발생하여 보여질 새로고침 영역을 표시한다.
 */
class RefreshLayout(context: Context): ViewGroup(context) {

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

    }
}