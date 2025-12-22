package com.xavlegbmaofff.todo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.xavlegbmaofff.todo.ui.theme.TodoAppTheme
import java.io.File
import java.time.Instant

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TodoAppTheme {
                val filesDir = LocalContext.current.filesDir
                val todoFile = File(filesDir, "todos.json")

                val storage = FileStorage()
                storage.load(todoFile)

                val newItem = TodoItem(
                    text = "Купить молоко",
                    importance = Importance.HIGH,
                    deadline = Instant.now().plusSeconds(3600)
                )
                storage.add(newItem)
                storage.save(todoFile)

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = storage.items.first().text,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TodoAppTheme {
        Greeting("Android")
    }
}