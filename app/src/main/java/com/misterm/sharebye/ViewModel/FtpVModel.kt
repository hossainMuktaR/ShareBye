package com.misterm.sharebye.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FtpVModel():ViewModel() {
    var _host = MutableLiveData<String>()
    var _userName = MutableLiveData<String>()
    var _userPass = MutableLiveData<String>()
    var _isConnected = MutableLiveData<Boolean>()

    fun sethost(data: String){
        _host.value = data
    }
    fun gethost(): MutableLiveData<String>{
        return _host
    }
    fun setUserName(data: String){
        _userName.value = data
    }
    fun getUserName(): MutableLiveData<String>{
        return _userName
    }
    fun setUserPassword(data: String){
        _userPass.value = data
    }
    fun getUserPassword(): MutableLiveData<String>{
        return _userPass
    }
    fun setisConnected(data: Boolean){
        _isConnected.value = data
    }
    fun getisConnected(): MutableLiveData<Boolean>{
        return _isConnected
    }

    override fun onCleared() {
        super.onCleared()
    }

}