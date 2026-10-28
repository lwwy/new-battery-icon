package dev.anilbeesetti.nextplayer.core.ui.components

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
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
                if (intent != null) {
                    val level = intent.getIntExtra(
                        BatteryManager.EXTRA_LEVEL,
                        -1,
                    )
                    val scale = intent.getIntExtra(
                        BatteryManager.EXTRA_SCALE,
                        -1,
                    )
                    if (level != -1 && scale != -1) {
                        batteryLevel = level * 100 / scale
                    }
                }
            }
        }
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val intent = context.registerReceiver(
            null,
            filter,
        )
        if (intent != null) {
            val initLevel = intent.getIntExtra(
                BatteryManager.EXTRA_LEVEL,
                -1,
            )
            val initScale = intent.getIntExtra(
                BatteryManager.EXTRA_SCALE,
                -1,
            )
            if (initLevel != -1 && initScale != -1) {
                batteryLevel = initLevel * 100 / initScale
            }
        }

        context.registerReceiver(
            receiver,
            filter,
        )
        onDispose {
            context.unregisterReceiver(receiver)
        }
    }

    val color = if (batteryLevel > 20) {
        Color(0xFF4CAF50)
    } else {
        Color.Red
    }

    // 触摸穿透
    Row(
        modifier = modifier.pointerInput(Unit) {},
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // 电池主体（再次放大：140x56）
        Box(
            modifier = Modifier
                .width(140.dp)
                .height(56.dp)
                .background(
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(8.dp),
                )
                .border(
                    width = 5.dp,
                    color = Color.White,
                    shape = RoundedCornerShape(8.dp),
                )
                .padding(6.dp),
            contentAlignment = Alignment.Center,
        ) {
            // 内部填充条
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(batteryLevel / 100f)
                    .background(
                        color = color,
                        shape = RoundedCornerShape(4.dp),
                    )
                    .align(Alignment.CenterStart),
            )

            // 字号放大到 36sp，加最粗的字体和强烈阴影
            Text(
                text = "$batteryLevel",
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                style = TextStyle(
                    shadow = Shadow(
                        color = Color.Black,
                        offset = Offset(3f, 3f),
                        blurRadius = 5f,
                    ),
                ),
            )
        }

        // 电池正极凸起（拉宽到 10dp）
        Box(
            modifier = Modifier
                .width(10.dp)
                .height(22.dp)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(
                        topEnd = 4.dp,
                        bottomEnd = 4.dp,
                    ),
                ),
        )
    }
}
