package com.xavlegbmaofff.todo.presentation.edit

import androidx.compose.ui.graphics.Color
import com.xavlegbmaofff.todo.data.model.Importance
import java.time.Instant
import android.graphics.Color as AndroidColor

data class TodoEditUiState(
    val text: String = "",
    val importance: Importance = Importance.NORMAL,
    val isDone: Boolean = false,
    val deadline: Instant? = null,
    val selectedColor: Color = Color(AndroidColor.WHITE),
    val customColor: Color? = null
)