package com.example.konekta_mz_app.ui.screens.job

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.konekta_mz_app.data.local.entity.JobOffer
import com.example.konekta_mz_app.data.local.entity.User
import com.example.konekta_mz_app.ui.components.CategorySelect
import com.example.konekta_mz_app.util.ImagePicker
import com.example.konekta_mz_app.util.LocationHelper
import com.example.konekta_mz_app.viewmodel.JobViewModel

private val GreenPrimary = Color(0xFF00A843)
private val BackgroundLight = Color(0xFFFAFAFA)
private val SearchBgColor = Color(0xFFF2F4F7)
private val TextDark = Color(0xFF1D2939)
private val TextMuted = Color(0xFF667085)
private val CardBorderColor = Color(0xFFEAECF0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateJobScreen(
    currentUser: User,
    viewModel: JobViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var salary by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var benefits by remember { mutableStateOf("") }
    var contactEmail by remember { mutableStateOf("") }
    var contactPhone by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imagePath by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf(0.0) }
    var longitude by remember { mutableStateOf(0.0) }
    var isGettingLocation by remember { mutableStateOf(false) }

    val requirements = remember { mutableStateListOf("") }
    val locationHelper = remember { LocationHelper(context) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = it
            val file = ImagePicker.createImageFile(context)
            if (ImagePicker.copyUriToFile(context, it, file)) {
                ImagePicker.compressImage(file)
                imagePath = file.absolutePath
            }
        }
    }

    val categories = state.categories

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

//    Scaffold(
//        containerColor = BackgroundLight,
//        topBar = {
//            TopAppBar(
//                title = {
//                    Text(
//                        "Criar",
//                        fontWeight = FontWeight.Bold,
//                        fontSize = 18.sp,
//                        color = TextDark
//                    )
//                },
//                navigationIcon = {
//                    IconButton(onClick = onNavigateBack) {
//                        Icon(
//                            Icons.AutoMirrored.Filled.ArrowBack,
//                            contentDescription = "Voltar",
//                            tint = TextDark
//                        )
//                    }
//                },
//                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = BackgroundLight
//                )
//            )
//        },
//        snackbarHost = { SnackbarHost(snackbarHostState) }
//    ) { paddingValues ->
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
//                .padding(paddingValues)
                .imePadding()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Box de Seleção de Imagem
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
                        text = "Imagem do Serviço / Capa",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = TextDark
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(SearchBgColor)
                            .border(
                                width = 1.dp,
                                color = GreenPrimary.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { imagePickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (imageUri != null) {
                            AsyncImage(
                                model = imageUri,
                                contentDescription = "Imagem Selecionada",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(GreenPrimary.copy(alpha = 0.1f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.CameraAlt,
                                        contentDescription = "Adicionar Imagem",
                                        tint = GreenPrimary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Clique para carregar uma imagem",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextMuted
                                )
                            }
                        }
                    }
                }
            }

            // Título
            CustomOutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = "Título da Vaga *",
                placeholder = "Ex: Desenvolvedor Mobile, Eletricista...",
                leadingIcon = Icons.Default.Work
            )

            // Selector de Categoria
            CategorySelect(
                categories = categories,
                selectedCategory = category,
                onCategorySelected = { category = it },
                label = "Categoria *",
                modifier = Modifier.fillMaxWidth()
            )

            // Descrição
            CustomOutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = "Descrição *",
                placeholder = "Descreva as responsabilidades e detalhes da vaga...",
                singleLine = false,
                maxLines = 4
            )

            // Salário
            CustomOutlinedTextField(
                value = salary,
                onValueChange = { salary = it },
                label = "Salário / Remuneração",
                placeholder = "Ex: 25.000 MZN / Negociável",
                leadingIcon = Icons.Default.MonetizationOn,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )

            // Localização
            CustomOutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = "Localização",
                placeholder = "Ex: Maputo, Beira ou Remoto",
                leadingIcon = Icons.Default.LocationOn
            )

            // Requisitos Dinâmicos
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderColor)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Requisitos da Vaga",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = TextDark
                        )
                        OutlinedButton(
                            onClick = { requirements.add("") },
                            shape = RoundedCornerShape(20.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GreenPrimary)
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = GreenPrimary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Adicionar",
                                color = GreenPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (requirements.isEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Nenhum requisito adicionado.",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }

                    requirements.forEachIndexed { index, requirement ->
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = requirement,
                                onValueChange = { requirements[index] = it },
                                placeholder = {
                                    Text(
                                        "Requisito ${index + 1}",
                                        color = TextMuted,
                                        fontSize = 13.sp
                                    )
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = SearchBgColor,
                                    unfocusedContainerColor = SearchBgColor,
                                    focusedBorderColor = GreenPrimary,
                                    unfocusedBorderColor = Color.Transparent
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { requirements.removeAt(index) }) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Remover Requisito",
                                    tint = Color(0xFFE53935),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Benefícios
            CustomOutlinedTextField(
                value = benefits,
                onValueChange = { benefits = it },
                label = "Benefícios oferecidos",
                placeholder = "Ex: Alojamento, Transporte, Bónus de desempenho...",
                singleLine = false,
                maxLines = 3
            )

            // Email de Contacto
            CustomOutlinedTextField(
                value = contactEmail,
                onValueChange = { contactEmail = it },
                label = "Email de Contacto",
                placeholder = currentUser.email,
                leadingIcon = Icons.Default.Email,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            // Telefone de Contacto
            CustomOutlinedTextField(
                value = contactPhone,
                onValueChange = { contactPhone = it },
                label = "Telefone de Contacto",
                placeholder = currentUser.phone.ifBlank { "+258 ..." },
                leadingIcon = Icons.Default.Phone,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Botão Publicar Oferta
            Button(
                onClick = {
                    val requirementsText = requirements.filter { it.isNotBlank() }.joinToString("\n")
                    val offer = JobOffer(
                        employerId = currentUser.id,
                        employerName = currentUser.companyName.ifBlank { currentUser.name },
                        title = title,
                        description = description,
                        category = category,
                        salary = salary,
                        location = location,
                        latitude = latitude,
                        longitude = longitude,
                        requirements = requirementsText,
                        benefits = benefits,
                        contactEmail = contactEmail.ifBlank { currentUser.email },
                        contactPhone = contactPhone.ifBlank { currentUser.phone },
                        imagePath = imagePath
                    )
                    viewModel.createOffer(offer)
                },
                enabled = !state.isLoading && title.isNotBlank() && description.isNotBlank() && category.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GreenPrimary,
                    disabledContainerColor = GreenPrimary.copy(alpha = 0.4f)
                )
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Publicar",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CustomOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Column {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextDark,
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = TextMuted, fontSize = 14.sp) },
            leadingIcon = leadingIcon?.let {
                { Icon(it, contentDescription = null, tint = TextMuted) }
            },
            singleLine = singleLine,
            maxLines = maxLines,
            keyboardOptions = keyboardOptions,
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
}