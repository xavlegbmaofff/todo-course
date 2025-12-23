package com.xavlegbmaofff.todo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xavlegbmaofff.todo.ui.theme.TodoAppTheme
import org.slf4j.LoggerFactory

/**
 * Activity для демонстрации утечки памяти с LeakCanary.
 *
 * ИНСТРУКЦИЯ:
 * 1. Нажмите кнопку "Создать утечку" в MainActivity
 * 2. Откроется эта Activity
 * 3. Нажмите кнопку "Назад" или "Закрыть"
 * 4. Подождите 5-10 секунд
 * 5. LeakCanary покажет уведомление об утечке!
 */
class LeakDemoActivity : ComponentActivity() {
    private val logger = LoggerFactory.getLogger(LeakDemoActivity::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logger.info("LeakDemoActivity onCreate")

        // УТЕЧКА! Сохраняем ссылку на эту Activity в статическом объекте
        LeakHolder.leakedActivity = this
        logger.error("УТЕЧКА СОЗДАНА! Activity сохранена в статическом поле")

        // Дополнительная утечка через Handler с долгим delay
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            // Этот callback держит ссылку на Activity
            logger.debug("Handler callback executed: ${this.localClassName}")
        }, 120_000) // 2 минуты - Activity точно закроется раньше

        setContent {
            TodoAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Leak Demo Activity",
                            style = MaterialTheme.typography.headlineMedium
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Эта Activity создала утечку памяти!",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Нажмите 'Назад' и подождите 5-10 секунд.\nLeakCanary покажет уведомление.",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(onClick = { finish() }) {
                            Text("Закрыть и создать утечку")
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        logger.warn("LeakDemoActivity onDestroy - но ссылка в LeakHolder остаётся!")
        logger.warn("LeakCanary обнаружит утечку через несколько секунд...")
        // НЕ очищаем LeakHolder.leakedActivity - это создаёт утечку!
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, LeakDemoActivity::class.java))
        }
    }
}

/**
 * Статический объект, который держит ссылку на Activity - классическая утечка памяти.
 */
object LeakHolder {
    var leakedActivity: LeakDemoActivity? = null

    fun clear() {
        leakedActivity = null
    }
}
