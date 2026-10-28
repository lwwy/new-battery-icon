package dev.anilbeesetti.nextplayer.core.ui.components

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
                val level = intent?.getIntExtra(
                    BatteryManager.EXTRA_LEVEL,
                    -1,
                ) ?: -1
                val scale = intent?.getIntExtra(
                    BatteryManager.EXTRA_SCALE,
                    -1,
                ) ?: -1
                if (level != -1 && scale != -1) {
                    batteryLevel = level * 100 / scale
                }
            }
        }
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val intent = context.registerReceiver(null, filter)
        val initLevel = intent?.getIntExtra(
            BatteryManager.EXTRA_LEVEL,
            -1,
        ) ?: -1
        val initScale = intent?.getIntExtra(
            BatteryManager.EXTRA_SCALE,
            -1,
        ) ?: -1
        if (initLevel != -1 && initScale != -1) {
            batteryLevel = initLevel * 100 / initScale
        }

        context.registerReceiver(receiver, filter)
        onDispose { context.unregisterReceiver(receiver) }
    }

    val color = if (batteryLevel > 20) Color(0xFF4CAF50) else Color.Red

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(4.dp),
                )
                .padding(2.dp)
                .background(
                    color = color,
                    shape = RoundedCornerShape(2.dp),
                )
                .padding(
                    horizontal = 6.dp,
                    vertical = 2.dp,
                ),
        ) {
            Text(
                text = "$batteryLevel",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(10.dp)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(
                        topEnd = 2.dp,
                        bottomEnd = 2.dp,
                    ),
                ),
        )
    }
}
