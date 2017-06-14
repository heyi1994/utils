package com.heyi.intent

import android.content.Context
import android.content.Intent
import android.provider.AlarmClock
import android.provider.CalendarContract
import java.util.*

/**
 * Author: Heyi.
 * Date: 2017/6/14.
 * Package:com.heyi.intent.
 * Desc:设置系统功能的工具类
 */
class FunctionUtils private constructor(){
    companion object{
        fun getInstance():FunctionUtils=FunctionUtils()
    }

    /**
     * 设置闹铃
     * @param context 上下文
     * @param message 提示，描述信息
     * @param seconds 秒数
     * @param minutes 分钟数
     * @param hours 小时数
     * @param skipUi  直接设置，不跳转到设置页面
     * @param vibrate 是否开启振动
     * @param days 一周中的周几开启  Calender.Monday
     * 需要权限：<uses-permission android:name="com.android.alarm.permission.SET_ALARM" />
     */
     fun setAlarm(context: Context,message:String,minutes:Int,hours:Int,
                         skipUi:Boolean,vibrate:Boolean,days:ArrayList<Int>){
         val intent=Intent(AlarmClock.ACTION_SET_ALARM)
         intent.putExtra(AlarmClock.EXTRA_MINUTES,minutes)
         intent.putExtra(AlarmClock.EXTRA_HOUR,hours)
         intent.putExtra(AlarmClock.EXTRA_MESSAGE,message)
         intent.putExtra(AlarmClock.EXTRA_VIBRATE,vibrate)
         intent.putExtra(AlarmClock.EXTRA_SKIP_UI,skipUi)
         intent.putExtra(AlarmClock.EXTRA_DAYS,days)
         context.startActivity(intent)
    }

    /**
     * 设置定时器  API19
     * @param context  上下文
     * @param message 信息
     * @param length 以秒为单位的计时器
     * @param skipUi 是否跳过UI直接设置
     * 需要权限：<uses-permission android:name="com.android.alarm.permission.SET_ALARM" />
     */
    fun setTimer(context: Context,message: String,length:Int,skipUi: Boolean){
      val intent=Intent(AlarmClock.ACTION_SET_TIMER)
        intent.putExtra(AlarmClock.EXTRA_SKIP_UI,skipUi)
        intent.putExtra(AlarmClock.EXTRA_LENGTH,length)
        intent.putExtra(AlarmClock.EXTRA_MESSAGE,message)
        context.startActivity(intent)
    }

    /**
     * 插入日历事件
     * @param context context
     * @param title  主题
     * @param location 地址
     * @param allDay 是否是全天事件
     * @param start 开始
     * @param end 结束
     * @param desc 事件描述
     */
    fun insertEvent(context: Context,title:String,location:String,allDay:Boolean,start:Calendar,end:Calendar,desc:String){
        val intent=Intent(Intent.ACTION_INSERT)
        intent.setData(CalendarContract.Events.CONTENT_URI)
        intent.putExtra(CalendarContract.Events.TITLE,title)
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION,location)
        intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY,allDay)
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,start)
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,end)
        intent.putExtra(CalendarContract.Events.DESCRIPTION,desc)
       context.startActivity(intent)
    }
}