package com.xqb.xqbutils.base

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.xqb.xqbutils.dialogx.dialogs.WaitDialog
import com.xqb.xqbutils.sharedPreferences.PerfUtil
import com.xqb.xqbutils.statuBar.ImmersionBar
import com.xqb.xqbutils.eventbus.Event
import com.xqb.xqbutils.eventbus.EventBusUtils
import com.xqb.xqbutils.eventbus.code.Subscribe
import com.xqb.xqbutils.eventbus.code.ThreadMode
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * @author xqb
 * @date 2021/9/6
 * activity基类
 */
abstract class BaseActivity<V:ViewDataBinding,VM: BaseViewModel> :AppCompatActivity() {

    var binding: V? = null
    var viewModel: VM? = null
    val perfUtil by lazy { PerfUtil(this)}
    val gson by lazy { Gson()}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImmersionBar.with(this).statusBarDarkFont(true).transparentStatusBar().init()
        this.tryCreateViewBindingAndViewModel()
        this.viewModelHelper()
        if (this.binding != null) {
            this.setContentView(this.binding!!.root)
        } else {
            this.setContentView(this.setContentLayoutId())
        }
        this.initView(savedInstanceState)
        if(enableEventBus()){
            EventBusUtils.register(this)
        }
        //添加至activity集合
        BaseApp.addActivity(this)
    }


    /**
    * 通过反射创建binding和viewModel
    * */
    private fun tryCreateViewBindingAndViewModel() {
        try {
            val type: Type = this.javaClass.genericSuperclass
            if (type is ParameterizedType) {
                val types: Array<Type> = (type as ParameterizedType).actualTypeArguments
                if (types.isNotEmpty()) {
                    val inflate: Method = (types[0] as Class<*>).getDeclaredMethod("inflate", LayoutInflater::class.java)
                    this.binding = inflate.invoke(null as Any?, this.layoutInflater) as V
//                    viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(types[1] as Class<VM>)  //这里不确定？？？
                    this.viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(types[1] as Class<BaseViewModel>) as VM

                }
            }
        } catch (var4: Throwable) {
            var4.printStackTrace()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        if(enableEventBus()){
            EventBusUtils.unregister(this)
        }
    }


    abstract fun setContentLayoutId(): Int
    abstract fun initView(savedInstanceState: Bundle?)
    open fun enableEventBus():Boolean{
       return false
    }

    @Subscribe(threadMode= ThreadMode.MAIN)
    open fun eventMessage(event:Event){

    }

    /**
     * 简单封装toast,其他重用度高的代码都可提取出来统一封装
     * */
    fun showToast(msg:String){
        Toast.makeText(this,msg, Toast.LENGTH_SHORT).show()
    }

    fun showProgress(msg:String){
        WaitDialog.show(msg).setOnBackPressedListener {
            true
        }
    }

    fun dismissProgress(){
        WaitDialog.dismiss()
    }

    /**
     * 针对viewModel做一些交互封装
     * */
    private fun viewModelHelper(){
        viewModel?.toastMessage?.observe(this) {
            showToast(it)
        }
        viewModel?.progressMessage?.observe(this) {
            if (it == "dismiss") {
                dismissProgress()
            } else {
                showProgress(it)
            }
        }
    }

    fun exitApp(){
        BaseApp.exitApp()
    }

    /**
    * 跳转页面封装
    * */
    fun xStartActivity(cls: Class<*>) {
        startActivity(Intent(applicationContext,cls))
    }
    /**
     * 跳转页面携带数字参数封装
     * */
    fun xStartActivity(cls: Class<*>,name:String,dataInt:Int) {
        startActivity(Intent(applicationContext,cls).putExtra(name,dataInt))
    }
    /**
     * 跳转页面携带字符参数封装
     * */
    fun xStartActivity(cls: Class<*>,name:String,dataString:String) {
        startActivity(Intent(applicationContext,cls).putExtra(name,dataString))
    }
    /**
     * 跳转页面携带对象参数封装
     * */
    fun xStartActivity(cls: Class<*>,name:String,dataObject:Any) {
        startActivity(Intent(applicationContext,cls).putExtra(name,gson.toJson(dataObject)))
    }
}