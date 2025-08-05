package com.example.rk_control_lib.listener

/**
 *  author : vinda
 *  date : 2021/8/19 8:21
 *  description :
 */
interface SetSystemTimeListener {
    //设定系统时间成功
    fun setTimeSuccess()
    //设定系统时间失败
    fun setTimeError()
}