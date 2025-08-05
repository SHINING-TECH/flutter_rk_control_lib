package com.example.rk_control_lib.listener

/**
 *  author : vinda
 *  date : 2021/8/19 8:21
 *  description :
 */
interface TakeShotListener {
    /**
     * 截屏回调
     */
    fun takeShotListener(isSuccesse:Boolean,absolutePath:String)
}