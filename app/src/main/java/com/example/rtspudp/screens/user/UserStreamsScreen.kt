package com.example.rtspudp.screens.user

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.rtspudp.components.StreamItem
import com.example.rtspudp.viewmodels.StreamViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserStreamsScreen(
    userId: Int,
    streamViewModel: StreamViewModel,
    onLogout: () -> Unit,
    navController: NavController
) {
    val streams by streamViewModel.streams.collectAsState()

    LaunchedEffect(userId) {
        streamViewModel.loadStreamsForUser(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Мои стримы") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.Logout, "Выйти")
                    }
                }
            )
        }
    ) { padding ->
        if (streams.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("У вас пока нет стримов")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                items(streams) { stream ->
                    StreamItem(
                        stream = stream,
                        onDeleteClick = null,
                        onClick = {
                            navController.navigate("video/${stream.id}")
                        }
                    )
                }
            }
        }
    }
}