package com.misterm.sharebye.Helper

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import java.lang.reflect.Method
import java.net.NetworkInterface
import java.net.SocketException

class StateCheckHelper(val context: Context) {
    fun hasInternetConnection(appContext : Context): Boolean{
        val connectivityManager = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)?: return false
            return when{
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }else{
            connectivityManager.activeNetworkInfo?.run{
                return when(type){
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }
    fun hasWifiConnection(applicationContext : Context): Boolean{
        val connectivityManager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)?: return false
            return when{
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                else -> false
            }
        }else{
            connectivityManager.activeNetworkInfo?.run{
                return when(type){
                    ConnectivityManager.TYPE_WIFI -> true
                    else -> false
                }
            }
        }
        return false
    }
    fun getHotspotState(appContext : Context): Boolean {
        var state : Boolean = false
        val wifiManager =
            appContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val method: Method = wifiManager.javaClass.getMethod(
            "getWifiApState"
        )
        method.isAccessible = true
        val invoke = method.invoke(wifiManager)
        println(invoke)
        state = invoke == 13
        return state;
    }
    fun getIpAddress(ctxt: Context, verbose: Boolean): String? {
        var result : String?  = null
        try {
            val ifaces = NetworkInterface.getNetworkInterfaces()
            while (ifaces.hasMoreElements()) {
                val iface = ifaces.nextElement()
                val ifaceDispName = iface.displayName
//                Log.d("IPADD","Display Name : ${ifaceDispName}")
                val inetAddrs = iface.inetAddresses
//                Log.d("IPADD","iNetAddress : ${inetAddrs}")

                while (inetAddrs.hasMoreElements()) {
                    val inetAddr = inetAddrs.nextElement()
                    val hostAddr = inetAddr.hostAddress
//                    Log.d("IPADD","inet.hostAddress : ${hostAddr}")

                    if (inetAddr.isLoopbackAddress) {
                        continue
                    }
                    if (inetAddr.isAnyLocalAddress) {
                        continue
                    }
                    if (hostAddr.contains("::")) {
                        // Don't include the raw encoded names. Just the raw IP addresses.
                        continue
                    }
                    if (!verbose && !ifaceDispName.startsWith("wlan")) {
                        continue
                    }
                    var displayText = hostAddr
                    Log.d("IPADD","Display text : ${displayText}")
                    result = hostAddr
                }
            }
        } catch (e: SocketException) {
          e.printStackTrace()
        }
        return result
    }
}