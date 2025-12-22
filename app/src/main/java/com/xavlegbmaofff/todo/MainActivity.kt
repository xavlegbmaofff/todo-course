package com.xavlegbmaofff.todo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.xavlegbmaofff.todo.presentation.navigation.TodoNavigationGraph
import com.xavlegbmaofff.todo.ui.theme.TodoAppTheme
import dagger.hilt.android.AndroidEntryPoint
import org.slf4j.LoggerFactory

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val logger = LoggerFactory.getLogger(MainActivity::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logger.info("MainActivity onCreate started")

        enableEdgeToEdge()
        setContent {
            TodoAppTheme {
                TodoNavigationGraph()
            }
        }

        logger.info("MainActivity onCreate completed")
    }

    override fun onDestroy() {
        super.onDestroy()
        logger.info("MainActivity onDestroy")
    }
}
