package com.azoo.vip.core

import android.util.Log
import rikka.shizuku.Shizuku
import java.io.OutputStreamWriter

object FilePatcher {
    private const val TAG = "FilePatcher"
    private const val GAME_PATH = "/data/data/com.tencent.ig/files/UE4Game/ShadowTrackerExtra/ShadowTrackerExtra/Saved/Config/Android/UserCustom.ini"

    fun patch90FPS(useRoot: Boolean) {
        val config = "[UserCustom DeviceProfile]\\n+CVars=r.PUBGDeviceFPSLow=60\\n+CVars=r.PUBGDeviceFPSMid=90\\n+CVars=r.PUBGDeviceFPSHigh=90\\n+CVars=r.PUBGActiveHighFrameRate=90\\n"
        val cmd = "echo '$config' > $GAME_PATH"
        ProtectionCore.runCommand(cmd, useRoot)
        Log.d(TAG, "90 FPS Forced Injection Completed.")
    }

    fun redirectLogs(useRoot: Boolean) {
        val logPaths = listOf(
            "/data/data/com.tencent.ig/cache",
            "/data/data/com.tencent.ig/files/UE4Game/ShadowTrackerExtra/ShadowTrackerExtra/Saved/Logs"
        )
        logPaths.forEach { path ->
            val cmd = "rm -rf $path && mkdir -p $path && chmod 000 $path"
            ProtectionCore.runCommand(cmd, useRoot)
        }
    }
}
