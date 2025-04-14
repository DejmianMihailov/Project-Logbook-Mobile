package com.example.mobilelogbook.ui.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun DateTimePickerDialog(
    label: String,
    selectedDateTime: LocalDateTime?,
    onDateTimeSelected: (LocalDateTime) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    var showDialog by remember { mutableStateOf(false) }

    val displayText = selectedDateTime?.format(formatter) ?: "Select $label"

    OutlinedButton(
        onClick = { showDialog = true },
        modifier = modifier
    ) {
        Text(text = "$label: $displayText")
    }

    if (showDialog) {
        showDateTimePicker(
            context = context,
            initialDateTime = selectedDateTime ?: LocalDateTime.now(),
            onDateTimeSelected = {
                onDateTimeSelected(it)
                showDialog = false
            },
            onDismiss = {
                showDialog = false
            }
        )
    }
}

private fun showDateTimePicker(
    context: Context,
    initialDateTime: LocalDateTime,
    onDateTimeSelected: (LocalDateTime) -> Unit,
    onDismiss: () -> Unit
) {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, initialDateTime.year)
        set(Calendar.MONTH, initialDateTime.monthValue - 1)
        set(Calendar.DAY_OF_MONTH, initialDateTime.dayOfMonth)
        set(Calendar.HOUR_OF_DAY, initialDateTime.hour)
        set(Calendar.MINUTE, initialDateTime.minute)
    }

    DatePickerDialog(
        context,
        { _, year, month, day ->
            TimePickerDialog(
                context,
                { _, hour, minute ->
                    val selected = LocalDateTime.of(year, month + 1, day, hour, minute)
                    onDateTimeSelected(selected)
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).apply {
                setOnCancelListener { onDismiss() }
            }.show()
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).apply {
        setOnCancelListener { onDismiss() }
    }.show()
}
