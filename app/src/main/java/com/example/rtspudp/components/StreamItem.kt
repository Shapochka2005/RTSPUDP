package com.example.rtspudp.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rtspudp.database.entities.UserStream

@Composable
fun StreamItem(
    stream: UserStream,
    onDeleteClick: (() -> Unit)? = null,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stream.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = stream.url,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Протокол: ${stream.protocol}",
                    style = MaterialTheme.typography.labelSmall
                )
            }

            onDeleteClick?.let { onClick ->
                IconButton(onClick = onClick) {
                    Icon(
                        Icons.Default.Delete,
                        "Удалить",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}