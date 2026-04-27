package com.ata.shield

import android.content.Context
import rikka.shizuku.Shizuku
import java.io.DataOutputStream

object BypassCore {
    init {
        System.loadLibrary("native-lib")
    }

    external fun applyMemoryPatch()

    fun executeBypass(context: Context) {
        val commands = listOf(
            "echo \"+CVars=r.PUBGDeviceFPSHigh=90\" >> /data/data/com.tencent.ig/files/UE4Game/ShadowTrackerExtra/ShadowTrackerExtra/Saved/Config/Android/UserCustom.ini",
            "pm grant ${context.packageName} android.permission.WRITE_SECURE_SETTINGS"
        )
        
        if (Shizuku.pingBinder()) {
            val process = Runtime.getRuntime().exec("sh")
            DataOutputStream(process.outputStream).use { os ->
                commands.forEach { cmd ->
                    os.writeBytes("$cmd\n")
                }
                os.writeBytes("exit\n")
                os.flush()
            }
        }
    }
}
