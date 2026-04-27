package com.azoo.vip.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.azoo.vip.core.ProtectionCore
import com.azoo.vip.network.NetworkManager
import com.azoo.vip.services.AntiLogService
import com.ata.shield.BypassCore
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            XeletronTheme {
                MainScreen(this)
            }
        }
    }
}

@Composable
fun XeletronTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF10B981),
            background = Color(0xFF020617),
            surface = Color(0xFF0F172A)
        ),
        content = content
    )
}

@Composable
fun MainScreen(activity: MainActivity) {
    var deviceModel by remember { mutableStateOf("جاري الفحص...") }
    var gameStatus by remember { mutableStateOf("جاري الفحص...") }
    var isGameRunning by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        deviceModel = ProtectionCore.getDeviceInfo()
        // Sync with GitHub on startup
        com.azoo.vip.core.SirLionV.syncWithCloud(activity)

        while (true) {
            val pid = ProtectionCore.getGamePid()
            isGameRunning = pid != -1
            gameStatus = if (isGameRunning) "اللعبة تعمل (Protected) ✅" else "اللعبة متوقفة (Idle)"
            delay(2000)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF020617))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            HeaderSection()

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    FeatureCard(
                        title = "بروتوكول الحرية (Full Freedom)",
                        subtitle = "Execute all Xeletron protocols at once",
                        icon = "🔥",
                        onClick = { com.azoo.vip.core.SirLionV.fullFreedom(activity) }
                    )
                }

                item {
                    FeatureCard(
                        title = "مسرع الإنترنت (20MS)",
                        subtitle = "Stable Ping Protocol",
                        icon = "🚀",
                        onClick = { NetworkManager.optimizeNetwork(true) }
                    )
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        SmallFeatureCard(
                            modifier = Modifier.weight(1f),
                            title = "تخطي الباند",
                            icon = "🛡️",
                            onClick = { ProtectionCore.applyMemoryBypass() }
                        )
                        SmallFeatureCard(
                            modifier = Modifier.weight(1f),
                            title = "تنظيف السجلات",
                            icon = "🧹",
                            onClick = { AntiLogService.clearAllLogs(true) }
                        )
                    }
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        SmallFeatureCard(
                            modifier = Modifier.weight(1f),
                            title = "90 فريم",
                            icon = "📺",
                            onClick = { 
                                BypassCore.executeBypass(activity)
                                BypassCore.applyMemoryPatch()
                            }
                        )
                        SmallFeatureCard(
                            modifier = Modifier.weight(1f),
                            title = "خادم سعودي",
                            icon = "🇸🇦",
                            onClick = { /* Start Saudi VPN */ }
                        )
                    }
                }

                item {
                    SystemInfoSection(deviceModel, gameStatus, isGameRunning)
                }
            }
        }
    }
}

@Composable
fun HeaderSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.4f))
            .padding(24.dp)
    ) {
        Text(
            text = "SirLionV5",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp
        )
        Text(
            text = "Xeletron Core Protocol",
            color = Color(0xFF10B981).copy(alpha = 0.5f),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun FeatureCard(title: String, subtitle: String, icon: String, onClick: () -> Unit) {
    var isActive by remember { mutableStateOf(false) }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { 
                isActive = !isActive
                onClick() 
            },
        shape = RoundedCornerShape(24.dp),
        color = if (isActive) Color(0xFF10B981).copy(alpha = 0.1f) else Color(0xFF0F172A),
        border = androidx.compose.foundation.BorderStroke(
            1.dp, 
            if (isActive) Color(0xFF10B981) else Color(0xFF10B981).copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF10B981).copy(alpha = 0.2f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = icon, fontSize = 24.sp)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = subtitle, color = Color(0xFF10B981).copy(alpha = 0.4f), fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun SmallFeatureCard(modifier: Modifier = Modifier, title: String, icon: String, onClick: () -> Unit) {
    var isActive by remember { mutableStateOf(false) }
    Surface(
        modifier = modifier
            .clickable { 
                isActive = !isActive
                onClick() 
            },
        shape = RoundedCornerShape(16.dp),
        color = if (isActive) Color(0xFF10B981).copy(alpha = 0.1f) else Color(0xFF0F172A),
        border = androidx.compose.foundation.BorderStroke(
            1.dp, 
            if (isActive) Color(0xFF10B981) else Color.White.copy(alpha = 0.05f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = icon, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(
                text = if (isActive) "ON ✅" else "OFF",
                color = if (isActive) Color(0xFF10B981) else Color.White.copy(alpha = 0.2f),
                fontSize = 9.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
fun SystemInfoSection(model: String, status: String, isRunning: Boolean) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF10B981).copy(alpha = 0.05f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "معلومات النظام الحقيقية", color = Color(0xFF10B981), fontSize = 12.sp, fontWeight = FontWeight.Black)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "الجهاز: $model", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
            Text(
                text = "حالة اللعبة: $status", 
                color = if (isRunning) Color(0xFF10B981) else Color.White.copy(alpha = 0.4f), 
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
