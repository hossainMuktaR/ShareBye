package com.misterm.sharebye

import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPClient
import org.apache.commons.net.ftp.FTPFile
import org.apache.commons.net.ftp.FTPReply
import org.apache.ftpserver.ftplet.FtpException
import java.io.IOException

class FtpNetworkClient(val host: String, val UserName: String, val UserPass: String) {
    val client: FTPClient = FTPClient()

    @Throws(IOException::class)
    fun logout(): Boolean {
        return client.logout()
    }

    fun isAvailable(): Boolean {
        return client.isAvailable
    }

    fun isConnected(): Boolean {
        return client.isConnected
    }

    @Throws(IOException::class)
    fun disconnected() {
        return client.disconnect()
    }

    @Throws(IOException::class)
    fun login(username: String, userPassword: String): Boolean {
        return client.login(username, userPassword)
    }

    @Throws(IOException::class)
    fun connectClient(): Boolean {
        var isLogged = false
        try {
            client.autodetectUTF8 = true
            client.controlEncoding = "UTF-8"
            client.connect(host, 2121)
            client.setFileType(FTP.BINARY_FILE_TYPE)
            client.enterLocalPassiveMode()
            client.login(UserName, UserPass)
            var reply = client.replyCode
            if (!FTPReply.isPositiveCompletion(reply)) {
                client.disconnect()
                 isLogged = false
            }else{
                isLogged = true
            }
        }catch (e: FtpException){
            e.printStackTrace()
            isLogged = false
        }
        return isLogged

    }

    @Throws(IOException::class)
    fun getWorkingDirectory(): String {
        return client.printWorkingDirectory()
    }

    @Throws(IOException::class)
    fun changeWorkingDirectory(pathName: String) {
        client.changeWorkingDirectory(pathName)
    }

    @Throws(IOException::class)
    fun deleteFile(path: String): Boolean {
        return client.deleteFile(path)
    }

    @Throws(IOException::class)
    fun listFiles(): Array<out FTPFile>? {
        return client.listFiles()
    }

    @Throws(IOException::class)
    fun completePendingCommand(): Boolean {
        return client.completePendingCommand()
    }
}