package com.example.mobilelogbook.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LabeledDropdown(
    label: String,
    options: List<Pair<Int, String>>, // ID + display text
    selectedId: Int?,
    onSelectedChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedText = options.firstOrNull { it.first == selectedId }?.second ?: "Select $label"

    Column(modifier = modifier.padding(vertical = 8.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelLarge)
        Box {
            OutlinedTextField(
                value = selectedText,
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true },
                readOnly = true,
                enabled = false
            )
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { (id, labelText) ->
                    DropdownMenuItem(
                        text = { Text(labelText) },
                        onClick = {
                            onSelectedChange(id)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
