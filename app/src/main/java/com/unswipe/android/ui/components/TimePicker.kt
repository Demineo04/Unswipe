package com.unswipe.android.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chargemap.compose.numberpicker.ListItemPicker

@Composable
fun CustomTimePicker(
    modifier: Modifier = Modifier,
    hour: Int,
    minute: Int,
    onHourChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        NumberPicker(
            value = hour,
            onValueChange = onHourChange,
            range = 0..23
        )

        Text(
            text = ":",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        NumberPicker(
            value = minute,
            onValueChange = onMinuteChange,
            range = 0..59
        )
    }
}

@Composable
private fun NumberPicker(
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange
) {
    ListItemPicker(
        label = { it.toString().padStart(2, '0') },
        value = value,
        onValueChange = onValueChange,
        list = range.toList(),
        dividersColor = MaterialTheme.colorScheme.primary,
        textStyle = MaterialTheme.typography.headlineMedium.copy(
            color = MaterialTheme.colorScheme.onSurface
        ),
    )
} 