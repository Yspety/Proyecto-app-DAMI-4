package com.cibertec.clinicacitas.Utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings

object NotificationUtils {

    fun scheduleAppointmentNotification(context: Context, appointmentId: Int, triggerTimeMillis: Long, doctorName: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Verificar si tenemos permiso para alarmas exactas en Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                context.startActivity(intent)

                return
            }
        }

        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("title", "Cita Próxima")
            putExtra("message", "Tu cita con el Dr. $doctorName comienza en 2 minutos.")
            putExtra("appointmentId", appointmentId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            appointmentId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Programar la alarma 2 minutos antes (triggerTimeMillis es el tiempo de la cita)
        val notificationTime = triggerTimeMillis - (2 * 60 * 1000)

        // Solo programar si el tiempo es en el futuro
        if (notificationTime > System.currentTimeMillis()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    notificationTime,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    notificationTime,
                    pendingIntent
                )
            }
        }
    }
}
