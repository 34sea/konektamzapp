package com.example.konekta_mz_app.ui.screens.auth

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.konekta_mz_app.data.local.entity.UserRole
import com.example.konekta_mz_app.util.ImagePicker
import com.example.konekta_mz_app.util.LocationHelper
import com.example.konekta_mz_app.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

private val GreenPrimary = Color(0xFF00A843)
private val LightTabBg = Color(0xFFF3F4F6)
private val FieldBackground = Color(0xFFF9FAFB)
private val BorderColor = Color(0xFFE5E7EB)
private val TextDark = Color(0xFF111827)
private val TextMuted = Color(0xFF6B7280)

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var phone by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.CANDIDATE) }
    var companyName by remember { mutableStateOf("") }
    var companyDescription by remember { mutableStateOf("") }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    var profileImagePath by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf(0.0) }
    var longitude by remember { mutableStateOf(0.0) }
    var isGettingLocation by remember { mutableStateOf(false) }

    val locationHelper = remember { LocationHelper(context) }

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

    LaunchedEffect(Unit) {
        if (locationHelper.hasLocationPermission()) {
            isGettingLocation = true
            val loc = locationHelper.getCurrentLocation()
            loc?.let { (lat, lng) ->
                latitude = lat
                longitude = lng
                location = locationHelper.getLocationName(lat, lng)
            }
            isGettingLocation = false
        }
    }

    LaunchedEffect(state.successMessage) {
        state.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSuccessMessage()
            onRegisterSuccess()
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    // Substituído Scaffold por Box para remover paddings/insets aninhados
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Logo
            Text(
                text = "Konekta",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Título
            Text(
                text = "Crie a sua conta",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Registe-se para aceder às melhores oportunidades",
                fontSize = 14.sp,
                color = TextMuted,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Tab Selector (Entrar / Criar Conta)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(LightTabBg)
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onNavigateBack() }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Entrar",
                        fontWeight = FontWeight.SemiBold,
                        color = TextMuted,
                        fontSize = 14.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Criar Conta",
                        fontWeight = FontWeight.Bold,
                        color = TextDark,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Foto de Perfil
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(FieldBackground)
                    .border(1.dp, BorderColor, CircleShape)
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (profileImageUri != null) {
                    AsyncImage(
                        model = profileImageUri,
                        contentDescription = "Foto de Perfil",
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Adicionar Foto",
                            modifier = Modifier.size(28.dp),
                            tint = GreenPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Foto",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = GreenPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Seletor de Perfil (Candidato vs Empregador)
            Text(
                text = "Tipo de Perfil",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (selectedRole == UserRole.CANDIDATE) GreenPrimary.copy(alpha = 0.1f) else FieldBackground)
                        .border(
                            width = 1.dp,
                            color = if (selectedRole == UserRole.CANDIDATE) GreenPrimary else BorderColor,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { selectedRole = UserRole.CANDIDATE }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Candidato",
                        fontWeight = if (selectedRole == UserRole.CANDIDATE) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedRole == UserRole.CANDIDATE) GreenPrimary else TextDark,
                        fontSize = 14.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (selectedRole == UserRole.EMPLOYER) GreenPrimary.copy(alpha = 0.1f) else FieldBackground)
                        .border(
                            width = 1.dp,
                            color = if (selectedRole == UserRole.EMPLOYER) GreenPrimary else BorderColor,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { selectedRole = UserRole.EMPLOYER }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Empregador",
                        fontWeight = if (selectedRole == UserRole.EMPLOYER) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedRole == UserRole.EMPLOYER) GreenPrimary else TextDark,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campos do Formulário
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text(if (selectedRole == UserRole.EMPLOYER) "Seu Nome" else "Nome Completo", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = TextDark) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = FieldBackground,
                    unfocusedContainerColor = FieldBackground,
                    focusedBorderColor = GreenPrimary,
                    unfocusedBorderColor = BorderColor,
                    focusedTextColor = TextDark,
                    unfocusedTextColor = TextDark
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("E-mail", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = TextDark) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = FieldBackground,
                    unfocusedContainerColor = FieldBackground,
                    focusedBorderColor = GreenPrimary,
                    unfocusedBorderColor = BorderColor,
                    focusedTextColor = TextDark,
                    unfocusedTextColor = TextDark
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                placeholder = { Text("Telefone (+258)", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = TextDark) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = FieldBackground,
                    unfocusedContainerColor = FieldBackground,
                    focusedBorderColor = GreenPrimary,
                    unfocusedBorderColor = BorderColor,
                    focusedTextColor = TextDark,
                    unfocusedTextColor = TextDark
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Senha", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = TextDark) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null,
                            tint = TextDark
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = FieldBackground,
                    unfocusedContainerColor = FieldBackground,
                    focusedBorderColor = GreenPrimary,
                    unfocusedBorderColor = BorderColor,
                    focusedTextColor = TextDark,
                    unfocusedTextColor = TextDark
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholder = { Text("Confirmar Senha", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = TextDark) },
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null,
                            tint = TextDark
                        )
                    }
                },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = FieldBackground,
                    unfocusedContainerColor = FieldBackground,
                    focusedBorderColor = GreenPrimary,
                    unfocusedBorderColor = BorderColor,
                    focusedTextColor = TextDark,
                    unfocusedTextColor = TextDark
                ),
                modifier = Modifier.fillMaxWidth()
            )

            if (selectedRole == UserRole.EMPLOYER) {
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = companyName,
                    onValueChange = { companyName = it },
                    placeholder = { Text("Nome da Empresa", color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Default.Work, contentDescription = null, tint = TextDark) },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = FieldBackground,
                        unfocusedContainerColor = FieldBackground,
                        focusedBorderColor = GreenPrimary,
                        unfocusedBorderColor = BorderColor,
                        focusedTextColor = TextDark,
                        unfocusedTextColor = TextDark
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = companyDescription,
                    onValueChange = { companyDescription = it },
                    placeholder = { Text("Descrição da Empresa", color = Color.Gray) },
                    maxLines = 3,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = FieldBackground,
                        unfocusedContainerColor = FieldBackground,
                        focusedBorderColor = GreenPrimary,
                        unfocusedBorderColor = BorderColor,
                        focusedTextColor = TextDark,
                        unfocusedTextColor = TextDark
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Campo de Localização
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                placeholder = { Text("Localização (ex: Maputo, Beira)", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = TextDark) },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                if (locationHelper.hasLocationPermission()) {
                                    isGettingLocation = true
                                    val loc = locationHelper.getCurrentLocation()
                                    loc?.let { (lat, lng) ->
                                        latitude = lat
                                        longitude = lng
                                        location = locationHelper.getLocationName(lat, lng)
                                    }
                                    isGettingLocation = false
                                }
                            }
                        }
                    ) {
                        if (isGettingLocation) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = GreenPrimary, strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.MyLocation, contentDescription = "Obter Localização", tint = GreenPrimary)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = FieldBackground,
                    unfocusedContainerColor = FieldBackground,
                    focusedBorderColor = GreenPrimary,
                    unfocusedBorderColor = BorderColor,
                    focusedTextColor = TextDark,
                    unfocusedTextColor = TextDark
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botão Principal
            Button(
                onClick = {
                    if (name.isBlank() || email.isBlank() || phone.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Por favor, preencha todos os campos obrigatórios.")
                        }
                        return@Button
                    }

                    if (password != confirmPassword) {
                        scope.launch {
                            snackbarHostState.showSnackbar("As senhas não coincidem.")
                        }
                        return@Button
                    }

                    if (password.length < 6) {
                        scope.launch {
                            snackbarHostState.showSnackbar("A senha deve ter pelo menos 6 caracteres.")
                        }
                        return@Button
                    }

                    if (selectedRole == UserRole.EMPLOYER && companyName.isBlank()) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Por favor, insira o nome da empresa.")
                        }
                        return@Button
                    }

                    viewModel.register(
                        name = name.trim(),
                        email = email.trim(),
                        password = password,
                        role = selectedRole,
                        phone = phone.trim(),
                        location = location.trim(),
                        latitude = latitude,
                        longitude = longitude,
                        companyName = companyName.trim(),
                        companyDescription = companyDescription.trim()
                    )
                },
                enabled = !state.isLoading,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GreenPrimary,
                    contentColor = Color.White,
                    disabledContainerColor = GreenPrimary.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Criar Conta",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Link para voltar
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                TextButton(onClick = onNavigateBack) {
                    Text(
                        text = "Já tem uma conta? Inicie Sessão",
                        color = GreenPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        // Host das mensagens do Snackbar na parte inferior da tela
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
    }
}