package com.xavlegbmaofff.todo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.xavlegbmaofff.todo.data.datasource.FileStorage
import com.xavlegbmaofff.todo.presentation.edit.TodoEditScreen
import com.xavlegbmaofff.todo.ui.theme.TodoAppTheme
import org.slf4j.LoggerFactory
import java.io.File

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

        enableEdgeToEdge()
        setContent {
            TodoAppTheme {
                TodoEditScreen(
                    todoItem = null,
                    onSave = {},
                    onBack = {}
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
