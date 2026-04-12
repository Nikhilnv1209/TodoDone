package com.example.tododone.ui.screens.addtask

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.tododone.domain.model.enums.Priority
import com.example.tododone.ui.theme.*
import java.time.LocalTime
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddTaskViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onNavigateBack()
        }
    }

    Scaffold(
        containerColor = BackgroundPrimary,
        topBar = {
            TopAppBar(
                title = { Text("New Task") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundPrimary,
                    titleContentColor = TextPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title Field
            Column {
                Text(
                    text = "Title",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = uiState.title,
                    onValueChange = { viewModel.updateTitle(it) },
                    placeholder = { Text("What needs to be done?", color = TextTertiary) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = uiState.titleError != null,
                    supportingText = uiState.titleError?.let { { Text(it, color = ErrorRed) } },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryCream,
                        unfocusedBorderColor = BackgroundElevated,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = BackgroundSecondary,
                        unfocusedContainerColor = BackgroundSecondary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Description Field
            Column {
                Text(
                    text = "Description",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = uiState.description,
                    onValueChange = { viewModel.updateDescription(it) },
                    placeholder = { Text("Add details (optional)", color = TextTertiary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryCream,
                        unfocusedBorderColor = BackgroundElevated,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = BackgroundSecondary,
                        unfocusedContainerColor = BackgroundSecondary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Due Date Picker
            DatePickerField(
                selectedDate = uiState.dueDate,
                onDateSelected = { viewModel.updateDueDate(it) }
            )

            // Due Time Picker
            TimePickerField(
                selectedTime = uiState.dueTime,
                onTimeSelected = { viewModel.updateDueTime(it) }
            )

            // Priority Selector
            PrioritySelector(
                selectedPriority = uiState.priority,
                onPrioritySelected = { viewModel.updatePriority(it) }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Save Button
            Button(
                onClick = { viewModel.saveTask() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !uiState.isSaving,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryCream,
                    disabledContainerColor = BackgroundElevated
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = OnPrimaryDark
                    )
                } else {
                    Text(
                        "Create Task",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = OnPrimaryDark
                        )
                    )
                }
            }
        }

        if (uiState.error != null) {
            LaunchedEffect(uiState.error) {
                // Could show snackbar here
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerField(
    selectedDate: kotlinx.datetime.LocalDate?,
    onDateSelected: (kotlinx.datetime.LocalDate?) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    Column {
        Text(
            text = "Due Date",
            style = MaterialTheme.typography.labelLarge.copy(
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = selectedDate?.let {
                "${it.month.name.take(3)} ${it.dayOfMonth}, ${it.year}"
            } ?: "",
            onValueChange = {},
            placeholder = { Text("Select date (optional)", color = TextTertiary) },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = "Select date",
                        tint = PrimaryCream
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryCream,
                unfocusedBorderColor = BackgroundElevated,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedContainerColor = BackgroundSecondary,
                unfocusedContainerColor = BackgroundSecondary
            ),
            shape = RoundedCornerShape(12.dp)
        )
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val date = Instant.fromEpochMilliseconds(millis)
                                .toLocalDateTime(TimeZone.currentSystemDefault())
                            onDateSelected(date.date)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK", color = PrimaryCream)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = BackgroundSecondary,
                    titleContentColor = TextPrimary,
                    headlineContentColor = TextPrimary,
                    weekdayContentColor = TextSecondary,
                    subheadContentColor = TextPrimary,
                    yearContentColor = TextSecondary,
                    currentYearContentColor = PrimaryCream,
                    selectedYearContentColor = BackgroundPrimary,
                    selectedYearContainerColor = PrimaryCream,
                    dayContentColor = TextPrimary,
                    selectedDayContentColor = BackgroundPrimary,
                    selectedDayContainerColor = PrimaryCream,
                    todayContentColor = PrimaryCream,
                    todayDateBorderColor = PrimaryCream
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerField(
    selectedTime: LocalTime?,
    onTimeSelected: (LocalTime?) -> Unit
) {
    var showTimePicker by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(
        initialHour = selectedTime?.hour ?: 9,
        initialMinute = selectedTime?.minute ?: 0
    )

    Column {
        Text(
            text = "Due Time",
            style = MaterialTheme.typography.labelLarge.copy(
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = selectedTime?.let {
                String.format("%02d:%02d", it.hour, it.minute)
            } ?: "",
            onValueChange = {},
            placeholder = { Text("Select time (optional)", color = TextTertiary) },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { showTimePicker = true }) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = "Select time",
                        tint = PrimaryCream
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryCream,
                unfocusedBorderColor = BackgroundElevated,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedContainerColor = BackgroundSecondary,
                unfocusedContainerColor = BackgroundSecondary
            ),
            shape = RoundedCornerShape(12.dp)
        )
    }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            containerColor = BackgroundSecondary,
            confirmButton = {
                TextButton(
                    onClick = {
                        onTimeSelected(LocalTime.of(timePickerState.hour, timePickerState.minute))
                        showTimePicker = false
                    }
                ) {
                    Text("OK", color = PrimaryCream)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            text = {
                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = BackgroundElevated,
                        clockDialSelectedContentColor = BackgroundPrimary,
                        clockDialUnselectedContentColor = TextPrimary,
                        selectorColor = PrimaryCream,
                        containerColor = BackgroundSecondary,
                        periodSelectorBorderColor = BackgroundElevated,
                        periodSelectorSelectedContainerColor = PrimaryCream,
                        periodSelectorUnselectedContainerColor = BackgroundElevated,
                        periodSelectorSelectedContentColor = BackgroundPrimary,
                        periodSelectorUnselectedContentColor = TextPrimary,
                        timeSelectorSelectedContainerColor = PrimaryCream,
                        timeSelectorUnselectedContainerColor = BackgroundElevated,
                        timeSelectorSelectedContentColor = BackgroundPrimary,
                        timeSelectorUnselectedContentColor = TextPrimary
                    )
                )
            }
        )
    }
}

@Composable
private fun PrioritySelector(
    selectedPriority: Priority,
    onPrioritySelected: (Priority) -> Unit
) {
    Column {
        Text(
            text = "Priority",
            style = MaterialTheme.typography.labelLarge.copy(
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Priority.entries.forEach { priority ->
                val isSelected = selectedPriority == priority
                val backgroundColor = when (priority) {
                    Priority.HIGH -> if (isSelected) PriorityHigh else BackgroundSecondary
                    Priority.MEDIUM -> if (isSelected) PriorityMedium else BackgroundSecondary
                    Priority.LOW -> if (isSelected) PriorityLow else BackgroundSecondary
                    Priority.NONE -> if (isSelected) TextTertiary else BackgroundSecondary
                }
                val textColor = if (isSelected) BackgroundPrimary else TextPrimary

                FilterChip(
                    selected = isSelected,
                    onClick = { onPrioritySelected(priority) },
                    label = {
                        Text(
                            priority.name,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = backgroundColor,
                        selectedLabelColor = textColor,
                        containerColor = BackgroundSecondary,
                        labelColor = TextPrimary
                    ),
                    border = if (isSelected) null else FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = false,
                        borderColor = BackgroundElevated
                    )
                )
            }
        }
    }
}
