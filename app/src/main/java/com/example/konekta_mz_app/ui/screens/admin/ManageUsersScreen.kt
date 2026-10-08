package com.example.konekta_mz_app.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.konekta_mz_app.data.local.entity.User
import com.example.konekta_mz_app.data.local.entity.UserRole
import com.example.konekta_mz_app.data.repository.AuthRepository
import kotlinx.coroutines.launch

private val GreenPrimary = Color(0xFF00A843)
private val BackgroundLight = Color(0xFFFAFAFA)
private val SearchBgColor = Color(0xFFF2F4F7)
private val TextDark = Color(0xFF1D2939)
private val TextMuted = Color(0xFF667085)
private val CardBorderColor = Color(0xFFEAECF0)
private val RedError = Color(0xFFE53935)
private val BlueRole = Color(0xFF2563EB)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageUsersScreen(
    authRepository: AuthRepository,
    onNavigateBack: () -> Unit
) {
    val users by authRepository.getAllUsers().collectAsState(initial = emptyList())
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Estado para controlar o utilizador selecionado para remoção
    var userToDelete by remember { mutableStateOf<User?>(null) }

//    Scaffold(
//        containerColor = BackgroundLight,
//        topBar = {
//            TopAppBar(
//                title = {
//                    Text(
//                        text = "Gerir Utilizadores",
//                        fontWeight = FontWeight.Bold,
//                        fontSize = 18.sp,
//                        color = TextDark
//                    )
//                },
//                navigationIcon = {
//                    IconButton(onClick = onNavigateBack) {
//                        Icon(
//                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                            contentDescription = "Voltar",
//                            tint = TextDark
//                        )
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight)
//            )
//        },
//        snackbarHost = { SnackbarHost(snackbarHostState) }
//    ) { paddingValues ->
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
    ) {
        if (users.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.White)
//                    .padding(paddingValues)
                ,
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = GreenPrimary)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
//                    .padding(paddingValues)
                    .imePadding()
                    .navigationBarsPadding(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(users) { user ->
                    UserManagementCard(
                        user = user,
                        onDeleteUser = { userToDelete = user }
                    )
                }
            }
        }

        userToDelete?.let { user ->
            AlertDialog(
                onDismissRequest = { userToDelete = null },
                shape = RoundedCornerShape(20.dp),
                containerColor = Color.White,
                title = {
                    Text(
                        text = "Eliminar Utilizador",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextDark
                    )
                },
                text = {
                    Text(
                        text = "Tem certeza que deseja eliminar o utilizador \"${user.name}\"? Esta ação não pode ser desfeita.",
                        fontSize = 14.sp,
                        color = TextMuted
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val targetUser = user
                            userToDelete = null
                            scope.launch {
                                authRepository.deleteUser(targetUser)
                                snackbarHostState.showSnackbar("Utilizador eliminado com sucesso")
                            }
                        }
                    ) {
                        Text(
                            text = "Eliminar",
                            fontWeight = FontWeight.Bold,
                            color = RedError
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { userToDelete = null }) {
                        Text(
                            text = "Cancelar",
                            fontWeight = FontWeight.Medium,
                            color = TextMuted
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun UserManagementCard(
    user: User,
    onDeleteUser: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        when (user.role) {
                            UserRole.ADMIN -> RedError.copy(alpha = 0.1f)
                            UserRole.EMPLOYER -> BlueRole.copy(alpha = 0.1f)
                            UserRole.CANDIDATE -> GreenPrimary.copy(alpha = 0.1f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = when (user.role) {
                        UserRole.ADMIN -> RedError
                        UserRole.EMPLOYER -> BlueRole
                        UserRole.CANDIDATE -> GreenPrimary
                    },
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = user.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                when (user.role) {
                                    UserRole.ADMIN -> RedError.copy(alpha = 0.12f)
                                    UserRole.EMPLOYER -> BlueRole.copy(alpha = 0.12f)
                                    UserRole.CANDIDATE -> GreenPrimary.copy(alpha = 0.12f)
                                }
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = when (user.role) {
                                UserRole.CANDIDATE -> "Candidato"
                                UserRole.EMPLOYER -> "Empregador"
                                UserRole.ADMIN -> "Admin"
                            },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (user.role) {
                                UserRole.ADMIN -> RedError
                                UserRole.EMPLOYER -> BlueRole
                                UserRole.CANDIDATE -> GreenPrimary
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = user.email,
                    fontSize = 12.sp,
                    color = TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (user.phone.isNotBlank()) {
                    Text(
                        text = user.phone,
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }

            if (user.role != UserRole.ADMIN) {
                IconButton(onClick = onDeleteUser) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar Utilizador",
                        tint = RedError
                    )
                }
            }
        }
    }
}