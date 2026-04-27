package com.azoo.vip.network

import android.net.VpnService
import android.os.ParcelFileDescriptor
import android.util.Log
import java.net.InetAddress

class VpnHandler : VpnService() {
    private var vpnInterface: ParcelFileDescriptor? = null

    override fun onStartCommand(intent: android.content.Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        if (action == "STOP") {
            stopVpn()
            return START_NOT_STICKY
        }
        startVpn()
        return START_STICKY
    }

    private fun startVpn() {
        val builder = Builder()
        builder.setSession("AZOO VIP SAUDI")
            .addAddress("10.8.0.2", 32)
            .addRoute("15.185.0.0", 16) // Route for Saudi AWS range as an example
            .addRoute("15.177.0.0", 16)
            .addDnsServer("1.1.1.1")
            .setMtu(1400)
            .setBlocking(false)

        try {
            vpnInterface = builder.establish()
            Log.d("VpnHandler", "Saudi VPN Tunnel Established")
        } catch (e: Exception) {
            Log.e("VpnHandler", "Failed to establish VPN", e)
        }
    }

    private fun stopVpn() {
        vpnInterface?.close()
        vpnInterface = null
        stopSelf()
    }

    override fun onDestroy() {
        stopVpn()
        super.onDestroy()
    }
}
