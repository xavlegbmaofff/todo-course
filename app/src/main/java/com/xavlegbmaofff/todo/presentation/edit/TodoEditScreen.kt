package com.xavlegbmaofff.todo.presentation.edit

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.xavlegbmaofff.todo.data.model.Importance
import com.xavlegbmaofff.todo.data.model.TodoItem
import com.xavlegbmaofff.todo.presentation.edit.components.ColorPicker
import com.xavlegbmaofff.todo.presentation.edit.components.ColorSelector
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoEditScreen(
    todoItemUid: String?,
    onNavigateBack: () -> Unit,
    viewModel: TodoEditViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(todoItemUid) {
        viewModel.initializeFromTodoItem(todoItemUid)
    }

    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var showColorPicker by rememberSaveable { mutableStateOf(false) }

    val contentAlpha by animateColorAsState(
        targetValue = if (uiState.isDone) Color.Gray.copy(alpha = 0.5f) else Color.Transparent,
        label = "done_alpha"
    )

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TodoEditTopBar(
                onBack = onNavigateBack,
                onSave = {
                    viewModel.saveTodoItem(todoItemUid)
                    onNavigateBack()
                }
            )
        },
        modifier = Modifier.imePadding()
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(scrollState)
            ) {
                TodoTextInput(
                    text = uiState.text,
                    onTextChange = viewModel::updateText,
                    isDone = uiState.isDone
                )

                Spacer(modifier = Modifier.height(24.dp))

                ImportanceSelector(
                    importance = uiState.importance,
                    onImportanceChange = viewModel::updateImportance,
                    isDone = uiState.isDone
                )

                Spacer(modifier = Modifier.height(24.dp))

                DeadlineSelector(
                    deadline = uiState.deadline,
                    onDeadlineClick = { showDatePicker = true },
                    onClearDeadline = { viewModel.updateDeadline(null) },
                    isDone = uiState.isDone
                )

                Spacer(modifier = Modifier.height(24.dp))

                DoneCheckbox(
                    isDone = uiState.isDone,
                    onDoneChange = viewModel::updateIsDone
                )

                Spacer(modifier = Modifier.height(24.dp))

                Box(modifier = Modifier.alpha(if (uiState.isDone) 0.5f else 1f)) {
                    ColorSelector(
                        selectedColor = uiState.selectedColor,
                        customColor = uiState.customColor,
                        onColorSelected = viewModel::updateSelectedColor,
                        onCustomColorLongClick = { showColorPicker = true }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }

            if (uiState.isDone) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(contentAlpha)
                )
            }
        }
    }

    if (showDatePicker) {
        TodoDatePickerDialog(
            initialDate = uiState.deadline,
            onDateSelected = { selectedDate ->
                viewModel.updateDeadline(selectedDate)
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }

    if (showColorPicker) {
        ColorPickerBottomSheet(
            initialColor = uiState.customColor ?: uiState.selectedColor,
            onColorSelected = { color ->
                viewModel.updateCustomColor(color)
                viewModel.updateSelectedColor(color)
            },
            onDismiss = { showColorPicker = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TodoEditTopBar(
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    TopAppBar(
        title = { Text("Редактирование") },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Назад"
                )
            }
        },
        actions = {
            IconButton(onClick = onSave) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Сохранить"
                )
            }
        }
    )
}

@Composable
private fun TodoTextInput(
    text: String,
    onTextChange: (String) -> Unit,
    isDone: Boolean
) {
    OutlinedTextField(
        value = text,
        onValueChange = onTextChange,
        label = { Text("Текст задачи") },
        placeholder = { Text("Введите описание задачи...") },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 100.dp)
            .alpha(if (isDone) 0.5f else 1f),
        minLines = 3,
        maxLines = 10,
        shape = RoundedCornerShape(12.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ImportanceSelector(
    importance: Importance,
    onImportanceChange: (Importance) -> Unit,
    isDone: Boolean
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.alpha(if (isDone) 0.5f else 1f)) {
        Text(
            text = "Важность",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = importance.value,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                shape = RoundedCornerShape(12.dp)
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                Importance.entries.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item.value) },
                        onClick = {
                            onImportanceChange(item)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun DeadlineSelector(
    deadline: Instant?,
    onDeadlineClick: () -> Unit,
    onClearDeadline: () -> Unit,
    isDone: Boolean
) {
    Column(modifier = Modifier.alpha(if (isDone) 0.5f else 1f)) {
        Text(
            text = "Дедлайн",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            onClick = onDeadlineClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null
                    )
                    Text(
                        text = deadline?.let { formatDate(it) } ?: "Не выбрано",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                if (deadline != null) {
                    TextButton(onClick = onClearDeadline) {
                        Text("Очистить")
                    }
                }
            }
        }
    }
}

@Composable
private fun DoneCheckbox(
    isDone: Boolean,
    onDoneChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDone)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isDone,
                onCheckedChange = onDoneChange
            )
            Text(
                text = if (isDone) "Выполнено" else "Отметить выполненным",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TodoDatePickerDialog(
    initialDate: Instant?,
    onDateSelected: (Instant?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate?.toEpochMilli()
            ?: System.currentTimeMillis()
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        onDateSelected(Instant.ofEpochMilli(millis))
                    }
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColorPickerBottomSheet(
    initialColor: Color,
    onColorSelected: (Color) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        androidx.compose.animation.AnimatedVisibility(
            visible = sheetState.isVisible,
            enter = androidx.compose.animation.slideInVertically(
                initialOffsetY = { it },
                animationSpec = androidx.compose.animation.core.spring(
                    dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
                    stiffness = androidx.compose.animation.core.Spring.StiffnessLow
                )
            ) + androidx.compose.animation.fadeIn(
                animationSpec = androidx.compose.animation.core.tween(300)
            ),
            exit = androidx.compose.animation.slideOutVertically(
                targetOffsetY = { it },
                animationSpec = androidx.compose.animation.core.tween(200)
            ) + androidx.compose.animation.fadeOut(
                animationSpec = androidx.compose.animation.core.tween(200)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = "Выбор цвета",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                ColorPicker(
                    initialColor = initialColor,
                    onColorSelected = onColorSelected
                )

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text("Готово")
                }
            }
        }
    }
}

private fun formatDate(instant: Instant): String {
    val localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate()
    val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
    return localDate.format(formatter)
}
