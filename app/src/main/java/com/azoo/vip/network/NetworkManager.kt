package com.azoo.vip.network

import com.azoo.vip.core.ProtectionCore
import android.util.Log

object NetworkManager {
    private const val TAG = "NetworkManager"

    fun applyDnsCloudflare(useRoot: Boolean): Boolean {
        Log.d(TAG, "Applying Cloudflare DNS (1.1.1.1)")
        
        // Using iptables to redirect DNS traffic (UDP 53) to Cloudflare
        val commands = listOf(
            "iptables -t nat -A OUTPUT -p udp --dport 53 -j DNAT --to-destination 1.1.1.1:53",
            "iptables -t nat -A OUTPUT -p tcp --dport 53 -j DNAT --to-destination 1.1.1.1:53",
            "setprop net.dns1 1.1.1.1",
            "setprop net.dns2 1.0.0.1"
        )
        
        commands.forEach { ProtectionCore.runCommand(it, useRoot) }
        return true
    }

    fun resetDns(useRoot: Boolean) {
        ProtectionCore.runCommand("iptables -t nat -F OUTPUT", useRoot)
    }

    fun optimizeNetwork(useRoot: Boolean) {
        if (!useRoot) return
        val commands = listOf(
            // TCP Tweaks for lower latency
            "setprop net.tcp.buffersize.wifi 4096,87380,110208,4096,16384,110208",
            "setprop net.tcp.buffersize.lte 4094,87380,1220608,4096,16384,1220608",
            "setprop net.tcp.buffersize.gprs 4092,87380,110208,4092,16384,110208",
            "setprop net.rmnet0.gw 1",
            "setprop net.dns1 1.1.1.1",
            "setprop net.dns2 8.8.8.8",
            "setprop ro.config.low_ram false",
            "setprop persist.sys.force_highendgfx true",
            // Prioritize game packets (TOS/DSCP)
            "iptables -t mangle -A OUTPUT -p udp --dport 10000:20000 -j DSCP --set-dscp 46",
            "iptables -t mangle -A OUTPUT -p udp --dport 17000:18000 -j DSCP --set-dscp 46"
        )
        commands.forEach { ProtectionCore.runCommand(it, true) }
    }

    fun setDpi(dpi: Int, useRoot: Boolean) {
        Log.d(TAG, "Setting DPI to $dpi")
        ProtectionCore.runCommand("wm density $dpi", useRoot)
    }

    fun resetDpi(useRoot: Boolean) {
        ProtectionCore.runCommand("wm density reset", useRoot)
    }
}
