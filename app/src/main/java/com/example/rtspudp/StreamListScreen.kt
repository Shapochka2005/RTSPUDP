package com.example.rtspudp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun StreamListScreen(
    streams: List<StreamDataModel>,
    onStreamClick: (Int) -> Unit
) {
    var newUrl by remember { mutableStateOf("") }
    var newName by remember { mutableStateOf("") }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            // Поле для ввода имени стрима
            TextField(
                value = newName,
                onValueChange = { newName = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                placeholder = { Text("Название стрима") },
                singleLine = true,
                label = { Text("Название") }
            )

            // Поле для ввода URL
            TextField(
                value = newUrl,
                onValueChange = { newUrl = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                placeholder = { Text("rtsp:// или udp:// URL") },
                singleLine = true,
                label = { Text("URL стрима") }
            )

            // Кнопка добавления
            Button(
                onClick = {
                    if (newUrl.isNotBlank()) {
                        val protocol = determineProtocol(newUrl)
                        StreamData.addStream(
                            StreamDataModel(
                                name = if (newName.isBlank()) "Custom Stream ($protocol)" else newName,
                                url = newUrl,
                                protocol = protocol
                            )
                        )
                        newUrl = ""
                        newName = ""
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                enabled = newUrl.isNotBlank()
            ) {
                Text("Добавить стрим")
            }

            // Список стримов
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(streams) { index, stream ->
                    StreamItem(
                        stream = stream,
                        onItemClick = { onStreamClick(index) },
                        onDeleteClick = { StreamData.deleteStream(index) }
                    )
                }
            }
        }
    }
}

@Composable
private fun StreamItem(
    stream: StreamDataModel,
    onItemClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onItemClick)
            ) {
                Text(
                    text = stream.name,
                    style = MaterialTheme.typography.titleSmall
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

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onDeleteClick
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Удалить"
                )
            }
        }
    }
}

private fun determineProtocol(url: String): String {
    return when {
        url.startsWith("rtsp://", ignoreCase = true) -> "RTSP"
        url.startsWith("udp://", ignoreCase = true) -> "UDP"
        url.startsWith("http://", ignoreCase = true) -> "HTTP"
        url.startsWith("https://", ignoreCase = true) -> "HTTPS"
        else -> "Unknown"
    }
}