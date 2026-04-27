package com.azoo.vip.ui

import android.webkit.JavascriptInterface
import android.util.Log
import com.azoo.vip.core.ProtectionCore
import com.azoo.vip.services.AntiLogService
import com.azoo.vip.network.NetworkManager
import com.azoo.vip.core.FilePatcher

import android.content.Intent
import com.azoo.vip.network.VpnHandler

class AndroidBridge(private val activity: MainActivity) {

    private val isRootAvailable: Boolean by lazy { ProtectionCore.isRootAvailable() }

    @JavascriptInterface
    fun toggleFeature(name: String, status: Boolean, value: String = "") {
        Log.d("AndroidBridge", "toggleFeature: $name -> $status (val: $value)")
        when (name.lowercase()) {
            "anti_ban", "memory_bypass" -> toggleAntiBan(status)
            "anti_log" -> if (status) cleanLogs() else unlockLogs()
            "vpn" -> setVpn(status)
            "network_booster" -> if (status) NetworkManager.optimizeNetwork(isRootAvailable) else NetworkManager.resetDns(isRootAvailable)
            "ultimate_power" -> setUltimatePower(status)
            "dpi_changer" -> {
                val dpi = value.toIntOrNull() ?: 360
                if (status) changeDPI(dpi) else resetDPI()
            }
            "90fps" -> set90FPS(status)
            "launch_game" -> launchGame()
        }
    }

    @JavascriptInterface
    fun getSystemInfo(): String {
        return ProtectionCore.getDeviceInfo()
    }

    @JavascriptInterface
    fun checkServiceStatus(name: String): Boolean {
        return when (name) {
            "root" -> isRootAvailable
            "shizuku" -> ProtectionCore.isShizukuAvailable(activity)
            "game_running" -> ProtectionCore.getGamePid() != -1
            else -> false
        }
    }

    private fun toggleAntiBan(status: Boolean) {
        if (status) {
            FilePatcher.redirectLogs(isRootAvailable)
            ProtectionCore.applyMemoryBypass()
        }
    }

    private fun cleanLogs() {
        AntiLogService.clearAllLogs(isRootAvailable)
    }

    private fun unlockLogs() {
        AntiLogService.unlockLogs(isRootAvailable)
    }

    private fun setVpn(active: Boolean) {
        val intent = Intent(activity, VpnHandler::class.java)
        if (active) {
            activity.startService(intent)
        } else {
            intent.action = "STOP"
            activity.startService(intent)
        }
    }

    private fun setUltimatePower(active: Boolean) {
        val governor = if (active) "performance" else "schedutil"
        val cmds = mutableListOf<String>()
        for (i in 0..7) {
            cmds.add("echo $governor > /sys/devices/system/cpu/cpu$i/cpufreq/scaling_governor")
        }
        if (active) {
            cmds.add("echo 0 > /sys/class/thermal/thermal_message/sconfig")
            cmds.add("setprop debug.composition.type gpu")
        }
        cmds.forEach { ProtectionCore.runCommand(it, isRootAvailable) }
    }

    private fun set90FPS(active: Boolean) {
        if (active) {
            FilePatcher.patch90FPS(isRootAvailable)
        }
    }

    private fun changeDPI(dpi: Int) {
        NetworkManager.setDpi(dpi, isRootAvailable)
    }

    private fun resetDPI() {
        NetworkManager.resetDpi(isRootAvailable)
    }

    private fun launchGame() {
        val packageName = "com.tencent.ig"
        val intent = activity.packageManager.getLaunchIntentForPackage(packageName)
        if (intent != null) {
            activity.startActivity(intent)
        } else {
            val versions = listOf("com.pubg.krmobile", "com.vng.pubgmobile")
            for (v in versions) {
                val it = activity.packageManager.getLaunchIntentForPackage(v)
                if (it != null) {
                    activity.startActivity(it)
                    break
                }
            }
        }
    }
}
