package com.misterm.sharebye

import android.content.Context
import android.os.Bundle
import android.os.FileUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.misterm.sharebye.databinding.ActivityTransferBinding
import org.apache.commons.net.ftp.FTP
import org.apache.ftpserver.ftplet.FtpException
import java.io.*


class TransferActivity : AppCompatActivity() {
    lateinit var _binding: ActivityTransferBinding
    var fileOperationDone = false
    var inputStream: FileInputStream? = null
    var outputStream: OutputStream? = null
    var workingThread: Thread? = null
    var isRead = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityTransferBinding.inflate(layoutInflater)
        var binding = _binding
        setContentView(binding.root)

        val sharedPref = getSharedPreferences("FTPPrefs", Context.MODE_PRIVATE)
        val host = sharedPref.getString("HOST", null) ?: ""
        val uName = sharedPref.getString("NAME", null) ?: ""
        val uPass = sharedPref.getString("PASS", null) ?: ""

        binding.btnSendAgain.setOnClickListener {
            finish()
        }
        val filePath = intent?.extras?.get("PATH") as String?
        if (filePath.isNullOrEmpty()) {
            finish()
        } else {
            val file = File(filePath)
            binding.tvFileName.text = file.name
            Log.d("ftp", "${file.name}")
            inputStream = FileInputStream(file)
            fileOperationDone = false
            workingThread = Thread {
                run {
                    try {
                        val ftpClient = FtpNetworkClient(host, uName, uPass)
                        ftpClient.connectClient()
                        if (ftpClient.isConnected()) {
                            ftpClient.client.setFileType(FTP.BINARY_FILE_TYPE)
                            var haveDirectory = false
                            if (ftpClient.client.cwd("/ShareBye/") == 550) {
                                ftpClient.client.makeDirectory("/ShareBye/")
                                haveDirectory = true
                            } else {
                                haveDirectory = true
                            }
                            if (haveDirectory) {
                                if (ftpClient.client.mlistFile("/ShareBye/${file.name}") == null) {
                                    outputStream =
                                        ftpClient.client.storeFileStream("/ShareBye/${file.name}")

                                    val lenghtOfFile = file.length()
                                    val buf = ByteArray(DEFAULT_BUFFER_SIZE)
                                    var len: Int
                                    var total: Long = 0
                                    while (inputStream!!.read(buf).also { len = it } != -1) {
                                        total += len
                                        runOnUiThread {
                                            binding.progressBar.progress =
                                                ((total * 100 / lenghtOfFile).toInt())

                                        }
                                        outputStream!!.write(buf, 0, len)
                                    }
                                    outputStream!!.flush()
                                    inputStream!!.close()
                                    outputStream!!.close()

                                    if (ftpClient.client.completePendingCommand()) {

                                        runOnUiThread {
                                            fileOperationDone = true
                                            binding.btnSendAgain.visibility = View.VISIBLE
                                            binding.tvFileName.text = "Sending Done"
                                            binding.progressBar.visibility = View.INVISIBLE
                                            Toast.makeText(this@TransferActivity, "file Send Done",
                                                Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        Log.d("ftp", "Sending Error")
                                        fileOperationDone = false
                                    }
                                } else {
                                    fileOperationDone = false
                                    Log.d("ftp", "File Exits")
                                }
                            }
                        } else {
                            ftpClient.disconnected()
                        }
                    } catch (e: FtpException) {
                        e.printStackTrace()
                    }
                }
            }
            workingThread?.start()
        }
    }

    override fun onBackPressed() {
        if (!fileOperationDone) {
            val alertDialog = AlertDialog.Builder(this,R.style.AlertDialogTheme)
                .setTitle("Exit")
                .setMessage("Are you sure to Exit")
                .setPositiveButton("Yes") { _, _ ->
                    finish()
                }
                .setNegativeButton("No", null)
                .create()
            alertDialog.show()
        } else {
            super.onBackPressed()
        }
    }
}