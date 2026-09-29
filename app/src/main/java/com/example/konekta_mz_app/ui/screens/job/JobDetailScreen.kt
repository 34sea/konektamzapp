package com.example.konekta_mz_app.ui.screens.job

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.konekta_mz_app.data.local.entity.JobOffer
import com.example.konekta_mz_app.data.local.entity.User
import com.example.konekta_mz_app.data.local.entity.UserRole
import com.example.konekta_mz_app.viewmodel.ApplicationsViewModel
import com.example.konekta_mz_app.viewmodel.JobViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDetailScreen(
    offerId: Long,
    currentUser: User,
    jobViewModel: JobViewModel,
    applicationsViewModel: ApplicationsViewModel,
    onNavigateBack: () -> Unit
) {
    val jobState by jobViewModel.state.collectAsState()
    val appState by applicationsViewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showApplyDialog by remember { mutableStateOf(false) }
    var coverLetter by remember { mutableStateOf("") }

    LaunchedEffect(offerId) {
        jobViewModel.loadOffer(offerId)
    }

    LaunchedEffect(appState.successMessage, appState.error) {
        appState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            applicationsViewModel.clearMessages()
            showApplyDialog = false
        }
        appState.error?.let {
            snackbarHostState.showSnackbar(it)
            applicationsViewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhes da Oferta") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (jobState.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val offer = jobState.selectedOffer
            if (offer == null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Oferta não encontrada")
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Text(
                        text = offer.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = offer.employerName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            InfoRow(Icons.Default.Work, "Categoria", offer.category)
                            if (offer.salary.isNotBlank()) {
                                InfoRow(Icons.Default.Work, "Salário", offer.salary)
                            }
                            InfoRow(Icons.Default.LocationOn, "Localização", offer.location.ifBlank { "Não especificada" })
                            if (offer.contactEmail.isNotBlank()) {
                                InfoRow(Icons.Default.Email, "Email", offer.contactEmail)
                            }
                            if (offer.contactPhone.isNotBlank()) {
                                InfoRow(Icons.Default.Phone, "Telefone", offer.contactPhone)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Descrição",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = offer.description,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    if (offer.requirements.isNotBlank()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Requisitos",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = offer.requirements,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    if (offer.benefits.isNotBlank()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Benefícios",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = offer.benefits,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Publicado em ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(offer.createdAt))}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Apply button for candidates
                    if (currentUser.role == UserRole.CANDIDATE) {
                        Button(
                            onClick = { showApplyDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Candidatar-se")
                        }
                    }

                    // Edit/Delete for employer who owns the offer or admin
                    if (currentUser.role == UserRole.ADMIN || (currentUser.role == UserRole.EMPLOYER && currentUser.id == offer.employerId)) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { jobViewModel.deleteOffer(offer); onNavigateBack() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Eliminar Oferta")
                        }
                    }
                }

                // Apply Dialog
                if (showApplyDialog) {
                    androidx.compose.material3.AlertDialog(
                        onDismissRequest = { showApplyDialog = false },
                        title = { Text("Candidatar-se") },
                        text = {
                            Column {
                                Text("Escreva uma carta de apresentação:")
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = coverLetter,
                                    onValueChange = { coverLetter = it },
                                    label = { Text("Carta de apresentação") },
                                    maxLines = 5,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    applicationsViewModel.applyForJob(currentUser, offer, coverLetter)
                                }
                            ) {
                                Text("Enviar")
                            }
                        },
                        dismissButton = {
                            androidx.compose.material3.TextButton(onClick = { showApplyDialog = false }) {
                                Text("Cancelar")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
