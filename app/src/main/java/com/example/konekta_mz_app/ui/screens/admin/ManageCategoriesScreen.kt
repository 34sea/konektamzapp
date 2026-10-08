package com.example.konekta_mz_app.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.konekta_mz_app.data.local.entity.Category
import com.example.konekta_mz_app.data.repository.CategoryRepository
import kotlinx.coroutines.launch

private val GreenPrimary = Color(0xFF00A843)
private val BackgroundLight = Color(0xFFFAFAFA)
private val SearchBgColor = Color(0xFFF2F4F7)
private val TextDark = Color(0xFF1D2939)
private val TextMuted = Color(0xFF667085)
private val CardBorderColor = Color(0xFFEAECF0)
private val RedError = Color(0xFFE53935)

private val AvailableCategoryIcons = listOf(
    "💼", "💻", "🔧", "🎨", "🚗", "🏥", "📚", "🍳",
    "🏗️", "⚡", "🛍️", "🚚", "📊", "🌱", "🛡️", "📞"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageCategoriesScreen(
    categoryRepository: CategoryRepository,
    onNavigateBack: () -> Unit
) {
    val categories by categoryRepository.getAllCategories().collectAsState(initial = emptyList())
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var newCategoryName by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf(AvailableCategoryIcons.first()) }

    var categoryToDelete by remember { mutableStateOf<Category?>(null) }
    var categoryToEdit by remember { mutableStateOf<Category?>(null) }
    var editCategoryName by remember { mutableStateOf("") }
    var editSelectedIcon by remember { mutableStateOf(AvailableCategoryIcons.first()) }
    categoryToEdit?.let { category ->

        AlertDialog(
            onDismissRequest = {
                categoryToEdit = null
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White,

            title = {
                Text(
                    text = "Editar Categoria",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextDark
                )
            },

            text = {
                Column {

                    OutlinedTextField(
                        value = editCategoryName,
                        onValueChange = {
                            editCategoryName = it
                        },
                        label = {
                            Text("Nome da Categoria")
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenPrimary,
                            unfocusedBorderColor = CardBorderColor,
                            focusedLabelColor = GreenPrimary
                        )
                    )

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    Text(
                        text = "Selecione um Ícone",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextMuted
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(AvailableCategoryIcons) { icon ->

                            val isSelected =
                                icon == editSelectedIcon

                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected)
                                            GreenPrimary.copy(alpha = 0.15f)
                                        else
                                            SearchBgColor
                                    )
                                    .border(
                                        width = if (isSelected) 2.dp else 0.dp,
                                        color = if (isSelected)
                                            GreenPrimary
                                        else
                                            Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        editSelectedIcon = icon
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = icon,
                                    fontSize = 20.sp
                                )
                            }
                        }
                    }
                }
            },

            confirmButton = {
                TextButton(
                    enabled = editCategoryName.isNotBlank(),
                    onClick = {

                        val updatedCategory = category.copy(
                            name = editCategoryName.trim(),
                            icon = editSelectedIcon
                        )

                        scope.launch {
                            try {
                                categoryRepository.updateCategory(
                                    updatedCategory
                                )

                                categoryToEdit = null

                                snackbarHostState.showSnackbar(
                                    "Categoria atualizada com sucesso"
                                )

                            } catch (e: Exception) {

                                snackbarHostState.showSnackbar(
                                    "Não foi possível atualizar a categoria"
                                )
                            }
                        }
                    }
                ) {
                    Text(
                        text = "Guardar",
                        fontWeight = FontWeight.Bold,
                        color = GreenPrimary
                    )
                }
            },

            dismissButton = {
                TextButton(
                    onClick = {
                        categoryToEdit = null
                    }
                ) {
                    Text(
                        text = "Cancelar",
                        fontWeight = FontWeight.Medium,
                        color = TextMuted
                    )
                }
            }
        )
    }

//    Scaffold(
//        containerColor = BackgroundLight,
//        topBar = {
//            TopAppBar(
//                title = {
//                    Text(
//                        text = "Gerir Categorias",
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
    Box (
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
            .background(color = Color.White)
    ){
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
//                .padding(paddingValues)
                .imePadding()
                .navigationBarsPadding(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderColor)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Nova Categoria",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Nome da Categoria
                        OutlinedTextField(
                            value = newCategoryName,
                            onValueChange = { newCategoryName = it },
                            label = { Text("Nome da Categoria") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GreenPrimary,
                                unfocusedBorderColor = CardBorderColor,
                                focusedLabelColor = GreenPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Seleção Visual de Ícone
                        Text(
                            text = "Selecione um Ícone",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextMuted
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(AvailableCategoryIcons) { icon ->
                                val isSelected = icon == selectedIcon
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) GreenPrimary.copy(alpha = 0.15f) else SearchBgColor)
                                        .border(
                                            width = if (isSelected) 2.dp else 0.dp,
                                            color = if (isSelected) GreenPrimary else Color.Transparent,
                                            shape = CircleShape
                                        )
                                        .clickable { selectedIcon = icon },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = icon, fontSize = 20.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = {
                                if (newCategoryName.isNotBlank()) {
                                    scope.launch {
                                        categoryRepository.insertCategory(
                                            Category(
                                                name = newCategoryName.trim(),
                                                icon = selectedIcon
                                            )
                                        )
                                        newCategoryName = ""
                                        snackbarHostState.showSnackbar("Categoria adicionada com sucesso")
                                    }
                                }
                            },
                            enabled = newCategoryName.isNotBlank(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = GreenPrimary,
                                disabledContainerColor = SearchBgColor
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Adicionar Categoria",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Categorias Existentes (${categories.size})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            }

            if (categories.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Nenhuma categoria registada.",
                            fontSize = 14.sp,
                            color = TextMuted
                        )
                    }
                }
            } else {
                items(categories) { category ->
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(GreenPrimary.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = category.icon, fontSize = 18.sp)
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Text(
                                    text = category.name,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

//                            IconButton(onClick = { categoryToDelete = category }) {
//                                Icon(
//                                    imageVector = Icons.Default.Delete,
//                                    contentDescription = "Eliminar Categoria",
//                                    tint = RedError
//                                )
//                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                // Editar
                                IconButton(
                                    onClick = {
                                        categoryToEdit = category
                                        editCategoryName = category.name
                                        editSelectedIcon = category.icon
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Editar Categoria",
                                        tint = GreenPrimary
                                    )
                                }

                                // Eliminar
                                IconButton(
                                    onClick = {
                                        categoryToDelete = category
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Eliminar Categoria",
                                        tint = RedError
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        categoryToDelete?.let { category ->
            AlertDialog(
                onDismissRequest = { categoryToDelete = null },
                shape = RoundedCornerShape(20.dp),
                containerColor = Color.White,
                title = {
                    Text(
                        text = "Eliminar Categoria",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = TextDark
                    )
                },
                text = {
                    Text(
                        text = "Tem certeza que deseja eliminar a categoria \"${category.name}\"?",
                        fontSize = 14.sp,
                        color = TextMuted
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val targetCategory = category
                            categoryToDelete = null
//                            scope.launch {
////                                categoryRepository.deleteCategory(targetCategory)
//                                snackbarHostState.showSnackbar("Categoria eliminada com sucesso")
//                            }
                            scope.launch {
                                try {
                                    categoryRepository.deleteCategory(targetCategory)

                                    snackbarHostState.showSnackbar(
                                        "Categoria eliminada com sucesso"
                                    )
                                } catch (e: Exception) {
                                    snackbarHostState.showSnackbar(
                                        "Não foi possível eliminar a categoria"
                                    )
                                }
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
                    TextButton(onClick = { categoryToDelete = null }) {
                        Text(
                            text = "Cancelar",
                            fontWeight = FontWeight.Medium,
                            color = TextMuted
                        )
                    }
                }
            )
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
        )
    }
}