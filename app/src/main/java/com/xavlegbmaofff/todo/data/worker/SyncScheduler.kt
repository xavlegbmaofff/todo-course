package com.xavlegbmaofff.todo.data.worker

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkRequest
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

class SyncScheduler(private val context: Context) {

    private val logger = LoggerFactory.getLogger(SyncScheduler::class.java)
    private val workManager = WorkManager.getInstance(context)

    fun scheduleSyncNow() {
        logger.info("Scheduling immediate sync with retry logic")

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<TodoSyncWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        workManager.enqueueUniqueWork(
            TodoSyncWorker.WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            syncRequest
        )

        logger.debug("Sync work enqueued: {}", syncRequest.id)
    }

    fun schedulePeriodicSync() {
        logger.info("Scheduling periodic sync (every 15 minutes)")

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicSyncRequest = PeriodicWorkRequestBuilder<TodoSyncWorker>(
            15, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        workManager.enqueueUniquePeriodicWork(
            "periodic_${TodoSyncWorker.WORK_NAME}",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicSyncRequest
        )

        logger.debug("Periodic sync work enqueued: {}", periodicSyncRequest.id)
    }

    fun cancelAllSync() {
        logger.info("Cancelling all sync work")
        workManager.cancelUniqueWork(TodoSyncWorker.WORK_NAME)
        workManager.cancelUniqueWork("periodic_${TodoSyncWorker.WORK_NAME}")
    }

    fun getSyncStatus(): LiveData<List<WorkInfo>> {
        return workManager.getWorkInfosForUniqueWorkLiveData(TodoSyncWorker.WORK_NAME)
    }
}
