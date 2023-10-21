package com.misterm.sharebye

import android.content.ComponentName
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.misterm.sharebye.Helper.PermissionHelper
import com.misterm.sharebye.Helper.StateCheckHelper
import com.misterm.sharebye.Services.FTPService
import com.misterm.sharebye.ViewModel.CheckedActivityVModel
import com.misterm.sharebye.databinding.ActivityCheckedBinding

class CheckedActivity : AppCompatActivity() {
    lateinit var _binding: ActivityCheckedBinding
    var DataWifiOn: Boolean = false
    var HotspotOn: Boolean = false
    val permissionHelper = PermissionHelper()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCheckedBinding.inflate(layoutInflater)
        var binding = _binding
        var stateCheckHelper = StateCheckHelper(this)
        var viewModel = ViewModelProvider(this).get(CheckedActivityVModel::class.java)

        setContentView(binding.root)

        viewModel.getisDataWifiON().observe(this, Observer {
            DataWifiOn = it
            if (viewModel._senderMode.value == true) {
                controllerOfWifiButton(binding, true)
            } else {
                controllerOfWifiButton(binding, false)
            }
        })
        viewModel.getisHotspotOn().observe(this, Observer {
            HotspotOn = it
            if (viewModel._senderMode.value == false) {
                controllerOfHotspotButton(binding)
            }
        })
        viewModel.getNextbtnVisibility().observe(this, Observer {
            if (it) {
                showNextButton(binding, it)
                if (viewModel._senderMode.value == true){
                    clickListenerForNextButton(binding,true)
                }else{
                    clickListenerForNextButton(binding, false)
                }
            } else {
                showNextButton(binding, it)
            }
        })
        var userType = intent.extras?.get("MODE")
        if (userType == "SENDER") {
            Toast.makeText(this, "You are now sender Mode", Toast.LENGTH_SHORT).show()
            viewModel.setSenderMode(true)
            hideHotspotLayout(binding)
        } else if (userType == "RECEIVED") {
            viewModel.setSenderMode(false)
        } else {
            Toast.makeText(this, "Mode Error", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            var stateCheckHelper = StateCheckHelper(this)
            var viewModel = ViewModelProvider(this).get(CheckedActivityVModel::class.java)
            if (viewModel._senderMode.value == true) {
                viewModel.setDataWifiNewState(
                    stateCheckHelper.hasWifiConnection(
                        applicationContext
                    )
                )
                if (viewModel._dataWifiOn.value == true){
                    viewModel.setNextbtnVisibility(true)
                }else{
                    viewModel.setNextbtnVisibility(false)
                }
            } else if (viewModel._senderMode.value == false) {
                viewModel.setDataWifiNewState(
                    stateCheckHelper.hasInternetConnection(
                        applicationContext
                    )
                )
                viewModel.setHotspotNewState(stateCheckHelper.getHotspotState(applicationContext))
                if ((viewModel._dataWifiOn.value == false) && (viewModel._hotspotOn.value == true)) {
                    viewModel.setNextbtnVisibility(true)
                } else {
                    viewModel.setNextbtnVisibility(false)
                }
            }

        }
    }

    fun controllerOfWifiButton(binding: ActivityCheckedBinding, isSender: Boolean) {
        if(isSender){
            binding.tvWifiText.text = "Wifi ON"
        }else{
            binding.tvWifiText.text = "Wifi Or Data Off"
        }
        if ((DataWifiOn != null) && DataWifiOn == isSender) {
            binding.btnWifiChecked.text = "Done"
            binding.btnWifiChecked.setTextColor(Color.parseColor("#A8A8A8"))
            binding.btnWifiChecked.background = null
            binding.btnWifiChecked.setOnClickListener {

            }
        } else {
            if (isSender) {
                binding.btnWifiChecked.text = "Turn On"
            } else {
                binding.btnWifiChecked.text = "Turn Off"
            }
            binding.btnWifiChecked.setTextColor(Color.parseColor("#FFFFFF"))
            binding.btnWifiChecked.setBackgroundResource(R.drawable.button_background)
            binding.btnWifiChecked.setOnClickListener {
                startActivity(Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY))

            }
        }
    }

    fun controllerOfHotspotButton(binding: ActivityCheckedBinding) {
        if ((HotspotOn != null) && HotspotOn == true) {
            binding.btnHotspotChecked.text = "Done"
            binding.btnHotspotChecked.setTextColor(Color.parseColor("#A8A8A8"))
            binding.btnHotspotChecked.background = null
            binding.btnHotspotChecked.setOnClickListener {
            }
        } else {
            binding.btnHotspotChecked.text = "Turn On"
            binding.btnHotspotChecked.setTextColor(Color.parseColor("#FFFFFF"))
            binding.btnHotspotChecked.setBackgroundResource(R.drawable.button_background)
            binding.btnHotspotChecked.setOnClickListener {
                Toast.makeText(this, "Clicked Turn ON Button", Toast.LENGTH_SHORT).show()
                startActivity(Intent(Intent.ACTION_MAIN, null).apply {
                    var componetName =
                        ComponentName("com.android.settings", "com.android.settings.TetherSettings")
                    setComponent(componetName)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                })
            }
        }
    }

    fun hideHotspotLayout(binding: ActivityCheckedBinding) {
        binding.lyHotspot.visibility = View.GONE
    }

    fun showNextButton(binding: ActivityCheckedBinding, isShow: Boolean) {
        if (isShow) {
            binding.btnNextChekced.visibility = View.VISIBLE
        } else {
            binding.btnNextChekced.visibility = View.INVISIBLE
        }

    }
    fun clickListenerForNextButton(binding: ActivityCheckedBinding, isSender: Boolean){
        if (isSender){
            if (permissionHelper.hasPermissonSingle(this, android.Manifest.permission.CAMERA, 100)){
                binding.btnNextChekced.setOnClickListener {
                    startActivity(Intent(this,ScannerActivity::class.java))
                    finish()
                }
            }else{
                Toast.makeText(this,"Your Don't have camera Permission",Toast.LENGTH_SHORT).show()
            }

        }else{
            binding.btnNextChekced.setOnClickListener {
                startActivity(Intent(this,QrgActivity::class.java))
                finish()
            }
        }
    }
}