package com.example.rtspudp.screens.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rtspudp.viewmodels.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    userViewModel: UserViewModel,
    onUserSelected: (Int) -> Unit,
    onLogout: () -> Unit
) {
    val users by userViewModel.allUsers.collectAsState()

    LaunchedEffect(Unit) {
        userViewModel.loadAllUsers()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Панель администратора") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.Logout, "Выйти")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            items(users) { user ->
                Card(
                    onClick = { onUserSelected(user.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = user.username,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = if (user.isAdmin) "Администратор" else "Пользователь",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}