package com.example.konekta_mz_app.ui.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.konekta_mz_app.data.local.entity.UserRole
import com.example.konekta_mz_app.util.ImagePicker
import com.example.konekta_mz_app.viewmodel.ProfileViewModel
import java.io.File

private val GreenPrimary = Color(0xFF00A843)
private val BackgroundLight = Color(0xFFFAFAFA)
private val SearchBgColor = Color(0xFFF2F4F7)
private val TextDark = Color(0xFF1D2939)
private val TextMuted = Color(0xFF667085)
private val CardBorderColor = Color(0xFFEAECF0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userId: Long,
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var skills by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("") }
    var companyName by remember { mutableStateOf("") }
    var companyDescription by remember { mutableStateOf("") }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    var profileImagePath by remember { mutableStateOf("") }
    var isInitialized by remember { mutableStateOf(false) }

    // Carregar os dados do utilizador
    LaunchedEffect(userId) {
        viewModel.loadUser(userId)
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            profileImageUri = it
            val file = ImagePicker.createImageFile(context)
            if (ImagePicker.copyUriToFile(context, it, file)) {
                ImagePicker.compressImage(file)
                profileImagePath = file.absolutePath
            }
        }
    }

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
                profileImagePath = user.profileImagePath
                if (user.profileImagePath.isNotBlank()) {
                    profileImageUri = Uri.fromFile(File(user.profileImagePath))
                }
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

//    Scaffold(
//        containerColor = BackgroundLight,
//        topBar = {
//            TopAppBar(
//                title = {
//                    Text(
//                        text = "Meu Perfil",
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
//                actions = {
//                    IconButton(onClick = onLogout) {
//                        Icon(
//                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
//                            contentDescription = "Sair",
//                            tint = Color(0xFFE53935)
//                        )
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundLight)
//            )
//        },
//        snackbarHost = { SnackbarHost(snackbarHostState) }
//    ) { paddingValues ->
    Box (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(color = Color.White)
    ){
        if (state.isLoading && !isInitialized) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
//                    .padding(paddingValues),
                        ,
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = GreenPrimary)
            }
        } else {
            val user = state.user
            if (user == null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
//                        .padding(paddingValues),

                            ,
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Utilizador não encontrado",
                        fontSize = 14.sp,
                        color = TextMuted
                    )
                }
            } else {
                Column(
                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(paddingValues)
                        .imePadding()
                        .navigationBarsPadding()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 0   .dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar com botão de alteração de imagem
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clickable { imagePickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(SearchBgColor)
                                .border(2.dp, CardBorderColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (profileImageUri != null) {
                                AsyncImage(
                                    model = profileImageUri,
                                    contentDescription = "Foto de Perfil",
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Sem foto",
                                    modifier = Modifier.size(54.dp),
                                    tint = TextMuted
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(GreenPrimary)
                                .border(2.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Trocar Foto",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderColor)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = user.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = user.email,
                                fontSize = 13.sp,
                                color = TextMuted
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(GreenPrimary.copy(alpha = 0.1f))
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = when (user.role) {
                                        UserRole.CANDIDATE -> "Candidato"
                                        UserRole.EMPLOYER -> "Empregador"
                                        UserRole.ADMIN -> "Administrador"
                                    },
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GreenPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Formula de Edição de Perfil
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Informações Pessoais",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        CustomOutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = "Nome completo",
                            icon = Icons.Default.Person
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        CustomOutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = "Contacto telefónico",
                            icon = Icons.Default.Phone
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        CustomOutlinedTextField(
                            value = location,
                            onValueChange = { location = it },
                            label = "Localização / Cidade",
                            icon = Icons.Default.LocationOn
                        )

                        if (user.role == UserRole.CANDIDATE) {
                            Spacer(modifier = Modifier.height(12.dp))

                            CustomOutlinedTextField(
                                value = skills,
                                onValueChange = { skills = it },
                                label = "Competências (ex: Pintura, Electricidade)",
                                icon = Icons.Default.Star,
                                maxLines = 3
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            CustomOutlinedTextField(
                                value = experience,
                                onValueChange = { experience = it },
                                label = "Experiência Profissional",
                                icon = Icons.Default.Work,
                                maxLines = 3
                            )
                        }

                        if (user.role == UserRole.EMPLOYER) {
                            Spacer(modifier = Modifier.height(12.dp))

                            CustomOutlinedTextField(
                                value = companyName,
                                onValueChange = { companyName = it },
                                label = "Nome da Empresa",
                                icon = Icons.Default.Business
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            CustomOutlinedTextField(
                                value = companyDescription,
                                onValueChange = { companyDescription = it },
                                label = "Descrição da Empresa",
                                icon = Icons.Default.Work,
                                maxLines = 3
                            )
                        }

                        Spacer(modifier = Modifier.height(28.dp))

                        Button(
                            onClick = {
                                val updatedUser = user.copy(
                                    name = name,
                                    phone = phone,
                                    location = location,
                                    skills = skills,
                                    experience = experience,
                                    companyName = companyName,
                                    companyDescription = companyDescription,
                                    profileImagePath = profileImagePath
                                )
                                viewModel.updateProfile(updatedUser)
                            },
                            enabled = !state.isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(26.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                        ) {
                            if (state.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    color = Color.White
                                )
                            } else {
                                Text(
                                    text = "Guardar Alterações",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    maxLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 13.sp, color = TextMuted) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = GreenPrimary) },
        maxLines = maxLines,
        singleLine = maxLines == 1,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = SearchBgColor,
            unfocusedContainerColor = SearchBgColor,
            focusedBorderColor = GreenPrimary,
            unfocusedBorderColor = Color.Transparent
        ),
        modifier = Modifier.fillMaxWidth()
    )
}