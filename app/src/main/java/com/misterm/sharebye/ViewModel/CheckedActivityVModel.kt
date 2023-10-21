package com.misterm.sharebye.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CheckedActivityVModel:ViewModel() {

    var _dataWifiOn = MutableLiveData<Boolean>()
    var _hotspotOn = MutableLiveData<Boolean>()
    var _senderMode = MutableLiveData<Boolean>()
    var _nextButton = MutableLiveData<Boolean>()

    fun setDataWifiNewState(state: Boolean){
        _dataWifiOn.value = state
    }
    fun getisDataWifiON(): MutableLiveData<Boolean> {
        return _dataWifiOn
    }

    fun setHotspotNewState(state : Boolean){
        _hotspotOn.value = state
    }
    fun getisHotspotOn():MutableLiveData<Boolean>{
        return _hotspotOn
    }
    fun setSenderMode(UserMode : Boolean){
        _senderMode.value = UserMode
    }
    fun getSenderMode(): MutableLiveData<Boolean>{
        return _senderMode
    }
    fun setNextbtnVisibility(visibility :Boolean){
        _nextButton.value = visibility
    }
    fun getNextbtnVisibility(): MutableLiveData<Boolean>{
        return _nextButton
    }

    override fun onCleared() {
        super.onCleared()
    }
}