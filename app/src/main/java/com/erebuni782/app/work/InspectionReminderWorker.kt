package com.erebuni782.app.work

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.erebuni782.app.R
import java.util.concurrent.TimeUnit

/**
 * Напоминание о плановом осмотре артефакта (WorkManager, спека §1).
 * Планируется на nextInspectionEpochDay при сохранении записи.
 */
class InspectionReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val artifactId = inputData.getString(KEY_ARTIFACT_ID).orEmpty()
        val title = inputData.getString(KEY_TITLE)
            ?: applicationContext.getString(R.string.reminder_default_title)
        showNotification(artifactId, title)
        return Result.success()
    }

    private fun showNotification(artifactId: String, title: String) {
        val ctx = applicationContext
        val manager = NotificationManagerCompat.from(ctx)
        val channel = NotificationChannel(
            CHANNEL_ID,
            ctx.getString(R.string.reminder_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        manager.createNotificationChannel(channel)
        val notification = NotificationCompat.Builder(ctx, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(ctx.getString(R.string.reminder_title))
            .setContentText(title)
            .setAutoCancel(true)
            .build()
        if (android.os.Build.VERSION.SDK_INT >= 33 &&
            ctx.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            return // без разрешения молча пропускаем (запрос — на экране сотрудника)
        }
        manager.notify(artifactId.hashCode(), notification)
    }

    companion object {
        const val CHANNEL_ID = "inspections"
        const val KEY_ARTIFACT_ID = "artifact_id"
        const val KEY_TITLE = "title"
    }
}

object ReminderScheduler {

    /** Чистая функция задержки — тестируется на JVM. */
    fun computeDelayMillis(nowMillis: Long, epochDay: Long): Long {
        val target = epochDay * MILLIS_PER_DAY
        return (target - nowMillis).coerceAtLeast(0L)
    }

    private const val MILLIS_PER_DAY = 24L * 60 * 60 * 1000

    fun scheduleNextInspection(context: Context, artifactId: String, title: String, epochDay: Long?) {
        if (epochDay == null) {
            WorkManager.getInstance(context)
                .cancelUniqueWork(uniqueName(artifactId))
            return
        }
        val delay = computeDelayMillis(System.currentTimeMillis(), epochDay)
        val request = OneTimeWorkRequestBuilder<InspectionReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(
                workDataOf(
                    InspectionReminderWorker.KEY_ARTIFACT_ID to artifactId,
                    InspectionReminderWorker.KEY_TITLE to title
                )
            )
            .build()
        WorkManager.getInstance(context)
            .enqueueUniqueWork(uniqueName(artifactId), ExistingWorkPolicy.REPLACE, request)
    }

    fun cancel(context: Context, artifactId: String) {
        WorkManager.getInstance(context).cancelUniqueWork(uniqueName(artifactId))
    }

    private fun uniqueName(artifactId: String) = "inspection_$artifactId"
}
