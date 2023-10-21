package com.misterm.sharebye.Services

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.util.Log
import android.widget.BaseExpandableListAdapter
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelProvider
import com.misterm.sharebye.Helper.StateCheckHelper
import com.misterm.sharebye.QrgActivity
import com.misterm.sharebye.R
import com.misterm.sharebye.ViewModel.FtpVModel
import org.apache.ftpserver.ConnectionConfigFactory
import org.apache.ftpserver.FtpServer
import org.apache.ftpserver.FtpServerFactory
import org.apache.ftpserver.ftplet.*
import org.apache.ftpserver.listener.ListenerFactory
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory
import org.apache.ftpserver.usermanager.impl.BaseUser
import org.apache.ftpserver.usermanager.impl.WritePermission
import java.io.IOException

class FTPService() :
    Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    lateinit var MainServer: FtpServer
    val CHANNEL_ID = "Channel ID"
    val CHANNEL_NAME = "FTP State"
    val BasePath = Environment.getExternalStorageDirectory().absolutePath
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("FTP","onStartCommand Call")

        val Name = intent?.extras?.get("NAME").toString()
        val Pass = intent?.extras?.get("PASSWORD").toString()
        if((intent != null)&&(Name != null)&&(Pass != null)){
            serverConfiguration(Name,Pass)
        }
        createNotificationChannel()
        val ipAddress = StateCheckHelper(this).getIpAddress(this,true)
        val contentText = BasePath+"\n"+ipAddress+":2121"
        startForeground(1,createNotification(contentText).build())
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        if (MainServer != null) {
            MainServer.stop()
            Log.d("FTP","MainServer Stoped")

        }
        Log.d("FTP","onDestroy call")
        stopSelf()
        super.onDestroy()

    }

    private fun serverConfiguration(userName:String, password: String) {
        val serverFactory = FtpServerFactory()
        val listenerFactory = ListenerFactory()
        val connectionConfigFactory = ConnectionConfigFactory()
        listenerFactory.port = 2121
        serverFactory.addListener("default", listenerFactory.createListener())
        // User
        val userManagerFactory = PropertiesUserManagerFactory()
        val user = BaseUser()
        user.name = userName
        user.password = password
        connectionConfigFactory.isAnonymousLoginEnabled = false
        user.homeDirectory = BasePath
        // Authorities
        val authorities = ArrayList<Authority>()
        authorities.add(WritePermission())
        user.authorities = authorities
        val userManager = userManagerFactory.createUserManager()
        try {
            userManager.save(user)
        } catch (e: FtpException) {
            e.printStackTrace()
        }

        serverFactory.userManager = userManager
        serverFactory.connectionConfig = connectionConfigFactory.createConnectionConfig()

        val m = HashMap<String, Ftplet>()
        m["misterMFtplet"] = object : Ftplet {

            @Throws(FtpException::class)
            override fun init(ftpletContext: FtpletContext) {
                System.out.println("init");
                System.out.println("Thread #" + Thread.currentThread().getId());
            }

            override fun destroy() {
                System.out.println("destroy");
                System.out.println("Thread #" + Thread.currentThread().getId());
            }

            @Throws(FtpException::class, IOException::class)
            override fun beforeCommand(session: FtpSession, request: FtpRequest): FtpletResult {
                System.out.println("beforeCommand " + session.getUserArgument() + " : " + session.toString() + " | " + request.getArgument() + " : " + request.getCommand() + " : " + request.getRequestLine());
                System.out.println("Thread #" + Thread.currentThread().getId());

                //do something
                return FtpletResult.DEFAULT//...or return accordingly
            }

            @Throws(FtpException::class, IOException::class)
            override fun afterCommand(
                session: FtpSession,
                request: FtpRequest,
                reply: FtpReply
            ): FtpletResult {
                System.out.println("afterCommand " + session.getUserArgument() + " : " + session.toString() + " | " + request.getArgument() + " : " + request.getCommand() + " : " + request.getRequestLine() + " | " + reply.getMessage() + " : " + reply.toString());
                System.out.println("Thread #" + Thread.currentThread().getId());

                //do something
                return FtpletResult.DEFAULT//...or return accordingly
            }

            @Throws(FtpException::class, IOException::class)
            override fun onConnect(session: FtpSession): FtpletResult {
                System.out.println("onConnect " + session.getUserArgument() + " : " + session.toString());System.out.println(
                    "Thread #" + Thread.currentThread().getId()
                );

                //do something
                return FtpletResult.DEFAULT//...or return accordingly
            }

            @Throws(FtpException::class, IOException::class)
            override fun onDisconnect(session: FtpSession): FtpletResult {
                System.out.println("onDisconnect " + session.getUserArgument() + " : " + session.toString());
                System.out.println("Thread #" + Thread.currentThread().getId());

                //do something
                return FtpletResult.DEFAULT//...or return accordingly
            }
        }

        MainServer = serverFactory.createServer()
        try {
            MainServer.start()
        } catch (e: FtpException) {
            e.printStackTrace()
        }

    }
    private fun createNotification(contentText : String): NotificationCompat.Builder {
        val notificationIntent = Intent(this, QrgActivity::class.java)
        val pendingIntent = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(notificationIntent)
            getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
        }
        val notification = NotificationCompat.Builder(this, CHANNEL_ID).apply {
            setContentTitle("ShareBye Receiver")
            setContentText(contentText)
            setSmallIcon(R.drawable.notification_icon_black)
            setContentIntent(pendingIntent)
        }
        return notification
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}