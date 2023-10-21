package com.misterm.sharebye.Adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.misterm.sharebye.FileListActivity
import com.misterm.sharebye.FtpNetworkClient
import com.misterm.sharebye.R
import com.misterm.sharebye.TransferActivity
import com.misterm.sharebye.databinding.FileListItemBinding
import org.apache.commons.net.ftp.FTPFile
import org.apache.ftpserver.ftplet.FtpFile
import java.io.File

class FileListAdapter(val context: Context, val folderFileArray: Array<File>?):RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(FileListItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val selectedFile = folderFileArray!!.get(position)
        val binding = holder.binding
        binding.fileFolderName.text = selectedFile.name

        if(selectedFile.isDirectory){
            binding.iconView.setImageResource(R.drawable.ic_folder_24)
            binding.imItemSendButton.visibility = View.GONE
        }else{
            binding.iconView.setImageResource(R.drawable.ic_file_24)
            binding.imItemSendButton.visibility = View.VISIBLE
        }
        binding.parentitem.setOnClickListener {
            if (selectedFile.isDirectory && !(selectedFile.name == "obb"||selectedFile.name=="data")){
                val path = selectedFile.absolutePath
                context.startActivity(Intent(context,FileListActivity::class.java).apply {
                    putExtra("PATH",path)
                    putExtra("ROOTACTIVITY",false)
                })
            }
        }
        binding.imItemSendButton.setOnClickListener {
            if (!selectedFile.isDirectory){
                context.startActivity(Intent(context,TransferActivity::class.java).apply {
                    putExtra("PATH",selectedFile.absoluteFile.toString())
                })
            }
        }
        binding.parentitem.setOnLongClickListener { v : View? ->
            if (!selectedFile.isDirectory){
                val popupMenu = PopupMenu(context, v)
                popupMenu.menu.add("Send")

                popupMenu.setOnMenuItemClickListener {
                    when(it.toString()){
                        "Send" ->{
                            Toast.makeText(context,"Send Button Clicked",Toast.LENGTH_SHORT).show()
                            context.startActivity(Intent(context,TransferActivity::class.java).apply {
                                putExtra("PATH",selectedFile.absoluteFile.toString())
                            })
                        }
                    }
                    return@setOnMenuItemClickListener true
                }
                popupMenu.show()
            }
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount(): Int {
        return folderFileArray?.size?:0
    }
}
class ViewHolder(val binding: FileListItemBinding):RecyclerView.ViewHolder(binding.root)