package com.example.konekta_mz_app.ui.screens.profile

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
import com.example.konekta_mz_app.data.local.entity.User
import com.example.konekta_mz_app.data.local.entity.UserRole
import com.example.konekta_mz_app.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userId: Long,
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var skills by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("") }
    var companyName by remember { mutableStateOf("") }
    var companyDescription by remember { mutableStateOf("") }
    var isInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(state.user) {
        state.user?.let { user ->
            if (!isInitialized) {
                name = user.name
                phone = user.phone
                location = user.location
                skills = user.skills
                experience = user.experience
                companyName = user.companyName
                companyDescription = user.companyDescription
                isInitialized = true
            }
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meu Perfil") },
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
        } else {
            val user = state.user
            if (user == null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Utilizador não encontrado")
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = user.name,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = user.email,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = when (user.role) {
                                    UserRole.CANDIDATE -> "Candidato"
                                    UserRole.EMPLOYER -> "Empregador"
                                    UserRole.ADMIN -> "Administrador"
                                },
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nome") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Telefone") },
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

                    if (user.role == UserRole.CANDIDATE) {
                        OutlinedTextField(
                            value = skills,
                            onValueChange = { skills = it },
                            label = { Text("Competências") },
                            maxLines = 3,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = experience,
                            onValueChange = { experience = it },
                            label = { Text("Experiência Profissional") },
                            maxLines = 3,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (user.role == UserRole.EMPLOYER) {
                        OutlinedTextField(
                            value = companyName,
                            onValueChange = { companyName = it },
                            label = { Text("Nome da Empresa") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = companyDescription,
                            onValueChange = { companyDescription = it },
                            label = { Text("Descrição da Empresa") },
                            maxLines = 3,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            val updatedUser = user.copy(
                                name = name,
                                phone = phone,
                                location = location,
                                skills = skills,
                                experience = experience,
                                companyName = companyName,
                                companyDescription = companyDescription
                            )
                            viewModel.updateProfile(updatedUser)
                        },
                        enabled = !state.isLoading,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.height(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Guardar Alterações")
                        }
                    }
                }
            }
        }
    }
}
