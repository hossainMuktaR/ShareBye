package com.misterm.sharebye

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.misterm.sharebye.Helper.StateCheckHelper
import com.misterm.sharebye.Services.FTPService
import com.misterm.sharebye.databinding.ActivityQrgBinding

class QrgActivity : AppCompatActivity() {
    lateinit var _binding: ActivityQrgBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityQrgBinding.inflate(layoutInflater)
        var binding = _binding
        setContentView(binding.root)
        val Host = StateCheckHelper(this).getIpAddress(this,true)
        val userName = "misterM"
        val userPassword = "misterM"

        val ftpIntent = Intent(this, FTPService::class.java).apply{
            putExtra("NAME",userName)
            putExtra("PASSWORD",userPassword)
        }
//        do this work when ftp services are off cause pending intent bug found
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(ftpIntent)
        } else {
            startService(ftpIntent)
        }

        binding.btnStopFtpServer.setOnClickListener {
            stopService(ftpIntent)
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
        var QrText = "$Host,$userName,$userPassword"

        var writer = MultiFormatWriter()
        try {
            var matrix = writer.encode(QrText, BarcodeFormat.QR_CODE, 250, 250)
            var encoder = BarcodeEncoder()
            var bitmap = encoder.createBitmap(matrix)
            binding.ivQrCode.setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        val alertDialog = AlertDialog.Builder(this,R.style.AlertDialogTheme)
            .setTitle("Exit")
            .setMessage("Are you sure to Exit")
            .setPositiveButton("Yes") { _, _ ->
                stopService(Intent(this, FTPService::class.java))
                startActivity(Intent(this,MainActivity::class.java))
                finish()
            }
            .setNegativeButton("No", null)
            .create()
        alertDialog.show()
    }
}