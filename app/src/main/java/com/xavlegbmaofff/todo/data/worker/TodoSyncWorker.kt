package com.xavlegbmaofff.todo.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.xavlegbmaofff.todo.data.network.ServerErrorException
import com.xavlegbmaofff.todo.domain.repository.TodoRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import org.slf4j.LoggerFactory

@HiltWorker
class TodoSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: TodoRepository
) : CoroutineWorker(context, workerParams) {

    private val logger = LoggerFactory.getLogger(TodoSyncWorker::class.java)

    override suspend fun doWork(): Result {
        logger.info("TodoSyncWorker started, attempt #{}", runAttemptCount)

        return try {
            repository.sync()

            logger.info("TodoSyncWorker completed successfully")
            Result.success()
        } catch (e: ServerErrorException) {
            logger.warn("TodoSyncWorker: server error, will retry. Attempt #{}", runAttemptCount, e)

            if (runAttemptCount < MAX_RETRIES) {
                Result.retry()
            } else {
                logger.error("TodoSyncWorker: max retries reached, giving up")
                Result.failure()
            }
        } catch (e: Exception) {
            logger.error("TodoSyncWorker failed with non-retryable error", e)
            Result.failure()
        }
    }

    companion object {
        const val WORK_NAME = "todo_sync_work"
        const val MAX_RETRIES = 5
    }
}
