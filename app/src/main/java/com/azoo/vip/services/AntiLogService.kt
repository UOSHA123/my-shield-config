package com.azoo.vip.services

import com.azoo.vip.core.ProtectionCore
import android.util.Log

object AntiLogService {
    private const val TAG = "AntiLogService"
    private val GAME_PACKAGES = listOf("com.tencent.ig", "com.pubg.krmobile", "com.vng.pubgmobile")

    fun clearAllLogs(useRoot: Boolean): Boolean {
        var success = true
        for (pkg in GAME_PACKAGES) {
            val cachePath = "/data/data/$pkg/cache"
            val filesPath = "/data/data/$pkg/files"
            
            Log.d(TAG, "Clearing logs for $pkg")
            
            // Clear Cache
            ProtectionCore.runCommand("rm -rf $cachePath/*", useRoot)
            
            // Clear and Lock Logs in files directory
            ProtectionCore.runCommand("rm -rf $filesPath/logs/*", useRoot)
            ProtectionCore.runCommand("chmod 444 $filesPath/logs", useRoot)
            
            // Add more specific paths if needed
        }
        return success
    }

    fun unlockLogs(useRoot: Boolean) {
        for (pkg in GAME_PACKAGES) {
            val filesPath = "/data/data/$pkg/files"
            ProtectionCore.runCommand("chmod 777 $filesPath/logs", useRoot)
        }
    }
}
