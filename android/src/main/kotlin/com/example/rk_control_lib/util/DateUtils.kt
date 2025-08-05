package com.example.rk_control_lib.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * @Author: QuYunShuo
 * @Time: 2020/9/8
 * @Class: DateUtils
 * @Remark: 时间工具类
 */
object DateUtils {
    /**
     * 获取时间格式化String
     * @param timestamp 时间戳
     * @param dateFormat 日期格式
     */
    fun getDateFormatString(timestamp: Long, dateFormat: String): String =
        SimpleDateFormat(dateFormat, Locale.CHINESE).format(Date(timestamp))

    /**
     * 将固定格式[dateFormat]的时间字符串[dateString]转换为时间值
     */
    fun getDateStringToDate(dateString: String, dateFormat: String): Long {
        val simpleDateFormat = SimpleDateFormat(dateFormat, Locale.CHINESE)
        var date = Date()
        try {
            date = simpleDateFormat.parse(dateString)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return date.time
    }

    fun getWeekOfDate(): String {
        var date = Date()
        val weekDays = arrayOf(7, 1, 2, 3, 4, 5, 6)
        val cal = Calendar.getInstance()
        cal.time = date
        var w = cal[Calendar.DAY_OF_WEEK] - 1
        if (w < 0) {
            w = 0
        }
        weekDays[w]
        return when(weekDays[w]){
            7->"星期日"
            1->"星期一"
            2->"星期二"
            3->"星期三"
            4->"星期四"
            5->"星期五"
            6->"星期六"
            else ->"error"
        }
    }

    fun getWeekOfDateIndexForSmdt(): Int {
        var date = Date()
        val weekDays = arrayOf(7, 1, 2, 3, 4, 5, 6)
        val cal = Calendar.getInstance()
        cal.time = date
        var w = cal[Calendar.DAY_OF_WEEK] - 1
        if (w < 0) {
            w = 0
        }
        weekDays[w]
        return when (weekDays[w]) {
            7 -> 0
            1 -> 1
            2 -> 2
            3 -> 3
            4 -> 4
            5 -> 5
            6 -> 6
            else -> -1
        }
    }
    /**
     * 根据日期获取当天星期
     * @param datetime "2022-05-07"
     * @return
     */
    fun dateToWeekForSmdt(datetime: String?): String? {
        val f = SimpleDateFormat("yyyy-MM-dd-HH-mm")
        val weekDays = arrayOf("0", "1", "2", "3", "4", "5", "6")
        val cal = Calendar.getInstance()
        val date: Date
        try {
            date = f.parse(datetime)
            cal.time = date
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        //一周的第几天
        var w = cal[Calendar.DAY_OF_WEEK] - 1
        if (w < 0) w = 0
        return weekDays[w]
    }

    /**
     * 计算两个时间戳距离的天数
     */
    fun calculateDaysBetweenTimestamps(timestamp1: Long, timestamp2: Long): Long {
        val millisecondsPerDay = 24 * 60 * 60 * 1000L // 一天的毫秒数
        val difference = timestamp2 - timestamp1 // 时间戳之间的毫秒数差距
        return difference / millisecondsPerDay
    }

}