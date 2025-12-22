package com.xavlegbmaofff.todo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.xavlegbmaofff.todo.data.datasource.FileStorage
import com.xavlegbmaofff.todo.presentation.navigation.TodoNavigationGraph
import com.xavlegbmaofff.todo.ui.theme.TodoAppTheme
import dagger.hilt.android.AndroidEntryPoint
import org.slf4j.LoggerFactory
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val logger = LoggerFactory.getLogger(MainActivity::class.java)

    @Inject
    lateinit var storage: FileStorage

    @Inject
    lateinit var todoFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logger.info("MainActivity onCreate started")

        enableEdgeToEdge()
        setContent {
            TodoAppTheme {
                TodoNavigationGraph(
                    storage = storage,
                    onSave = { saveData() }
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
