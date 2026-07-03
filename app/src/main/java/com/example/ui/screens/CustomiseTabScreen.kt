package com.example.ui.screens

import android.widget.Toast
import android.os.Build
import android.content.Intent
import android.app.AlarmManager
import android.provider.Settings
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.CalendarViewModel
import com.example.data.WidgetTheme
import com.example.ui.widget.WidgetComponent

@Composable
fun CustomiseTabScreen(viewModel: CalendarViewModel) {
    val context = LocalContext.current
    val widgetTheme by viewModel.widgetTheme.collectAsState()
    val widgetTransparency by viewModel.widgetTransparency.collectAsState()
    val firstDayOfWeek by viewModel.firstDayOfWeek.collectAsState()
    val isDark by viewModel.isDarkMode.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Widget Real-time dynamic preview card
        item {
            Text(
                text = "Preview",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Reusing the same calendar component for preview!
            Box(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
                WidgetComponent(viewModel)
            }
        }

        // Exact Alarm Permission for Midnight Updates
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            item {
                val alarmManager = context.getSystemService(android.content.Context.ALARM_SERVICE) as AlarmManager
                if (!alarmManager.canScheduleExactAlarms()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Perfect Midnight Updates",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 15.sp),
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "To ensure your widget updates exactly at midnight, please allow 'Alarms & Reminders' permission.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.onErrorContainer,
                                    contentColor = MaterialTheme.colorScheme.errorContainer
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Grant Permission", style = MaterialTheme.typography.labelLarge)
                            }
                        }
                    }
                }
            }
        }

        // Removed Theme Customization presets card as requested


        // Premium Pin layout & Manual installation instructions
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Add Widget to Home Screen",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            ),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "To manually add the widget, long-press an empty space on your home screen, select Widgets, and choose the Calendar app. Alternatively, tap the button below to pin it instantly.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    if (viewModel.isPinWidgetSupported()) {
                        Button(
                            onClick = {
                                try {
                                    viewModel.pinWidget()
                                    Toast.makeText(context, "Request sent! Check your home screen.", Toast.LENGTH_SHORT).show()
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Widget pinning not supported on this device.", Toast.LENGTH_LONG).show()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_pin_widget"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                "Pin Widget Instantly", 
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Row(
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(16.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                "How to Add Manually:",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                "1. Go to your phone's home screen.\n" +
                                "2. Long press on an empty space.\n" +
                                "3. Tap 'Widgets' and scroll to 'Calendar'.\n" +
                                "4. Drag 'Calendar Widget' and place it. It is very resizable! Stretch it to full width for optimum details.",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 16.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}
