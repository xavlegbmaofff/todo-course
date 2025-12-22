package com.xavlegbmaofff.todo

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import org.slf4j.LoggerFactory
import java.lang.ref.WeakReference

object MemoryLeakDemo {
    private val logger = LoggerFactory.getLogger(MemoryLeakDemo::class.java)

    private val leakedActivities = mutableListOf<Activity>()

    // ============================================================================
    // ПРОБЛЕМА 1: Утечка памяти через статическую ссылку на Context
    // LeakCanary обнаружит: "MainActivity has leaked"
    // ============================================================================
    object BadContextHolder {
        private var context: Context? = null

        fun init(context: Context) {
            this.context = context
            logger.warn("BadContextHolder: Сохранена прямая ссылка на Context - УТЕЧКА!")
            logger.warn("LeakCanary обнаружит эту утечку при повороте экрана или закрытии Activity")
        }

        fun getAppName(): String? {
            return context?.applicationInfo?.name
        }

        fun clear() {
            context = null
            logger.info("BadContextHolder: Ссылка очищена")
        }
    }

    object GoodContextHolder {
        private var contextRef: WeakReference<Context>? = null

        fun init(context: Context) {
            this.contextRef = WeakReference(context.applicationContext)
            logger.info("GoodContextHolder: Используем WeakReference на applicationContext")
        }

        fun getAppName(): String? {
            return contextRef?.get()?.applicationInfo?.name
        }
    }

    fun simulateActivityLeak(activity: Activity) {
        leakedActivities.add(activity)
        logger.error("simulateActivityLeak: Добавлена Activity в статический список!")
        logger.error("Всего утёкших Activity: ${leakedActivities.size}")
        logger.error("LeakCanary обнаружит ${leakedActivities.size} утечек")
    }

    fun clearActivityLeaks() {
        val count = leakedActivities.size
        leakedActivities.clear()
        logger.info("clearActivityLeaks: Очищено $count утечек")
    }

    // ============================================================================
    // ПРОБЛЕМА 2: Утечка памяти через Handler
    // ============================================================================

    class BadHandlerExample(private val context: Context) {
        private val handler = Handler(Looper.getMainLooper())

        fun startLongTask() {
            logger.warn("BadHandlerExample: Запуск задачи с потенциальной утечкой")
            handler.postDelayed({
                logger.debug("Task completed with context: ${context.packageName}")
            }, 60_000)
        }
    }

    class GoodHandlerExample(context: Context) {
        private val contextRef = WeakReference(context)
        private val handler = Handler(Looper.getMainLooper())
        private val runnable = Runnable {
            contextRef.get()?.let { ctx ->
                logger.debug("Task completed with context: ${ctx.packageName}")
            } ?: logger.warn("Context уже собран GC")
        }

        fun startLongTask() {
            logger.info("GoodHandlerExample: Запуск задачи с WeakReference")
            handler.postDelayed(runnable, 60_000)
        }

        fun cleanup() {
            logger.info("GoodHandlerExample: Очистка callbacks")
            handler.removeCallbacks(runnable)
        }
    }
}