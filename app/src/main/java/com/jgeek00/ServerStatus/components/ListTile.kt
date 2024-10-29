package com.jgeek00.ServerStatus.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ListTile(label: String, supportingText: String) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(vertical = 8.dp)
    ) {
        Text(text = label)
        Text(
            text = supportingText,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp
        )
    }
}

@Composable
fun SwitchListTile(label: String, supportingText: String? = null, checked: Boolean, onCheckedChange: (Boolean) -> Unit, modifier: Modifier? = null) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(),
                onClick = { onCheckedChange(!checked) }
            )
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier ?: Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(vertical = 8.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                Text(text = label)
                if (supportingText != null) {
                    Text(
                        text = supportingText,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                }
            }
            Switch(
                checked,
                onCheckedChange,
            )
        }
    }
}