package com.misterm.sharebye

import android.app.ProgressDialog.show
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PerformanceHintManager
import android.provider.Settings
import android.widget.ProgressBar
import android.widget.Toast
import com.misterm.sharebye.Helper.PermissionHelper
import com.misterm.sharebye.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var _binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        var binding = _binding
        setContentView(binding.root)

        var message = intent.extras?.get("MESSAGES") as String?
        if (!message.isNullOrEmpty()){
            Toast.makeText(this,"$message",Toast.LENGTH_SHORT).show()
        }

        binding.btnSend.setOnClickListener {
            if (PermissionHelper().hasStoragePermission(
                    this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    0
                )
            ) {
                startActivity(Intent(this, CheckedActivity::class.java).apply {
                    putExtra("MODE", "SENDER")
                })
            }
        }
        binding.btnReceive.setOnClickListener {
            if (PermissionHelper().hasStoragePermission(
                    this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    0
                )
            ) {
                startActivity(Intent(this, CheckedActivity::class.java).apply {
                    putExtra("MODE", "RECEIVED")
                })
            }
        }
    }
}