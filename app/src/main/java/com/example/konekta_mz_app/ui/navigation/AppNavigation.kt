package com.example.konekta_mz_app.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.konekta_mz_app.KonektaApp
import com.example.konekta_mz_app.data.local.entity.UserRole
import com.example.konekta_mz_app.ui.components.ProfileBar
import com.example.konekta_mz_app.ui.screens.admin.AdminScreen
import com.example.konekta_mz_app.ui.screens.admin.ManageCategoriesScreen
import com.example.konekta_mz_app.ui.screens.admin.ManageUsersScreen
import com.example.konekta_mz_app.ui.screens.applications.ApplicationsScreen
import com.example.konekta_mz_app.ui.screens.auth.LoginScreen
import com.example.konekta_mz_app.ui.screens.auth.RegisterScreen
import com.example.konekta_mz_app.ui.screens.home.HomeScreen
import com.example.konekta_mz_app.ui.screens.job.CreateJobScreen
import com.example.konekta_mz_app.ui.screens.job.EditJobScreen
import com.example.konekta_mz_app.ui.screens.job.JobDetailScreen
import com.example.konekta_mz_app.ui.screens.map.MapScreen
import com.example.konekta_mz_app.ui.screens.profile.ProfileScreen
import com.example.konekta_mz_app.viewmodel.AdminViewModel
import com.example.konekta_mz_app.viewmodel.ApplicationsViewModel
import com.example.konekta_mz_app.viewmodel.AuthViewModel
import com.example.konekta_mz_app.viewmodel.HomeViewModel
import com.example.konekta_mz_app.viewmodel.JobViewModel
import com.example.konekta_mz_app.viewmodel.MapViewModel
import com.example.konekta_mz_app.viewmodel.ProfileViewModel

// Cores personalizadas do menu inferior
private val GreenPrimary = Color(0xFF00A843)
private val NavInactive = Color(0xFF8E8E93)

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Map : Screen("map")
    object Profile : Screen("profile")
    object CreateJob : Screen("create_job")
    object EditJob : Screen("edit_job/{offerId}") {
        fun createRoute(offerId: Long) = "edit_job/$offerId"
    }
    object JobDetail : Screen("job_detail/{offerId}") {
        fun createRoute(offerId: Long) = "job_detail/$offerId"
    }
    object Applications : Screen("applications")
    object Admin : Screen("admin")
    object ManageCategories : Screen("manage_categories")
    object ManageUsers : Screen("manage_users")
}

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun AppNavigation(app: KonektaApp) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModel.Factory(context.applicationContext as android.app.Application, app.authRepository)
    )
    val authState by authViewModel.state.collectAsState()

    var showLogoutDialog by remember { mutableStateOf(false) }

    val currentUser = authState.currentUser
    val userRole = currentUser?.role ?: UserRole.CANDIDATE

    val bottomNavItems = remember(userRole) {
        when (userRole) {
            UserRole.ADMIN -> listOf(
                BottomNavItem(Screen.Home, "Início", Icons.Default.Home, Icons.Outlined.Home),
                BottomNavItem(Screen.Map, "Mapa", Icons.Default.Map, Icons.Outlined.Map),
                BottomNavItem(Screen.Admin, "Admin", Icons.Default.Work, Icons.Outlined.WorkOutline),
                BottomNavItem(Screen.Profile, "Perfil", Icons.Default.Person, Icons.Outlined.Person)
            )
            UserRole.EMPLOYER -> listOf(
                BottomNavItem(Screen.Home, "Início", Icons.Default.Home, Icons.Outlined.Home),
                BottomNavItem(Screen.Map, "Mapa", Icons.Default.Map, Icons.Outlined.Map),
                BottomNavItem(Screen.Applications, "Vagas", Icons.Default.Work, Icons.Outlined.WorkOutline),
                BottomNavItem(Screen.Profile, "Perfil", Icons.Default.Person, Icons.Outlined.Person)
            )
            else -> listOf(
                BottomNavItem(Screen.Home, "Início", Icons.Default.Home, Icons.Outlined.Home),
                BottomNavItem(Screen.Map, "Mapa", Icons.Default.Map, Icons.Outlined.Map),
                BottomNavItem(Screen.Applications, "Vagas", Icons.Default.Work, Icons.Outlined.WorkOutline),
                BottomNavItem(Screen.Profile, "Perfil", Icons.Default.Person, Icons.Outlined.Person)
            )
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.route in bottomNavItems.map { it.screen.route }

    // Diálogo de confirmação de Logout
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Sair") },
            text = { Text("Tem a certeza que deseja sair?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }) {
                    Text("Sair", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar && authState.isLoggedIn) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp
                ) {
                    bottomNavItems.forEach { item ->
                        val isSelected = currentDestination?.hierarchy?.any { it.route == item.screen.route } == true

                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            selected = isSelected,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = GreenPrimary,
                                selectedTextColor = GreenPrimary,
                                unselectedIconColor = NavInactive,
                                unselectedTextColor = NavInactive,
                                indicatorColor = Color.Transparent // Remove a pílula/fundo no ícone ativo
                            ),
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }

                    // Botão Sair integrado de forma limpa ao menu
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "Sair"
                            )
                        },
                        label = {
                            Text(
                                text = "Sair",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Normal
                            )
                        },
                        selected = false,
                        colors = NavigationBarItemDefaults.colors(
                            unselectedIconColor = NavInactive,
                            unselectedTextColor = NavInactive,
                            indicatorColor = Color.Transparent
                        ),
                        onClick = { showLogoutDialog = true }
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(color = Color.White)
                .padding(bottom = 0.dp)
        ) {
            // Profile Bar no topo
            if (authState.isLoggedIn && currentUser != null) {
                ProfileBar(
                    user = currentUser,
                    onProfileClick = { navController.navigate(Screen.Profile.route) }
                )
            }

            NavHost(
                navController = navController,
                startDestination = if (authState.isLoggedIn) Screen.Home.route else Screen.Login.route,
//                modifier = Modifier.weight(1f)
            ) {
                composable(Screen.Login.route) {
                    LoginScreen(
                        viewModel = authViewModel,
                        onLoginSuccess = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        },
                        onNavigateToRegister = {
                            navController.navigate(Screen.Register.route)
                        }
                    )
                }

                composable(Screen.Register.route) {
                    RegisterScreen(
                        viewModel = authViewModel,
                        onRegisterSuccess = {
                            navController.popBackStack()
                        },
                        onNavigateBack = {
                            navController.popBackStack()
                        }
                    )
                }

                composable(Screen.Home.route) {
                    val homeViewModel: HomeViewModel = viewModel(
                        factory = HomeViewModel.Factory(app.jobRepository, app.categoryRepository)
                    )
                    val applicationsViewModel: ApplicationsViewModel = viewModel(
                        factory = ApplicationsViewModel.Factory(app.jobRepository, app.authRepository)
                    )
                    val applications by applicationsViewModel.state.collectAsState()
                    val appliedJobIds = if (currentUser?.role == UserRole.CANDIDATE) {
                        applications.myApplications
                            .filter { it.status != com.example.konekta_mz_app.data.local.entity.ApplicationStatus.ACCEPTED }
                            .map { it.jobId }
                            .toSet()
                    } else {
                        emptySet()
                    }

                    HomeScreen(
                        viewModel = homeViewModel,
                        userRole = userRole,
                        appliedJobIds = appliedJobIds,
                        onOfferClick = { offerId ->
                            navController.navigate(Screen.JobDetail.createRoute(offerId))
                        },
                        onCreateOffer = {
                            navController.navigate(Screen.CreateJob.route)
                        }
                    )
                }

                composable(Screen.Map.route) {
                    val mapViewModel: MapViewModel = viewModel(
                        factory = MapViewModel.Factory(app.jobRepository)
                    )
                    MapScreen(
                        viewModel = mapViewModel,
                        onOfferClick = { offerId ->
                            navController.navigate(Screen.JobDetail.createRoute(offerId))
                        }
                    )
                }

                composable(Screen.Profile.route) {
                    val profileViewModel: ProfileViewModel = viewModel(
                        factory = ProfileViewModel.Factory(app.authRepository)
                    )
                    currentUser?.let { user ->
                        ProfileScreen(
                            userId = user.id,
                            viewModel = profileViewModel,
                            onNavigateBack = { navController.popBackStack() },
                            onLogout = {
                                authViewModel.logout()
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        )
                    }
                }

                composable(Screen.CreateJob.route) {
                    val jobViewModel: JobViewModel = viewModel(
                        factory = JobViewModel.Factory(app.jobRepository, app.categoryRepository)
                    )
                    currentUser?.let { user ->
                        CreateJobScreen(
                            currentUser = user,
                            viewModel = jobViewModel,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }

                composable(
                    route = Screen.EditJob.route,
                    arguments = listOf(navArgument("offerId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val offerId = backStackEntry.arguments?.getLong("offerId") ?: return@composable
                    val jobViewModel: JobViewModel = viewModel(
                        factory = JobViewModel.Factory(app.jobRepository, app.categoryRepository)
                    )
                    currentUser?.let { user ->
                        EditJobScreen(
                            offerId = offerId,
                            currentUser = user,
                            viewModel = jobViewModel,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }

                composable(
                    route = Screen.JobDetail.route,
                    arguments = listOf(navArgument("offerId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val offerId = backStackEntry.arguments?.getLong("offerId") ?: return@composable
                    val jobViewModel: JobViewModel = viewModel(
                        factory = JobViewModel.Factory(app.jobRepository, app.categoryRepository)
                    )
                    val applicationsViewModel: ApplicationsViewModel = viewModel(
                        factory = ApplicationsViewModel.Factory(app.jobRepository, app.authRepository)
                    )
                    currentUser?.let { user ->
                        JobDetailScreen(
                            offerId = offerId,
                            currentUser = user,
                            jobViewModel = jobViewModel,
                            applicationsViewModel = applicationsViewModel,
                            onNavigateBack = { navController.popBackStack() },
                            onEditOffer = { navController.navigate(Screen.EditJob.createRoute(offerId)) }
                        )
                    }
                }

                composable(Screen.Applications.route) {
                    val applicationsViewModel: ApplicationsViewModel = viewModel(
                        factory = ApplicationsViewModel.Factory(app.jobRepository, app.authRepository)
                    )
                    currentUser?.let { user ->
                        ApplicationsScreen(
                            userId = user.id,
                            userRole = user.role,
                            viewModel = applicationsViewModel,
                            onNavigateBack = { navController.popBackStack() },
                            onJobClick = { jobId ->
                                navController.navigate(Screen.JobDetail.createRoute(jobId))
                            }
                        )
                    }
                }

                composable(Screen.Admin.route) {
                    val adminViewModel: AdminViewModel = viewModel(
                        factory = AdminViewModel.Factory(app.authRepository, app.jobRepository)
                    )
                    AdminScreen(
                        viewModel = adminViewModel,
                        onNavigateBack = { navController.popBackStack() },
                        onManageCategories = { navController.navigate(Screen.ManageCategories.route) },
                        onManageUsers = { navController.navigate(Screen.ManageUsers.route) }
                    )
                }

                composable(Screen.ManageCategories.route) {
                    ManageCategoriesScreen(
                        categoryRepository = app.categoryRepository,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(Screen.ManageUsers.route) {
                    ManageUsersScreen(
                        authRepository = app.authRepository,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}