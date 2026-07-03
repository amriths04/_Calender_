package com.example

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.util.Calendar

class MidnightWidgetReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == Intent.ACTION_BOOT_COMPLETED || 
            action == Intent.ACTION_TIME_CHANGED ||
            action == Intent.ACTION_TIMEZONE_CHANGED ||
            action == ACTION_MIDNIGHT_ALARM) {
            // Trigger widget update
            CalendarWidgetProvider.triggerAllWidgetsUpdate(context)
            
            // Schedule the next alarm
            scheduleNextMidnightAlarm(context)
        }
    }

    companion object {
        const val ACTION_MIDNIGHT_ALARM = "com.example.calendarplus.ACTION_MIDNIGHT_ALARM"

        fun scheduleNextMidnightAlarm(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            
            val intent = Intent(context, MidnightWidgetReceiver::class.java).apply {
                action = ACTION_MIDNIGHT_ALARM
            }
            
            val pendingIntent = PendingIntent.getBroadcast(
                context, 
                0, 
                intent, 
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                add(Calendar.DAY_OF_YEAR, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 1) // 1 second past midnight to be safe
                set(Calendar.MILLISECOND, 0)
            }

            try {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } catch (e: SecurityException) {
                // Fallback for Android 12+ if exact alarm permission is denied
                alarmManager.setWindow(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                    pendingIntent
                )
            }
        }
    }
}
