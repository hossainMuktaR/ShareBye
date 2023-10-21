package com.misterm.sharebye

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ScanMode
import com.misterm.sharebye.Helper.PermissionHelper
import com.misterm.sharebye.ViewModel.FtpVModel
import com.misterm.sharebye.databinding.ActivityScannerBinding
import java.io.IOException

class ScannerActivity : AppCompatActivity() {
    lateinit var _binding: ActivityScannerBinding
    lateinit var codeScanner: CodeScanner
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityScannerBinding.inflate(layoutInflater)
        var binding = _binding
        setContentView(binding.root)
        val permissionHelper = PermissionHelper()
        val sharedPref = getSharedPreferences("FTPPrefs", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        val viewModel = ViewModelProvider(this).get(FtpVModel::class.java)
        var serverNotFound = true
        var currentScan = 0
        var scanDead = 1
        viewModel.getisConnected().observe(this, Observer {
            if (currentScan.equals(scanDead)) {
                if (it) {
                    startActivity(Intent(this, FileListActivity::class.java).apply {
                        val path = Environment.getExternalStorageDirectory().absolutePath
                        putExtra("PATH", path)
                        putExtra("ROOTACTIVITY",true)
                        finish()
                    })
                }


            }
        })
        if (permissionHelper.hasPermissonSingle(this, android.Manifest.permission.CAMERA, 100)) {
            codeScanner = CodeScanner(this, binding.scannerView)
            codeScanner.apply {
                camera = CodeScanner.CAMERA_BACK
                formats = CodeScanner.ALL_FORMATS

                autoFocusMode = AutoFocusMode.SAFE
                scanMode = ScanMode.CONTINUOUS
                isAutoFocusEnabled = true
                isFlashEnabled = false
                decodeCallback = DecodeCallback {
                    if (serverNotFound) {
                        val result = it.text.split(",")
                        if (result.size == 3) {
                            if (currentScan < scanDead) {
                                try {
                                    val ftpClient =
                                        FtpNetworkClient(result[0],
                                            result[1],
                                            result[2])
                                    ftpClient.connectClient()
                                    if (ftpClient.isConnected()) {
                                        currentScan++
                                        Log.d("ftp", currentScan.toString())
                                    } else {
                                        Log.d("ftp", "not Connected")
                                    }
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                    Log.e("ftp", "Server Not Found")
                                    serverNotFound = false
                                }

                            } else {
                                runOnUiThread {
                                    if ((viewModel._isConnected.value != true)&& (currentScan.equals(scanDead)) ){
                                        editor.apply {
                                            putString("HOST", result[0])
                                            putString("NAME", result[1])
                                            putString("PASS", result[2])
                                            apply()
                                            Log.d("ftp", "Shared Pref saved")
                                        }
                                        viewModel.setisConnected(true)
                                    }
                                }
                            }

                        } else {
                            runOnUiThread {
                                Toast.makeText(this@ScannerActivity,
                                    "Worng Qr Code",
                                    Toast.LENGTH_SHORT).show()
                            }

                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@ScannerActivity,
                                "Server Not Found",
                                Toast.LENGTH_SHORT)
                                .show()
                            serverNotFound = false
                        }

                    }
                }
            }



            binding.scannerView.setOnClickListener {
                codeScanner.startPreview()
                Toast.makeText(this, "Code Scanner Run", Toast.LENGTH_SHORT).show()
            }

        } else {
            Toast.makeText(this, "You Don't Have Camera Permission yet", Toast.LENGTH_SHORT).show()

        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }
}