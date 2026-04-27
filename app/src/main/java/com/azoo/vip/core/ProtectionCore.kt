package com.azoo.vip.core

import android.content.Context
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.ipc.RootService
import rikka.shizuku.Shizuku
import android.content.pm.PackageManager
import android.util.Log

object ProtectionCore {
    private const val TAG = "ProtectionCore"

    init {
        System.loadLibrary("azoovip")
        // Initialize libsu with a high-performance shell
        Shell.getShell()
    }

    external fun getNativeStatus(): String
    fun isRootAvailable(): Boolean {
        return Shell.getShell().isRoot
    }

    fun getGamePid(): Int {
        val out = runCommand("pidof com.tencent.ig", isRootAvailable())
        return if (out.isNotEmpty()) {
            try { out[0].trim().split(" ")[0].toInt() } catch (e: Exception) { -1 }
        } else -1
    }

    fun applyMemoryBypass(): Boolean {
        val pid = getGamePid()
        if (pid != -1) {
            Log.i(TAG, "Attempting bypass on PID: $pid")
            return nativeApplyMemoryBypass(pid)
        }
        Log.e(TAG, "Target process not found")
        return false
    }

    fun startProtectionService() {
        Log.i(TAG, "Starting God Level Protection...")
        // Real implementation: Hook system calls or monitor suspicious processes
    }

    private external fun nativeApplyMemoryBypass(pid: Int): Boolean
    external fun nativeScanAndPatch(pid: Int)

    fun isShizukuAvailable(context: Context): Boolean {
        return try {
            Shizuku.pingBinder() && Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
        } catch (e: Exception) {
            false
        }
    }

    fun runCommand(command: String, useRoot: Boolean): List<String> {
        if (useRoot && isRootAvailable()) {
            return Shell.cmd(command).exec().out
        } else if (Shizuku.pingBinder()) {
            try {
                val process = Shizuku.newProcess(arrayOf("sh", "-c", command), null, null)
                val out = process.inputStream.bufferedReader().readLines()
                process.waitFor()
                return out
            } catch (e: Exception) {
                Log.e(TAG, "Shizuku Command Failed: $command", e)
            }
        }
        // Fallback for non-root, non-shizuku (limited)
        return Shell.cmd(command).exec().out
    }

    fun getDeviceInfo(): String {
        return "Model: ${android.os.Build.MODEL} | Android: ${android.os.Build.VERSION.RELEASE}"
    }
}
