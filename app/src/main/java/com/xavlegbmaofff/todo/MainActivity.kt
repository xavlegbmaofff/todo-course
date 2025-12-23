package com.xavlegbmaofff.todo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.xavlegbmaofff.todo.ui.theme.TodoAppTheme
import org.slf4j.LoggerFactory
import java.io.File
import java.time.Instant

class MainActivity : ComponentActivity() {
    private val logger = LoggerFactory.getLogger(MainActivity::class.java)
    private lateinit var storage: FileStorage
    private lateinit var todoFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logger.info("MainActivity onCreate started")

        todoFile = File(filesDir, "todos.json")
        storage = FileStorage()
        storage.load(todoFile)

        MemoryLeakDemo.BadContextHolder.init(this)

        enableEdgeToEdge()
        setContent {
            TodoAppTheme {
                TodoApp(
                    storage = storage,
                    onSave = { saveData() },
                    onLeakDemo = { LeakDemoActivity.start(this) }
                )
            }
        }

        logger.info("MainActivity onCreate completed")
    }

    private fun saveData() {
        logger.debug("Saving data to file")
        storage.save(todoFile)
    }

    override fun onStop() {
        super.onStop()
        logger.info("MainActivity onStop - saving data")
        saveData()
    }

    override fun onDestroy() {
        super.onDestroy()
        logger.info("MainActivity onDestroy")
    }
}

@Composable
fun TodoApp(storage: FileStorage, onSave: () -> Unit, onLeakDemo: () -> Unit) {
    var items by remember { mutableStateOf(storage.items) }
    var counter by remember { mutableStateOf(0) }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Todo List",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = {
                    counter++
                    val newItem = TodoItem(
                        text = "Задача #$counter",
                        importance = Importance.values()[counter % 3],
                        deadline = Instant.now().plusSeconds(3600)
                    )
                    storage.add(newItem)
                    items = storage.items
                    onSave()
                }) {
                    Text("Добавить")
                }

                Button(onClick = {
                    if (items.isNotEmpty()) {
                        storage.delete(items.last().uid)
                        items = storage.items
                        onSave()
                    }
                }) {
                    Text("Удалить")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Кнопка для демонстрации утечки памяти
            Button(
                onClick = onLeakDemo,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Создать утечку (LeakCanary)")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Всего задач: ${items.size}",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items) { item ->
                    TodoItemCard(item = item)
                }
            }
        }
    }
}

@Composable
fun TodoItemCard(item: TodoItem) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = item.text,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Важность: ${item.importance.value}",
                style = MaterialTheme.typography.bodySmall
            )
            item.deadline?.let {
                Text(
                    text = "Дедлайн: $it",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TodoItemCardPreview() {
    TodoAppTheme {
        TodoItemCard(
            item = TodoItem(
                text = "Тестовая задача",
                importance = Importance.HIGH
            )
        )
    }
}