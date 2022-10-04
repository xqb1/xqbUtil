package com.xqb.xqbutils.base

import android.app.Activity
import android.app.Application

open class BaseApp :Application() {
    companion object{
        private val activityList: ArrayList<Activity> = arrayListOf()//所有activity集合
        /**
         * 打开一个新页面就添加activity到集合中
         * */
        fun addActivity(activity: Activity){
            activityList.add(activity)
        }

        /**
         * 关闭所有页面
         * */
        fun exitApp(){
            for(activity in activityList){
                activity.finish()
            }
            activityList.clear()
        }
    }
}