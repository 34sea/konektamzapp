package com.example.konekta_mz_app.ui.screens.applications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.konekta_mz_app.data.local.entity.ApplicationStatus
import com.example.konekta_mz_app.data.local.entity.JobApplication
import com.example.konekta_mz_app.data.local.entity.UserRole
import com.example.konekta_mz_app.viewmodel.ApplicationsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationsScreen(
    userId: Long,
    userRole: UserRole,
    viewModel: ApplicationsViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userId, userRole) {
        if (userRole == UserRole.CANDIDATE) {
            viewModel.loadMyApplications(userId)
        } else {
            viewModel.loadJobApplications(userId)
        }
    }

    LaunchedEffect(state.successMessage, state.error) {
        state.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    val applications = if (userRole == UserRole.CANDIDATE) state.myApplications else state.jobApplications

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (userRole == UserRole.CANDIDATE) "Minhas Candidaturas" else "Candidaturas Recebidas") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (state.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (applications.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (userRole == UserRole.CANDIDATE) "Ainda não se candidatou a nenhuma oferta" else "Nenhuma candidatura recebida",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(applications) { app ->
                    ApplicationCard(
                        application = app,
                        showActions = userRole == UserRole.EMPLOYER || userRole == UserRole.ADMIN,
                        onAccept = { viewModel.updateApplicationStatus(app.id, ApplicationStatus.ACCEPTED) },
                        onReject = { viewModel.updateApplicationStatus(app.id, ApplicationStatus.REJECTED) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ApplicationCard(
    application: JobApplication,
    showActions: Boolean,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = application.jobTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Candidato: ${application.candidateName}",
                style = MaterialTheme.typography.bodyMedium
            )

            if (application.coverLetter.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = application.coverLetter,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Estado: ${application.status}",
                    style = MaterialTheme.typography.labelMedium,
                    color = when (application.status) {
                        ApplicationStatus.ACCEPTED -> MaterialTheme.colorScheme.primary
                        ApplicationStatus.REJECTED -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )

                if (showActions && application.status == ApplicationStatus.PENDING) {
                    Row {
                        OutlinedButton(onClick = onReject) {
                            Text("Rejeitar")
                        }
                        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                        Button(
                            onClick = onAccept,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Aceitar")
                        }
                    }
                }
            }
        }
    }
}
