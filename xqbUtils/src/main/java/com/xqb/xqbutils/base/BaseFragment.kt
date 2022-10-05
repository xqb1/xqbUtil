package com.xqb.xqbutils.base

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.google.gson.Gson
import com.xqb.xqbutils.dialogx.dialogs.WaitDialog
import com.xqb.xqbutils.eventbus.Event
import com.xqb.xqbutils.eventbus.EventBusUtils
import com.xqb.xqbutils.eventbus.code.Subscribe
import com.xqb.xqbutils.eventbus.code.ThreadMode
import com.xqb.xqbutils.sharedPreferences.PerfUtil
import java.lang.reflect.ParameterizedType

/**
* fragment基类
* */
abstract class BaseFragment<V:ViewDataBinding,VM: BaseViewModel> :Fragment() {
    var binding:V?=null
    var viewModel:VM?=null
    var activity:Activity?=null
    var rootView: View?=null
    val perfUtil by lazy { PerfUtil(requireActivity()) }
    val gson by lazy { Gson() }
    

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.activity=getActivity()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.tryCreateViewBindingAndViewModel(container)
        if (this.binding != null) {
            this.rootView = this.binding!!.root
        } else {
            this.rootView = inflater.inflate(this.setContentLayoutId(), container, false)
            this.rootView!!.isClickable = true
        }
        if(enableEventBus()){
            EventBusUtils.register(this)
        }
        return this.rootView
    }

    private fun tryCreateViewBindingAndViewModel(parent: ViewGroup?) {
        val owner: ViewModelStoreOwner=if(initViewModelOwner()) requireActivity() else this
        try {
            val type = this.javaClass.genericSuperclass
            if (type is ParameterizedType) {
                val types = type.actualTypeArguments
                if (types.isNotEmpty()) {
                    this.binding = (types[0] as Class<*>).getDeclaredMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java).invoke(null as Any?, this.layoutInflater,parent,false) as V
                    this.viewModel = ViewModelProvider(owner).get(types[1] as Class<VM>)
                }
            }
        } catch (var5: Exception) {
            var5.printStackTrace()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.initView(savedInstanceState)
        this.viewModelHelper()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        this.binding=null
        if(enableEventBus()){
            EventBusUtils.unregister(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        this.rootView=null
        this.viewModel=null
    }


    abstract fun setContentLayoutId(): Int
    abstract fun initView(savedInstanceState: Bundle?)
    open fun enableEventBus():Boolean{
        return false
    }

    /**
    * 该ViewModel是否activity持有，
     * true为activity持有（activity与fragment之间可以数据共享）
     * false为fragment自身持有（单独实例，数据不能共享）
     * 默认为true
    * */
    open fun initViewModelOwner(): Boolean{
        return true
    }

    @Subscribe(threadMode= ThreadMode.MAIN)
    open fun eventMessage(event: Event){

    }



    /**
     * 简单封装toast,其他重用度高的代码都可提取出来统一封装
     * */
    fun showToast(msg:String){
        Toast.makeText(activity,msg, Toast.LENGTH_SHORT).show()
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
        viewModel?.toastMessage?.observe(viewLifecycleOwner,{
            showToast(it)
        })
        viewModel?.progressMessage?.observe(viewLifecycleOwner,{
            if(it=="dismiss"){
                dismissProgress()
            }else{
                showProgress(it)
            }
        })
    }

    /**
     * 跳转页面封装
     * */
    fun xStartActivity(cls: Class<*>) {
        startActivity(Intent(requireContext(),cls))
    }
    /**
     * 跳转页面携带数字参数封装
     * */
    fun xStartActivity(cls: Class<*>,name:String,dataInt:Int) {
        startActivity(Intent(requireContext(),cls).putExtra(name,dataInt))
    }
    /**
     * 跳转页面携带字符参数封装
     * */
    fun xStartActivity(cls: Class<*>,name:String,dataString:String) {
        startActivity(Intent(requireContext(),cls).putExtra(name,dataString))
    }
    /**
     * 跳转页面携带对象参数封装
     * */
    fun xStartActivity(cls: Class<*>,name:String,dataObject:Any) {
        startActivity(Intent(requireContext(),cls).putExtra(name,gson.toJson(dataObject)))
    }
}