package com.xqb.xqbutils.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class BaseViewModel : ViewModel() {
    val toastMessage =MutableLiveData<String>()
    val progressMessage=MutableLiveData<String>()

    fun showToast(msg:String){
        toastMessage.postValue(msg)
    }

    fun showProgress(msg:String){
        progressMessage.value=msg
    }
    fun disMissProgress(){
        progressMessage.value="dismiss"
    }
}