package com.misterm.sharebye

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.misterm.sharebye.Adapter.FileListAdapter
import com.misterm.sharebye.databinding.ActivityFileListBinding
import org.apache.commons.net.ftp.FTPFile
import org.apache.ftpserver.ftplet.FtpException
import java.io.File
import java.util.*
import java.util.Arrays.sort
import java.util.Collections.sort

class FileListActivity : AppCompatActivity() {
    lateinit var _binding: ActivityFileListBinding
    var isMainActivity = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityFileListBinding.inflate(layoutInflater)
        val binding = _binding
        setContentView(binding.root)
        var fileFolderArray: Array<File>
        val sharedPref = getSharedPreferences("FTPPrefs", Context.MODE_PRIVATE)
        val host = sharedPref.getString("HOST", null) ?: ""
        val uName = sharedPref.getString("NAME", null) ?: ""
        val uPass = sharedPref.getString("PASS", null) ?: ""

        val isPath = intent.extras?.get("PATH") as String
        isMainActivity = intent.extras?.get("ROOTACTIVITY") as Boolean
        Log.d("ftp", isPath)
        if (!isPath.isNullOrEmpty()) {
            Log.d("ftp", "$host\n$uName\n$uPass")
            if (host.isNullOrEmpty() && uName.isNullOrEmpty() && uPass.isNullOrEmpty()) {
                Log.d("ftp", "Qr Failed")
                finish()
            } else {
                val rootFile = File(isPath)
                fileFolderArray = rootFile.listFiles()
                if (fileFolderArray.isNullOrEmpty()){
                    binding.tvNoFile.visibility = View.VISIBLE
                    binding.recyclerViewFileList.visibility = View.INVISIBLE
                }else{
                    binding.recyclerViewFileList.adapter = FileListAdapter(this,fileFolderArray)
                }
            }
        }
    }

    override fun onBackPressed() {
        if (isMainActivity) {
            val alertDialog = AlertDialog.Builder(this,R.style.AlertDialogTheme)
                .setTitle("Exit")
                .setMessage("Are you sure to Exit")
                .setPositiveButton("Yes") { _, _ ->
                    super.onBackPressed()
                }
                .setNegativeButton("No", null)
                .create()
            alertDialog.show()
        } else {
            super.onBackPressed()
        }
    }
}
