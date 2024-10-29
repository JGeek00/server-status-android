package com.jgeek00.ServerStatus.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.Surface
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoPaddingAlertDialog(
    onDismissRequest: () -> Unit,
    title: String,
    content: @Composable () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable (() -> Unit)? = null,
    properties: DialogProperties = DialogProperties()
) {
    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        properties = properties
    ) {
       Surface(
           color = MaterialTheme.colorScheme.surfaceContainerHigh,
           elevation = 6.dp,
           modifier = Modifier
               .clip(RoundedCornerShape(28.dp))
       ) {
           Column(
               modifier = Modifier
                   .fillMaxWidth()
           ) {
               Text(
                   text = title,
                   fontSize = 24.sp,
                   modifier = Modifier
                       .padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 16.dp)
               )
               content.invoke()
               Box(
                   Modifier
                       .fillMaxWidth()
                       .padding(all = 24.dp)
               ) {
                   Row(
                       horizontalArrangement = Arrangement.End,
                       modifier = Modifier.fillMaxWidth()
                   ) {
                       dismissButton?.invoke()
                       confirmButton()
                   }
               }
           }
       }
    }
}