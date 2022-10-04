package com.xqb.xqbutils.sharedPreferences

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Create by xqb
 * On 2021/5/6
 */
class PerfUtil(context: Context) {
    private val PERFNAME=context.packageName.toString()
    private val sharedPreferences by lazy {context.getSharedPreferences(PERFNAME,0) }
    private val gson by lazy { Gson() }

    /**
    * 存取String类型
    * */
    fun getString(name:String, default:String): String? {
        return sharedPreferences.getString(name,default)
    }
    fun putString(name:String,value:String?): Boolean {
        return sharedPreferences.edit().putString(name,value).commit()
    }

    /**
    * 存取Int类型
    * */
    fun getInt(name:String, default:Int): Int {
        return sharedPreferences.getInt(name,default)
    }
    fun putInt(name:String,value:Int): Boolean {
        return sharedPreferences.edit().putInt(name,value).commit()
    }

    /**
    * 存取Long类型
    * */
    fun getLong(name:String,default:Long): Long {
        return sharedPreferences.getLong(name,default)
    }
    fun putLong(name:String,value:Long): Boolean {
        return sharedPreferences.edit().putLong(name,value).commit()
    }

    /**
    * 存取boolean类型
    * */
    fun getBoolean(name:String,default:Boolean): Boolean {
        return sharedPreferences.getBoolean(name,default)
    }
    fun putBoolean(name:String,value:Boolean): Boolean {
        return sharedPreferences.edit().putBoolean(name,value).commit()
    }

    /**
    * 存取List<int>集合
    * */
    fun getListInt(name:String):MutableList<Int>{
        var listData= mutableListOf<Int>()
        val data=sharedPreferences.getString(name,"")
        if(data==""){
            return listData
        }
        listData = gson.fromJson(data, object : TypeToken<MutableList<Int>>() {}.type)
        return listData
    }
    fun putListInt(name:String,value:MutableList<Int>):Boolean{
        if(value.isEmpty()) return false
        val data=gson.toJson(value)
        return sharedPreferences.edit().putString(name,data).commit()
    }

    /**
    * 清除某个
    * */
    fun clear(name:String){
        sharedPreferences.edit().remove(name).apply()
    }

    /*
    * 清空
    * */
    fun clearAll(){
        sharedPreferences.edit().clear().apply()
    }

}