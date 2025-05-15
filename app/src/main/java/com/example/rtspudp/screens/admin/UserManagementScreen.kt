package com.example.rtspudp.screens.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.rtspudp.components.StreamItem
import com.example.rtspudp.viewmodels.StreamViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManagementScreen(
    userId: Int,
    streamViewModel: StreamViewModel,
    navController: NavController,
    onBack: () -> Unit
) {
    var newStreamName by remember { mutableStateOf("") }
    var newStreamUrl by remember { mutableStateOf("") }
    val streams by streamViewModel.streams.collectAsState()
    val enabled = newStreamUrl.isNotBlank()

    LaunchedEffect(userId) {
        streamViewModel.loadStreamsForUser(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Управление стримами") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (enabled) {
                        val protocol = if (newStreamUrl.startsWith("rtsp://")) "RTSP" else "RTMP"
                        streamViewModel.addStream(
                            userId = userId,
                            name = newStreamName.ifBlank { "Новый стрим" },
                            url = newStreamUrl,
                            protocol = protocol
                        )
                        newStreamName = ""
                        newStreamUrl = ""
                    }
                },
                modifier = Modifier.padding(16.dp),
                containerColor = if (enabled) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                contentColor = if (enabled) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f)
            ) {
                Icon(Icons.Default.Add, "Добавить стрим")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = newStreamName,
                onValueChange = { newStreamName = it },
                label = { Text("Название стрима") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = newStreamUrl,
                onValueChange = { newStreamUrl = it },
                label = { Text("URL стрима (rtsp:// или rtmp://)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (streams.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Нет стримов для этого пользователя")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(streams) { stream ->
                        StreamItem(
                            stream = stream,
                            onDeleteClick = {
                                streamViewModel.deleteStream(stream)
                            },
                            onClick = {
                                navController.navigate("video/${stream.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}