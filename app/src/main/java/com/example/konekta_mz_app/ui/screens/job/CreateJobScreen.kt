package com.example.konekta_mz_app.ui.screens.job

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.dp
import com.example.konekta_mz_app.data.local.entity.JobOffer
import com.example.konekta_mz_app.data.local.entity.User
import com.example.konekta_mz_app.viewmodel.JobViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateJobScreen(
    currentUser: User,
    viewModel: JobViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var salary by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var requirements by remember { mutableStateOf("") }
    var benefits by remember { mutableStateOf("") }
    var contactEmail by remember { mutableStateOf("") }
    var contactPhone by remember { mutableStateOf("") }

    LaunchedEffect(state.successMessage) {
        state.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
            onNavigateBack()
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Criar Oferta de Emprego") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título da Vaga *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Categoria *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descrição *") },
                maxLines = 5,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = salary,
                onValueChange = { salary = it },
                label = { Text("Salário") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Localização") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = requirements,
                onValueChange = { requirements = it },
                label = { Text("Requisitos") },
                maxLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = benefits,
                onValueChange = { benefits = it },
                label = { Text("Benefícios") },
                maxLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = contactEmail,
                onValueChange = { contactEmail = it },
                label = { Text("Email de Contacto") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = contactPhone,
                onValueChange = { contactPhone = it },
                label = { Text("Telefone de Contacto") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val offer = JobOffer(
                        employerId = currentUser.id,
                        employerName = currentUser.companyName.ifBlank { currentUser.name },
                        title = title,
                        description = description,
                        category = category,
                        salary = salary,
                        location = location,
                        requirements = requirements,
                        benefits = benefits,
                        contactEmail = contactEmail.ifBlank { currentUser.email },
                        contactPhone = contactPhone.ifBlank { currentUser.phone }
                    )
                    viewModel.createOffer(offer)
                },
                enabled = !state.isLoading && title.isNotBlank() && description.isNotBlank() && category.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Publicar Oferta")
                }
            }
        }
    }
}
