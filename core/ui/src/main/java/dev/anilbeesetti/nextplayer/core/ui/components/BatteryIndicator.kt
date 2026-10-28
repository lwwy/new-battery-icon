package dev.anilbeesetti.nextplayer.core.ui.components

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BatteryIndicator(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var batteryLevel by remember { mutableIntStateOf(100) }

    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
                val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
                if (level != -1 && scale != -1) {
                    batteryLevel = level * 100 / scale
                }
            }
        }
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        
        // 初始读取一次，防止刚打开显示100
        val intent = context.registerReceiver(null, filter)
        val initLevel = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val initScale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        if (initLevel != -1 && initScale != -1) {
            batteryLevel = initLevel * 100 / initScale
        }

        context.registerReceiver(receiver, filter)
        onDispose { context.unregisterReceiver(receiver) }
    }

    // 颜色逻辑：大于20%绿色，小于等于20%红色
    val color = if (batteryLevel > 20) Color(0xFF4CAF50) else Color.Red

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // 电池外壳（白色粗边）
        Box(
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(4.dp))
                .padding(2.dp) // 白边厚度
                .background(color, RoundedCornerShape(2.dp)) // 内部填充色
                .padding(horizontal = 6.dp, vertical = 2.dp), // <--- 这里加了逗号
        ) {
            Text(
                text = "$batteryLevel",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        // 电池正极凸起
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(10.dp)
                .background(Color.White, RoundedCornerShape(topEnd = 2.dp, bottomEnd = 2.dp)), // <--- 这里加了逗号
        )
    }
}
