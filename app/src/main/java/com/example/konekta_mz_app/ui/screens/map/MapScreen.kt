//package com.example.konekta_mz_app.ui.screens.map
//
//import android.Manifest
//import android.content.Context
//import android.content.pm.PackageManager
//import android.graphics.Bitmap
//import android.graphics.Canvas
//import android.graphics.Color
//import android.graphics.Paint
//import android.graphics.Path
//import android.graphics.PorterDuff
//import android.graphics.PorterDuffXfermode
//import android.graphics.Rect
//import android.graphics.RectF
//import android.graphics.drawable.BitmapDrawable
//import android.os.Handler
//import android.os.Looper
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.animation.AnimatedVisibility
//import androidx.compose.animation.fadeIn
//import androidx.compose.animation.fadeOut
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.MyLocation
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.FloatingActionButton
//import androidx.compose.material3.Icon
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.material3.TopAppBar
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.DisposableEffect
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.viewinterop.AndroidView
//import androidx.core.content.ContextCompat
//import coil.ImageLoader
//import coil.request.ImageRequest
//import coil.request.SuccessResult
//import com.example.konekta_mz_app.data.local.entity.JobOffer
//import com.example.konekta_mz_app.util.LocationHelper
//import com.example.konekta_mz_app.viewmodel.MapViewModel
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import org.osmdroid.config.Configuration
//import org.osmdroid.tileprovider.MapTileProviderBase
//import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
//import org.osmdroid.util.GeoPoint
//import org.osmdroid.util.MapTileIndex
//import org.osmdroid.views.MapView
//import org.osmdroid.views.overlay.Marker
//import java.io.File
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun MapScreen(
//    viewModel: MapViewModel,
//    currentUserPhotoPath: String? = null,
//    onOfferClick: (Long) -> Unit
//) {
//    val state by viewModel.state.collectAsState()
//    val context = LocalContext.current
//    val locationHelper = remember { LocationHelper(context) }
//    var hasPermission by remember {
//        mutableStateOf(
//            ContextCompat.checkSelfPermission(
//                context,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//        )
//    }
//
//    val permissionLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.RequestPermission()
//    ) { granted ->
//        hasPermission = granted
//    }
//
//    LaunchedEffect(hasPermission) {
//        if (hasPermission) {
//            val loc = locationHelper.getCurrentLocation()
//            loc?.let { (lat, lng) ->
//                viewModel.updateLocation(lat, lng)
//            }
//        }
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Mapa de Empregos") }
//            )
//        },
//        floatingActionButton = {
//            FloatingActionButton(
//                onClick = {
//                    if (!hasPermission) {
//                        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
//                    }
//                }
//            ) {
//                Icon(Icons.Default.MyLocation, contentDescription = "Minha Localização")
//            }
//        }
//    ) { paddingValues ->
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//        ) {
//            if (state.isLoading) {
//                Box(
//                    modifier = Modifier.fillMaxSize(),
//                    contentAlignment = Alignment.Center
//                ) {
//                    CircularProgressIndicator()
//                }
//            } else {
//                OsmMapView(
//                    context = context,
//                    offers = state.offers,
//                    userLat = state.userLatitude,
//                    userLng = state.userLongitude,
//                    userPhotoPath = currentUserPhotoPath,
//                    zoomLevel = if (state.zoomLevel == 0.0) 17.0 else state.zoomLevel,
//                    onOfferClick = onOfferClick
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun OsmMapView(
//    context: Context,
//    offers: List<JobOffer>,
//    userLat: Double,
//    userLng: Double,
//    userPhotoPath: String?,
//    zoomLevel: Double,
//    onOfferClick: (Long) -> Unit
//) {
//    val scope = rememberCoroutineScope()
//    var isMapTilesLoading by remember { mutableStateOf(true) }
//
//    DisposableEffect(Unit) {
//        Configuration.getInstance().apply {
//            userAgentValue = "com.seuapp.map/1.0 (Android)"
//            osmdroidBasePath = context.filesDir
//            osmdroidTileCache = context.cacheDir
//        }
//        onDispose { }
//    }
//
//    val openStreetMapTileSource = remember {
//        object : OnlineTileSourceBase(
//            "OpenStreetMapStandard",
//            0, 19, 256, ".png",
//            arrayOf(
//                "https://a.tile.openstreetmap.org/",
//                "https://b.tile.openstreetmap.org/",
//                "https://c.tile.openstreetmap.org/"
//            )
//        ) {
//            override fun getTileURLString(pMapTileIndex: Long): String {
//                return baseUrl +
//                        MapTileIndex.getZoom(pMapTileIndex) + "/" +
//                        MapTileIndex.getX(pMapTileIndex) + "/" +
//                        MapTileIndex.getY(pMapTileIndex) + mImageFilenameEnding
//            }
//        }
//    }
//
//    Box(modifier = Modifier.fillMaxSize()) {
//        AndroidView(
//            factory = { ctx ->
//                MapView(ctx).apply {
//                    setTileSource(openStreetMapTileSource)
//                    setMultiTouchControls(true)
//                    isTilesScaledToDpi = true
//
//                    controller.setZoom(zoomLevel)
//                    controller.setCenter(GeoPoint(userLat, userLng))
//
//                    // Registrar callback para monitorar o download de tiles
//                    tileProvider.setTileRequestCompleteHandler(Handler(Looper.getMainLooper()) { msg ->
//                        if (msg.what == MapTileProviderBase.MAPTILE_SUCCESS_ID) {
//                            // Quando a primeira leva de tiles for concluída com sucesso
//                            isMapTilesLoading = false
//                        }
//                        true
//                    })
//
//                    // Marcador do Utilizador (Com Foto de Perfil)
//                    val userMarker = Marker(this).apply {
//                        position = GeoPoint(userLat, userLng)
//                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
//                        title = "Você está aqui"
//                    }
//                    overlays.add(userMarker)
//
//                    scope.launch {
//                        val userBitmap = loadBitmapFromPathOrUri(ctx, userPhotoPath)
//                        val customUserIcon = createCircularMarkerBitmap(
//                            sourceBitmap = userBitmap,
//                            borderColor = Color.parseColor("#1976D2"),
//                            sizePx = 130
//                        )
//                        userMarker.icon = BitmapDrawable(ctx.resources, customUserIcon)
//                        invalidate()
//                    }
//
//                    // Marcadores das Ofertas de Emprego (Com Foto da Empresa/Vaga)
//                    offers.forEach { offer ->
//                        if (offer.latitude != 0.0 && offer.longitude != 0.0) {
//                            val offerMarker = Marker(this).apply {
//                                position = GeoPoint(offer.latitude, offer.longitude)
//                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
//                                title = offer.title
//                                snippet = "${offer.employerName}\n${offer.location}"
//                                setOnMarkerClickListener { _, _ ->
//                                    onOfferClick(offer.id)
//                                    true
//                                }
//                            }
//                            overlays.add(offerMarker)
//
//                            scope.launch {
//                                val offerBitmap = loadBitmapFromPathOrUri(ctx, offer.imagePath)
//                                val customOfferIcon = createCircularMarkerBitmap(
//                                    sourceBitmap = offerBitmap,
//                                    borderColor = Color.parseColor("#388E3C"),
//                                    sizePx = 120
//                                )
//                                offerMarker.icon = BitmapDrawable(ctx.resources, customOfferIcon)
//                                invalidate()
//                            }
//                        }
//                    }
//                }
//            },
//            update = { mapView ->
//                if (userLat != 0.0 && userLng != 0.0) {
//                    mapView.controller.setCenter(GeoPoint(userLat, userLng))
//                }
//            },
//            modifier = Modifier.fillMaxSize()
//        )
//
//        // Overlay do Loader enquanto os tiles do mapa carregam
//        AnimatedVisibility(
//            visible = isMapTilesLoading,
//            enter = fadeIn(),
//            exit = fadeOut(),
//            modifier = Modifier.fillMaxSize()
//        ) {
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.7f)),
//                contentAlignment = Alignment.Center
//            ) {
//                CircularProgressIndicator()
//            }
//        }
//    }
//}
//
//private suspend fun loadBitmapFromPathOrUri(context: Context, path: String?): Bitmap? {
//    if (path.isNullOrBlank()) return null
//    return withContext(Dispatchers.IO) {
//        try {
//            val imageLoader = ImageLoader(context)
//            val request = ImageRequest.Builder(context)
//                .data(if (path.startsWith("http") || path.startsWith("content:")) path else File(path))
//                .allowHardware(false)
//                .build()
//
//            val result = imageLoader.execute(request)
//            if (result is SuccessResult) {
//                (result.drawable as? BitmapDrawable)?.bitmap
//            } else null
//        } catch (e: Exception) {
//            null
//        }
//    }
//}
//
//private fun createCircularMarkerBitmap(
//    sourceBitmap: Bitmap?,
//    borderColor: Int,
//    sizePx: Int = 120
//): Bitmap {
//    val pinHeight = sizePx + (sizePx * 0.25f).toInt()
//    val output = Bitmap.createBitmap(sizePx, pinHeight, Bitmap.Config.ARGB_8888)
//    val canvas = Canvas(output)
//
//    val radius = sizePx / 2f
//    val borderWidth = 6f
//
//    val pointerPath = Path().apply {
//        moveTo(radius - 18f, sizePx - 8f)
//        lineTo(radius, pinHeight.toFloat())
//        lineTo(radius + 18f, sizePx - 8f)
//        close()
//    }
//    val pointerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
//        color = borderColor
//        style = Paint.Style.FILL
//    }
//    canvas.drawPath(pointerPath, pointerPaint)
//
//    val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
//        color = borderColor
//        style = Paint.Style.FILL
//    }
//    canvas.drawCircle(radius, radius, radius, borderPaint)
//
//    val contentRadius = radius - borderWidth
//    val imagePaint = Paint(Paint.ANTI_ALIAS_FLAG)
//
//    if (sourceBitmap != null) {
//        val srcRect = Rect(0, 0, sourceBitmap.width, sourceBitmap.height)
//        val destRect = RectF(
//            borderWidth,
//            borderWidth,
//            sizePx - borderWidth,
//            sizePx - borderWidth
//        )
//
//        val layer = canvas.saveLayer(0f, 0f, sizePx.toFloat(), sizePx.toFloat(), null)
//        canvas.drawCircle(radius, radius, contentRadius, imagePaint)
//        imagePaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
//        canvas.drawBitmap(sourceBitmap, srcRect, destRect, imagePaint)
//        imagePaint.xfermode = null
//        canvas.restoreToCount(layer)
//    } else {
//        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
//            color = Color.WHITE
//            style = Paint.Style.FILL
//        }
//        canvas.drawCircle(radius, radius, contentRadius, bgPaint)
//    }
//
//    return output
//}
//
//package com.example.konekta_mz_app.ui.screens.map
//
//import android.Manifest
//import android.content.Context
//import android.content.pm.PackageManager
//import android.graphics.Bitmap
//import android.graphics.Canvas
//import android.graphics.Color
//import android.graphics.Paint
//import android.graphics.Path
//import android.graphics.PorterDuff
//import android.graphics.PorterDuffXfermode
//import android.graphics.Rect
//import android.graphics.RectF
//import android.graphics.drawable.BitmapDrawable
//import android.os.Handler
//import android.os.Looper
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.animation.AnimatedVisibility
//import androidx.compose.animation.fadeIn
//import androidx.compose.animation.fadeOut
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.statusBarsPadding
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.MyLocation
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.FloatingActionButton
//import androidx.compose.material3.Icon
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.DisposableEffect
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.shadow
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.compose.ui.viewinterop.AndroidView
//import androidx.core.content.ContextCompat
//import coil.ImageLoader
//import coil.request.ImageRequest
//import coil.request.SuccessResult
//import com.example.konekta_mz_app.data.local.entity.JobOffer
//import com.example.konekta_mz_app.util.LocationHelper
//import com.example.konekta_mz_app.viewmodel.MapViewModel
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import org.osmdroid.config.Configuration
//import org.osmdroid.tileprovider.MapTileProviderBase
//import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
//import org.osmdroid.util.GeoPoint
//import org.osmdroid.util.MapTileIndex
//import org.osmdroid.views.MapView
//import org.osmdroid.views.overlay.Marker
//import java.io.File
//
//private val TextDark = androidx.compose.ui.graphics.Color(0xFF111827)
//private val GreenPrimary = androidx.compose.ui.graphics.Color(0xFF00A843)
//
//@Composable
//fun MapScreen(
//    viewModel: MapViewModel,
//    currentUserPhotoPath: String? = null,
//    onOfferClick: (Long) -> Unit
//) {
//    val state by viewModel.state.collectAsState()
//    val context = LocalContext.current
//    val locationHelper = remember { LocationHelper(context) }
//    var hasPermission by remember {
//        mutableStateOf(
//            ContextCompat.checkSelfPermission(
//                context,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//        )
//    }
//
//    val permissionLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.RequestPermission()
//    ) { granted ->
//        hasPermission = granted
//    }
//
////    LaunchedEffect(hasPermission) {
////        if (hasPermission) {
////            val loc = locationHelper.getCurrentLocation()
////            loc?.let { (lat, lng) ->
////                viewModel.updateLocation(lat, lng)
////            }
////        }
////    }
//    DisposableEffect(hasPermission) {
//
//        if (hasPermission) {
//
//            locationHelper.startLocationUpdates { lat, lng ->
//
//                viewModel.updateLocation(
//                    lat,
//                    lng
//                )
//            }
//        }
//
//        onDispose {
//            locationHelper.stopLocationUpdates()
//        }
//    }
//
//    // Substituído Scaffold por Box para remover paddings/insets aninhados
//    Box(
//        modifier = Modifier.fillMaxSize()
//    ) {
//        // Conteúdo Principal do Mapa ou Loader
//        if (state.isLoading) {
//            Box(
//                modifier = Modifier.fillMaxSize(),
//                contentAlignment = Alignment.Center
//            ) {
//                CircularProgressIndicator(color = GreenPrimary)
//            }
//        } else {
//            OsmMapView(
//                context = context,
//                offers = state.offers,
//                userLat = state.userLatitude,
//                userLng = state.userLongitude,
//                userPhotoPath = currentUserPhotoPath,
//                zoomLevel = if (state.zoomLevel == 0.0) 17.0 else state.zoomLevel,
//                onOfferClick = onOfferClick
//            )
//        }
//
//        // Barra Superior / Top Bar Customizada (Substitui o TopAppBar do Scaffold)
////        Box(
////            modifier = Modifier
////                .fillMaxWidth()
////                .statusBarsPadding()
////                .padding(16.dp)
////        ) {
////            Row(
////                modifier = Modifier
////                    .fillMaxWidth()
////                    .height(56.dp)
////                    .shadow(4.dp, RoundedCornerShape(16.dp))
////                    .background(
////                        color = androidx.compose.ui.graphics.Color.White,
////                        shape = RoundedCornerShape(16.dp)
////                    )
////                    .padding(horizontal = 16.dp),
////                verticalAlignment = Alignment.CenterVertically
////            ) {
////                Text(
////                    text = "Mapa de Empregos",
////                    fontSize = 18.sp,
////                    fontWeight = FontWeight.Bold,
////                    color = TextDark
////                )
////            }
////        }
//
//        // Botão Flutuante (FAB) para Minha Localização
//        FloatingActionButton(
//            onClick = {
//                if (!hasPermission) {
//                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
//                }
//            },
//            containerColor = androidx.compose.ui.graphics.Color.White,
//            contentColor = GreenPrimary,
//            modifier = Modifier
//                .align(Alignment.BottomEnd)
//                .padding(bottom = 24.dp, end = 16.dp)
//        ) {
//            Icon(Icons.Default.MyLocation, contentDescription = "Minha Localização")
//        }
//    }
//}
//
//@Composable
//fun OsmMapView(
//    context: Context,
//    offers: List<JobOffer>,
//    userLat: Double,
//    userLng: Double,
//    userPhotoPath: String?,
//    zoomLevel: Double,
//    onOfferClick: (Long) -> Unit
//) {
//    val scope = rememberCoroutineScope()
//    var isMapTilesLoading by remember { mutableStateOf(true) }
//
//    DisposableEffect(Unit) {
//        Configuration.getInstance().apply {
//            userAgentValue = "com.seuapp.map/1.0 (Android)"
//            osmdroidBasePath = context.filesDir
//            osmdroidTileCache = context.cacheDir
//        }
//        onDispose { }
//    }
//
//    val openStreetMapTileSource = remember {
//        object : OnlineTileSourceBase(
//            "OpenStreetMapStandard",
//            0, 19, 256, ".png",
//            arrayOf(
//                "https://a.tile.openstreetmap.org/",
//                "https://b.tile.openstreetmap.org/",
//                "https://c.tile.openstreetmap.org/"
//            )
//        ) {
//            override fun getTileURLString(pMapTileIndex: Long): String {
//                return baseUrl +
//                        MapTileIndex.getZoom(pMapTileIndex) + "/" +
//                        MapTileIndex.getX(pMapTileIndex) + "/" +
//                        MapTileIndex.getY(pMapTileIndex) + mImageFilenameEnding
//            }
//        }
//    }
//
//    Box(modifier = Modifier.fillMaxSize()) {
//        AndroidView(
//            factory = { ctx ->
//                MapView(ctx).apply {
//                    setTileSource(openStreetMapTileSource)
//                    setMultiTouchControls(true)
//                    isTilesScaledToDpi = true
//
//                    controller.setZoom(zoomLevel)
//                    controller.setCenter(GeoPoint(userLat, userLng))
//
//                    // Registrar callback para monitorar o download de tiles
//                    tileProvider.setTileRequestCompleteHandler(Handler(Looper.getMainLooper()) { msg ->
//                        if (msg.what == MapTileProviderBase.MAPTILE_SUCCESS_ID) {
//                            // Quando a primeira leva de tiles for concluída com sucesso
//                            isMapTilesLoading = false
//                        }
//                        true
//                    })
//
//                    // Marcador do Utilizador (Com Foto de Perfil)
//                    val userMarker = Marker(this).apply {
//                        position = GeoPoint(userLat, userLng)
//                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
//                        title = "Você está aqui"
//                    }
//                    val userMarker = remember {
//                        mutableStateOf<Marker?>(null)
//                    }
//                    overlays.add(userMarker)
//
//                    scope.launch {
//                        val userBitmap = loadBitmapFromPathOrUri(ctx, userPhotoPath)
//                        val customUserIcon = createCircularMarkerBitmap(
//                            sourceBitmap = userBitmap,
//                            borderColor = Color.parseColor("#1976D2"),
//                            sizePx = 130
//                        )
//                        userMarker.icon = BitmapDrawable(ctx.resources, customUserIcon)
//                        invalidate()
//                    }
//
//                    // Marcadores das Ofertas de Emprego (Com Foto da Empresa/Vaga)
//                    offers.forEach { offer ->
//                        if (offer.latitude != 0.0 && offer.longitude != 0.0) {
//                            val offerMarker = Marker(this).apply {
//                                position = GeoPoint(offer.latitude, offer.longitude)
//                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
//                                title = offer.title
//                                snippet = "${offer.employerName}\n${offer.location}"
//                                setOnMarkerClickListener { _, _ ->
//                                    onOfferClick(offer.id)
//                                    true
//                                }
//                            }
//                            overlays.add(offerMarker)
//
//                            scope.launch {
//                                val offerBitmap = loadBitmapFromPathOrUri(ctx, offer.imagePath)
//                                val customOfferIcon = createCircularMarkerBitmap(
//                                    sourceBitmap = offerBitmap,
//                                    borderColor = Color.parseColor("#388E3C"),
//                                    sizePx = 120
//                                )
//                                offerMarker.icon = BitmapDrawable(ctx.resources, customOfferIcon)
//                                invalidate()
//                            }
//                        }
//                    }
//                }
//            },
//            update = { mapView ->
//                if (userLat != 0.0 && userLng != 0.0) {
//                    mapView.controller.setCenter(GeoPoint(userLat, userLng))
//                }
//            },
//            modifier = Modifier.fillMaxSize()
//        )
//
//        // Overlay do Loader enquanto os tiles do mapa carregam
//        AnimatedVisibility(
//            visible = isMapTilesLoading,
//            enter = fadeIn(),
//            exit = fadeOut(),
//            modifier = Modifier.fillMaxSize()
//        ) {
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.7f)),
//                contentAlignment = Alignment.Center
//            ) {
//                CircularProgressIndicator(color = GreenPrimary)
//            }
//        }
//    }
//}
//
//private suspend fun loadBitmapFromPathOrUri(context: Context, path: String?): Bitmap? {
//    if (path.isNullOrBlank()) return null
//    return withContext(Dispatchers.IO) {
//        try {
//            val imageLoader = ImageLoader(context)
//            val request = ImageRequest.Builder(context)
//                .data(if (path.startsWith("http") || path.startsWith("content:")) path else File(path))
//                .allowHardware(false)
//                .build()
//
//            val result = imageLoader.execute(request)
//            if (result is SuccessResult) {
//                (result.drawable as? BitmapDrawable)?.bitmap
//            } else null
//        } catch (e: Exception) {
//            null
//        }
//    }
//}
//
//private fun createCircularMarkerBitmap(
//    sourceBitmap: Bitmap?,
//    borderColor: Int,
//    sizePx: Int = 120
//): Bitmap {
//    val pinHeight = sizePx + (sizePx * 0.25f).toInt()
//    val output = Bitmap.createBitmap(sizePx, pinHeight, Bitmap.Config.ARGB_8888)
//    val canvas = Canvas(output)
//
//    val radius = sizePx / 2f
//    val borderWidth = 6f
//
//    val pointerPath = Path().apply {
//        moveTo(radius - 18f, sizePx - 8f)
//        lineTo(radius, pinHeight.toFloat())
//        lineTo(radius + 18f, sizePx - 8f)
//        close()
//    }
//    val pointerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
//        color = borderColor
//        style = Paint.Style.FILL
//    }
//    canvas.drawPath(pointerPath, pointerPaint)
//
//    val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
//        color = borderColor
//        style = Paint.Style.FILL
//    }
//    canvas.drawCircle(radius, radius, radius, borderPaint)
//
//    val contentRadius = radius - borderWidth
//    val imagePaint = Paint(Paint.ANTI_ALIAS_FLAG)
//
//    if (sourceBitmap != null) {
//        val srcRect = Rect(0, 0, sourceBitmap.width, sourceBitmap.height)
//        val destRect = RectF(
//            borderWidth,
//            borderWidth,
//            sizePx - borderWidth,
//            sizePx - borderWidth
//        )
//
//        val layer = canvas.saveLayer(0f, 0f, sizePx.toFloat(), sizePx.toFloat(), null)
//        canvas.drawCircle(radius, radius, contentRadius, imagePaint)
//        imagePaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
//        canvas.drawBitmap(sourceBitmap, srcRect, destRect, imagePaint)
//        imagePaint.xfermode = null
//        canvas.restoreToCount(layer)
//    } else {
//        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
//            color = Color.WHITE
//            style = Paint.Style.FILL
//        }
//        canvas.drawCircle(radius, radius, contentRadius, bgPaint)
//    }
//
//    return output
//}

//Konekta
//package com.example.konekta_mz_app.ui.screens.map
//
//import android.Manifest
//import android.content.Context
//import android.content.pm.PackageManager
//import android.graphics.Bitmap
//import android.graphics.Canvas
//import android.graphics.Color
//import android.graphics.Paint
//import android.graphics.Path
//import android.graphics.PorterDuff
//import android.graphics.PorterDuffXfermode
//import android.graphics.Rect
//import android.graphics.RectF
//import android.graphics.drawable.BitmapDrawable
//import android.os.Handler
//import android.os.Looper
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.animation.AnimatedVisibility
//import androidx.compose.animation.fadeIn
//import androidx.compose.animation.fadeOut
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.MyLocation
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.FloatingActionButton
//import androidx.compose.material3.Icon
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.DisposableEffect
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.viewinterop.AndroidView
//import androidx.core.content.ContextCompat
//import coil.ImageLoader
//import coil.request.ImageRequest
//import coil.request.SuccessResult
//import com.example.konekta_mz_app.data.local.entity.JobOffer
//import com.example.konekta_mz_app.util.LocationHelper
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import org.osmdroid.config.Configuration
//import org.osmdroid.tileprovider.MapTileProviderBase
//import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
//import org.osmdroid.util.GeoPoint
//import org.osmdroid.util.MapTileIndex
//import org.osmdroid.views.MapView
//import org.osmdroid.views.overlay.Marker
//import java.io.File
//
//private val GreenPrimary =
//    androidx.compose.ui.graphics.Color(0xFF00A843)
//
//@Composable
//fun MapScreen(
//    viewModel: MapViewModel,
//    currentUserPhotoPath: String? = null,
//    onOfferClick: (Long) -> Unit
//) {
//    val state by viewModel.state.collectAsState()
//
//    val context = LocalContext.current
//
//    val locationHelper = remember {
//        LocationHelper(context)
//    }
//
//    var hasPermission by remember {
//        mutableStateOf(
//            ContextCompat.checkSelfPermission(
//                context,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//        )
//    }
//
//    /*
//     * Solicitação de permissão.
//     */
//    val permissionLauncher =
//        rememberLauncherForActivityResult(
//            contract = ActivityResultContracts.RequestPermission()
//        ) { granted ->
//            hasPermission = granted
//        }
//
//    /*
//     * Acompanha continuamente a localização.
//     *
//     * Enquanto esta tela estiver aberta:
//     *
//     * GPS
//     *   ↓
//     * LocationHelper
//     *   ↓
//     * nova latitude/longitude
//     *   ↓
//     * ViewModel
//     *   ↓
//     * mapa
//     */
//    DisposableEffect(hasPermission) {
//
//        if (hasPermission) {
//
//            locationHelper.startLocationUpdates { lat, lng ->
//
//                viewModel.updateLocation(
//                    lat,
//                    lng
//                )
//            }
//        }
//
//        onDispose {
//            locationHelper.stopLocationUpdates()
//        }
//    }
//
//    /*
//     * Quando a permissão ainda não existe,
//     * pede automaticamente.
//     */
//    LaunchedEffect(Unit) {
//
//        if (!hasPermission) {
//            permissionLauncher.launch(
//                Manifest.permission.ACCESS_FINE_LOCATION
//            )
//        }
//    }
//
//    /*
//     * Guarda uma referência ao MapView para que
//     * o botão "Minha localização" consiga recentrar
//     * o mapa.
//     */
//    var mapViewReference by remember {
//        mutableStateOf<MapView?>(null)
//    }
//
//    Box(
//        modifier = Modifier.fillMaxSize()
//    ) {
//
//        /*
//         * Mapa / Loader
//         */
//        if (state.isLoading) {
//
//            Box(
//                modifier = Modifier.fillMaxSize(),
//                contentAlignment = Alignment.Center
//            ) {
//
//                CircularProgressIndicator(
//                    color = GreenPrimary
//                )
//            }
//
//        } else {
//
//            OsmMapView(
//                context = context,
//                offers = state.offers,
//                userLat = state.userLatitude,
//                userLng = state.userLongitude,
//                userPhotoPath = currentUserPhotoPath,
//                zoomLevel =
//                if (state.zoomLevel == 0.0) {
//                    17.0
//                } else {
//                    state.zoomLevel
//                },
//                onMapViewCreated = { mapView ->
//                    mapViewReference = mapView
//                },
//                onOfferClick = onOfferClick
//            )
//        }
//
//        /*
//         * Botão "Minha localização".
//         *
//         * Ele NÃO é usado para atualizar o GPS.
//         * O GPS já está sendo atualizado continuamente.
//         *
//         * O botão apenas centraliza o mapa na posição atual.
//         */
//        FloatingActionButton(
//            onClick = {
//
//                if (!hasPermission) {
//
//                    permissionLauncher.launch(
//                        Manifest.permission.ACCESS_FINE_LOCATION
//                    )
//
//                } else {
//
//                    val lat = state.userLatitude
//                    val lng = state.userLongitude
//
//                    if (lat != 0.0 && lng != 0.0) {
//
//                        mapViewReference?.controller?.animateTo(
//                            GeoPoint(lat, lng)
//                        )
//                    }
//                }
//            },
//            containerColor =
//            androidx.compose.ui.graphics.Color.White,
//            contentColor = GreenPrimary,
//            modifier = Modifier
//                .align(Alignment.BottomEnd)
//                .padding(
//                    bottom = 24.dp,
//                    end = 16.dp
//                )
//        ) {
//
//            Icon(
//                imageVector = Icons.Default.MyLocation,
//                contentDescription = "Minha Localização"
//            )
//        }
//    }
//}
//
//@Composable
//fun OsmMapView(
//    context: Context,
//    offers: List<JobOffer>,
//    userLat: Double,
//    userLng: Double,
//    userPhotoPath: String?,
//    zoomLevel: Double,
//    onMapViewCreated: (MapView) -> Unit,
//    onOfferClick: (Long) -> Unit
//) {
//    var isMapTilesLoading by remember {
//        mutableStateOf(true)
//    }
//
//    /*
//     * Configuração do osmdroid.
//     */
//    DisposableEffect(Unit) {
//
//        Configuration.getInstance().apply {
//
//            userAgentValue =
//                "com.seuapp.map/1.0 (Android)"
//
//            osmdroidBasePath =
//                context.filesDir
//
//            osmdroidTileCache =
//                context.cacheDir
//        }
//
//        onDispose {
//        }
//    }
//
//    /*
//     * Tile source do OpenStreetMap.
//     */
//    val openStreetMapTileSource = remember {
//
//        object : OnlineTileSourceBase(
//            "OpenStreetMapStandard",
//            0,
//            19,
//            256,
//            ".png",
//            arrayOf(
//                "https://a.tile.openstreetmap.org/",
//                "https://b.tile.openstreetmap.org/",
//                "https://c.tile.openstreetmap.org/"
//            )
//        ) {
//
//            override fun getTileURLString(
//                pMapTileIndex: Long
//            ): String {
//
//                return baseUrl +
//                        MapTileIndex.getZoom(
//                            pMapTileIndex
//                        ) +
//                        "/" +
//                        MapTileIndex.getX(
//                            pMapTileIndex
//                        ) +
//                        "/" +
//                        MapTileIndex.getY(
//                            pMapTileIndex
//                        ) +
//                        mImageFilenameEnding
//            }
//        }
//    }
//
//    /*
//     * Referência persistente do marcador do usuário.
//     *
//     * IMPORTANTE:
//     * O remember fica fora do MapView.apply {},
//     * porque remember é uma API do Compose.
//     */
//    val userMarkerState =
//        remember {
//            mutableStateOf<Marker?>(null)
//        }
//
//    /*
//     * Guarda o último conjunto de ofertas para que
//     * possamos evitar problemas de recriação desnecessária.
//     */
//    Box(
//        modifier = Modifier.fillMaxSize()
//    ) {
//
//        AndroidView(
//
//            factory = { ctx ->
//
//                MapView(ctx).apply {
//
//                    /*
//                     * Informa o MapView criado para o MapScreen.
//                     */
//                    onMapViewCreated(this)
//
//                    setTileSource(
//                        openStreetMapTileSource
//                    )
//
//                    setMultiTouchControls(true)
//
//                    isTilesScaledToDpi = true
//
//                    /*
//                     * Zoom inicial.
//                     */
//                    controller.setZoom(
//                        zoomLevel
//                    )
//
//                    /*
//                     * Centro inicial.
//                     *
//                     * Só fazemos isso se já existir
//                     * uma localização válida.
//                     */
//                    if (
//                        userLat != 0.0 &&
//                        userLng != 0.0
//                    ) {
//
//                        controller.setCenter(
//                            GeoPoint(
//                                userLat,
//                                userLng
//                            )
//                        )
//                    }
//
//                    /*
//                     * Monitor de carregamento dos tiles.
//                     */
//                    tileProvider.setTileRequestCompleteHandler(
//                        Handler(
//                            Looper.getMainLooper()
//                        ) { msg ->
//
//                            if (
//                                msg.what ==
//                                MapTileProviderBase
//                                    .MAPTILE_SUCCESS_ID
//                            ) {
//
//                                isMapTilesLoading = false
//                            }
//
//                            true
//                        }
//                    )
//
//                    /*
//                     * =========================
//                     * MARCADOR DO USUÁRIO
//                     * =========================
//                     */
//                    val marker =
//                        Marker(this).apply {
//
//                            position = GeoPoint(
//                                userLat,
//                                userLng
//                            )
//
//                            setAnchor(
//                                Marker.ANCHOR_CENTER,
//                                Marker.ANCHOR_BOTTOM
//                            )
//
//                            title = "Você está aqui"
//                        }
//
//                    userMarkerState.value =
//                        marker
//
//                    overlays.add(marker)
//
//                    /*
//                     * Carrega a foto do usuário.
//                     */
//                    kotlinx.coroutines.MainScope().launch {
//
//                        val userBitmap =
//                            loadBitmapFromPathOrUri(
//                                ctx,
//                                userPhotoPath
//                            )
//
//                        val customUserIcon =
//                            createCircularMarkerBitmap(
//                                sourceBitmap =
//                                userBitmap,
//                                borderColor =
//                                Color.parseColor(
//                                    "#1976D2"
//                                ),
//                                sizePx = 130
//                            )
//
//                        marker.icon =
//                            BitmapDrawable(
//                                ctx.resources,
//                                customUserIcon
//                            )
//
//                        invalidate()
//                    }
//
//                    /*
//                     * =========================
//                     * MARCADORES DAS OFERTAS
//                     * =========================
//                     */
//                    offers.forEach { offer ->
//
//                        if (
//                            offer.latitude != 0.0 &&
//                            offer.longitude != 0.0
//                        ) {
//
//                            val offerMarker =
//                                Marker(this).apply {
//
//                                    position =
//                                        GeoPoint(
//                                            offer.latitude,
//                                            offer.longitude
//                                        )
//
//                                    setAnchor(
//                                        Marker.ANCHOR_CENTER,
//                                        Marker.ANCHOR_BOTTOM
//                                    )
//
//                                    title =
//                                        offer.title
//
//                                    snippet =
//                                        "${offer.employerName}\n" +
//                                                offer.location
//
//                                    setOnMarkerClickListener {
//                                            _, _ ->
//
//                                        onOfferClick(
//                                            offer.id
//                                        )
//
//                                        true
//                                    }
//                                }
//
//                            overlays.add(
//                                offerMarker
//                            )
//
//                            kotlinx.coroutines
//                                .MainScope()
//                                .launch {
//
//                                    val offerBitmap =
//                                        loadBitmapFromPathOrUri(
//                                            ctx,
//                                            offer.imagePath
//                                        )
//
//                                    val customOfferIcon =
//                                        createCircularMarkerBitmap(
//                                            sourceBitmap =
//                                            offerBitmap,
//                                            borderColor =
//                                            Color.parseColor(
//                                                "#388E3C"
//                                            ),
//                                            sizePx = 120
//                                        )
//
//                                    offerMarker.icon =
//                                        BitmapDrawable(
//                                            ctx.resources,
//                                            customOfferIcon
//                                        )
//
//                                    invalidate()
//                                }
//                        }
//                    }
//                }
//            },
//
//            /*
//             * Executado quando os valores do Compose
//             * mudam.
//             */
//            update = { mapView ->
//
//                if (
//                    userLat != 0.0 &&
//                    userLng != 0.0
//                ) {
//
//                    /*
//                     * Atualiza SOMENTE o marcador.
//                     *
//                     * Não fazemos setCenter() aqui.
//                     *
//                     * Assim o usuário pode mover o mapa
//                     * livremente sem a câmera voltar
//                     * automaticamente para ele.
//                     */
//                    userMarkerState.value?.let { marker ->
//
//                        marker.position =
//                            GeoPoint(
//                                userLat,
//                                userLng
//                            )
//                    }
//
//                    mapView.invalidate()
//                }
//            },
//
//            modifier = Modifier.fillMaxSize()
//        )
//
//        /*
//         * Loader enquanto os tiles carregam.
//         */
//        AnimatedVisibility(
//            visible = isMapTilesLoading,
//            enter = fadeIn(),
//            exit = fadeOut(),
//            modifier = Modifier.fillMaxSize()
//        ) {
//
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(
//                        androidx.compose.ui.graphics.Color
//                            .White
//                            .copy(alpha = 0.7f)
//                    ),
//                contentAlignment =
//                Alignment.Center
//            ) {
//
//                CircularProgressIndicator(
//                    color = GreenPrimary
//                )
//            }
//        }
//    }
//}
//
//private suspend fun loadBitmapFromPathOrUri(
//    context: Context,
//    path: String?
//): Bitmap? {
//
//    if (path.isNullOrBlank()) {
//        return null
//    }
//
//    return withContext(Dispatchers.IO) {
//
//        try {
//
//            val imageLoader =
//                ImageLoader(context)
//
//            val request =
//                ImageRequest.Builder(context)
//                    .data(
//                        if (
//                            path.startsWith("http") ||
//                            path.startsWith("content:")
//                        ) {
//                            path
//                        } else {
//                            File(path)
//                        }
//                    )
//                    .allowHardware(false)
//                    .build()
//
//            val result =
//                imageLoader.execute(request)
//
//            if (
//                result is SuccessResult
//            ) {
//
//                (
//                        result.drawable
//                                as? BitmapDrawable
//                        )?.bitmap
//
//            } else {
//
//                null
//            }
//
//        } catch (e: Exception) {
//
//            null
//        }
//    }
//}
//
//private fun createCircularMarkerBitmap(
//    sourceBitmap: Bitmap?,
//    borderColor: Int,
//    sizePx: Int = 120
//): Bitmap {
//
//    val pinHeight =
//        sizePx +
//                (sizePx * 0.25f).toInt()
//
//    val output =
//        Bitmap.createBitmap(
//            sizePx,
//            pinHeight,
//            Bitmap.Config.ARGB_8888
//        )
//
//    val canvas =
//        Canvas(output)
//
//    val radius =
//        sizePx / 2f
//
//    val borderWidth = 6f
//
//    /*
//     * Ponteira do marcador.
//     */
//    val pointerPath =
//        Path().apply {
//
//            moveTo(
//                radius - 18f,
//                sizePx - 8f
//            )
//
//            lineTo(
//                radius,
//                pinHeight.toFloat()
//            )
//
//            lineTo(
//                radius + 18f,
//                sizePx - 8f
//            )
//
//            close()
//        }
//
//    val pointerPaint =
//        Paint(Paint.ANTI_ALIAS_FLAG).apply {
//
//            color = borderColor
//
//            style =
//                Paint.Style.FILL
//        }
//
//    canvas.drawPath(
//        pointerPath,
//        pointerPaint
//    )
//
//    /*
//     * Borda circular.
//     */
//    val borderPaint =
//        Paint(Paint.ANTI_ALIAS_FLAG).apply {
//
//            color = borderColor
//
//            style =
//                Paint.Style.FILL
//        }
//
//    canvas.drawCircle(
//        radius,
//        radius,
//        radius,
//        borderPaint
//    )
//
//    val contentRadius =
//        radius - borderWidth
//
//    val imagePaint =
//        Paint(Paint.ANTI_ALIAS_FLAG)
//
//    if (sourceBitmap != null) {
//
//        val srcRect =
//            Rect(
//                0,
//                0,
//                sourceBitmap.width,
//                sourceBitmap.height
//            )
//
//        val destRect =
//            RectF(
//                borderWidth,
//                borderWidth,
//                sizePx - borderWidth,
//                sizePx - borderWidth
//            )
//
//        val layer =
//            canvas.saveLayer(
//                0f,
//                0f,
//                sizePx.toFloat(),
//                sizePx.toFloat(),
//                null
//            )
//
//        canvas.drawCircle(
//            radius,
//            radius,
//            contentRadius,
//            imagePaint
//        )
//
//        imagePaint.xfermode =
//            PorterDuffXfermode(
//                PorterDuff.Mode.SRC_IN
//            )
//
//        canvas.drawBitmap(
//            sourceBitmap,
//            srcRect,
//            destRect,
//            imagePaint
//        )
//
//        imagePaint.xfermode = null
//
//        canvas.restoreToCount(layer)
//
//    } else {
//
//        val bgPaint =
//            Paint(Paint.ANTI_ALIAS_FLAG).apply {
//
//                color = Color.WHITE
//
//                style =
//                    Paint.Style.FILL
//            }
//
//        canvas.drawCircle(
//            radius,
//            radius,
//            contentRadius,
//            bgPaint
//        )
//    }
//
//    return output
//}

package com.example.konekta_mz_app.ui.screens.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
//import androidx.compose.ui.graphics.Color
//import android.graphics.Color
import androidx.compose.ui.graphics.Color
import android.graphics.Color as AndroidColor
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.konekta_mz_app.data.local.entity.JobOffer
import com.example.konekta_mz_app.util.LocationHelper
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.MapTileProviderBase
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import java.io.File
//private val GreenPrimary = Color(0xFF00A843)
private val GreenDark = Color(0xFF007A30)
private val BackgroundLight = Color(0xFFFFFFFF)
private val SearchBgColor = Color(0xFFF2F4F7)
private val TextDark = Color(0xFF1D2939)
private val TextMuted = Color(0xFF667085)
private val GreenPrimary = Color(0xFF00A843)

@Composable
fun MapScreen(
    viewModel: MapViewModel,
    currentUserPhotoPath: String? = null,
    onOfferClick: (Long) -> Unit,
    onOfferRoute: (JobOffer) -> Unit
) {
    val state by viewModel.state.collectAsState()

    val context = LocalContext.current

    val locationHelper = remember {
        LocationHelper(context)
    }

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }


    var selectedOffer by remember {
        mutableStateOf<JobOffer?>(null)
    }


    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { granted ->
            hasPermission = granted
        }


    DisposableEffect(hasPermission) {

        if (hasPermission) {

            locationHelper.startLocationUpdates { lat, lng ->

                viewModel.updateLocation(
                    lat,
                    lng
                )
            }
        }

        onDispose {
            locationHelper.stopLocationUpdates()
        }
    }


    LaunchedEffect(Unit) {

        if (!hasPermission) {

            permissionLauncher.launch(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }


    var mapViewReference by remember {
        mutableStateOf<MapView?>(null)
    }


    DisposableEffect(Unit) {

        onDispose {
            locationHelper.stopLocationUpdates()


            viewModel.clearRoute()


            mapViewReference = null
            selectedOffer = null
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        if (state.isLoading) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                CircularProgressIndicator(
                    color = GreenPrimary
                )
            }

        } else {

            OsmMapView(
                context = context,
                offers = state.offers,
                userLat = state.userLatitude,
                userLng = state.userLongitude,
                userPhotoPath = currentUserPhotoPath,
                zoomLevel =
                if (state.zoomLevel == 0.0) {
                    17.0
                } else {
                    state.zoomLevel
                },

                route = state.route,

                onMapViewCreated = { mapView ->
                    mapViewReference = mapView
                },

                onOfferClick = { offer ->
                    selectedOffer = offer
                }
            )
        }


        FloatingActionButton(
            onClick = {

                if (!hasPermission) {

                    permissionLauncher.launch(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )

                } else {

                    val lat = state.userLatitude
                    val lng = state.userLongitude

                    if (lat != 0.0 && lng != 0.0) {

                        mapViewReference
                            ?.controller
                            ?.animateTo(
                                GeoPoint(
                                    lat,
                                    lng
                                )
                            )
                    }
                }
            },
            containerColor = Color.White,
            contentColor = GreenPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    bottom = 24.dp,
                    end = 16.dp
                )
        ) {

            Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = "Minha Localização"
            )
        }


        AnimatedVisibility(
            visible = state.isRouteLoading,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {

            Box(
                modifier = Modifier
                    .background(
                        color = Color.White,
                        shape =
                        RoundedCornerShape(12.dp)
                    )
                    .padding(20.dp)
            ) {

                Row(
                    verticalAlignment =
                    Alignment.CenterVertically
                ) {

                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = GreenPrimary
                    )

                    Spacer(
                        modifier = Modifier.width(12.dp)
                    )

                    Text(
                        text = "A calcular rota..."
                    )
                }
            }
        }


        AnimatedVisibility(
            visible =
            state.route.isNotEmpty() &&
                    !state.isRouteLoading,

            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(
                    top = 16.dp,
                    start = 16.dp,
                    end = 16.dp
                )
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.White,
                        shape =
                        RoundedCornerShape(12.dp)
                    )
                    .padding(14.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment =
                    Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector =
                        Icons.Default.Directions,
                        contentDescription = null,
                        tint = GreenPrimary
                    )

                    Spacer(
                        modifier = Modifier.width(10.dp)
                    )

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text = "Rota encontrada"
                        )

                        val distanceKm =
                            state.routeDistanceMeters / 1000.0

                        Text(
                            text =
                            if (distanceKm >= 1.0) {

                                String.format(
                                    "%.2f km",
                                    distanceKm
                                )

                            } else {

                                String.format(
                                    "%.0f m",
                                    state.routeDistanceMeters
                                )
                            }
                        )
                    }

                    TextButton(
                        onClick = {
                            viewModel.clearRoute()
                        }
                    ) {

                        Text(
                            text = "Limpar"
                        )
                    }
                }
            }
        }


        AnimatedVisibility(
            visible = state.routeError != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(
                    bottom = 100.dp,
                    start = 16.dp,
                    end = 16.dp
                )
        ) {

            state.routeError?.let { error ->

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color.White,
                            shape =
                            RoundedCornerShape(12.dp)
                        )
                        .padding(16.dp)
                ) {

                    Text(
                        text = error
                    )
                }
            }
        }


        selectedOffer?.let { offer ->

            AlertDialog(
                onDismissRequest = {
                    selectedOffer = null
                },
                shape = RoundedCornerShape(20.dp),
                containerColor = BackgroundLight,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Work,
                        contentDescription = null,
                        tint = GreenPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                },
                title = {
                    Text(
                        text = offer.title,
                        color = TextDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                text = {
                    Text(
                        text = "O que deseja fazer com este serviço?",
                        color = TextMuted,
                        fontSize = 14.sp
                    )
                },


                confirmButton = {
                    Button(
                        onClick = {
                            val offerId = offer.id
                            selectedOffer = null
                            onOfferClick(offerId)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GreenPrimary,
                            contentColor = BackgroundLight
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Text(
                            text = "Ver detalhes",
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                },


                dismissButton = {
                    OutlinedButton(
                        onClick = {
                            selectedOffer = null
                            onOfferRoute(offer)
                        },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = TextDark
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SearchBgColor),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Directions,
                            contentDescription = null,
                            tint = TextDark,
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Text(
                            text = "Ver rota",
                            color = TextDark,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                }
            )
        }
    }
}


@Composable
fun OsmMapView(
    context: Context,
    offers: List<JobOffer>,
    userLat: Double,
    userLng: Double,
    userPhotoPath: String?,
    zoomLevel: Double,
    route: List<LatLng>,
    onMapViewCreated: (MapView) -> Unit,
    onOfferClick: (JobOffer) -> Unit
) {


    val coroutineScope = rememberCoroutineScope()
    var displayedRouteKey by remember {
        mutableStateOf<Int?>(null)
    }

    var isMapTilesLoading by remember {
        mutableStateOf(true)
    }

    val userMarkerState =
        remember {
            mutableStateOf<Marker?>(null)
        }

    val routePolylineState =
        remember {
            mutableStateOf<Polyline?>(null)
        }

    DisposableEffect(Unit) {

        Configuration.getInstance().apply {

            userAgentValue =
                "com.seuapp.map/1.0 (Android)"

            osmdroidBasePath =
                context.filesDir

            osmdroidTileCache =
                context.cacheDir
        }

        onDispose {
            // A limpeza do MapView acontece no onRelease
            // do AndroidView.
        }
    }


    val openStreetMapTileSource = remember {

        object : OnlineTileSourceBase(
            "OpenStreetMapStandard",
            0,
            19,
            256,
            ".png",
            arrayOf(
                "https://a.tile.openstreetmap.org/",
                "https://b.tile.openstreetmap.org/",
                "https://c.tile.openstreetmap.org/"
            )
        ) {

            override fun getTileURLString(
                pMapTileIndex: Long
            ): String {

                return baseUrl +
                        MapTileIndex.getZoom(
                            pMapTileIndex
                        ) +
                        "/" +
                        MapTileIndex.getX(
                            pMapTileIndex
                        ) +
                        "/" +
                        MapTileIndex.getY(
                            pMapTileIndex
                        ) +
                        mImageFilenameEnding
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        AndroidView(

            factory = { ctx ->

                MapView(ctx).apply {


                    onMapViewCreated(this)


                    setTileSource(
                        openStreetMapTileSource
                    )

                    setMultiTouchControls(true)

                    isTilesScaledToDpi = true


                    controller.setZoom(
                        zoomLevel
                    )


                    if (
                        userLat != 0.0 &&
                        userLng != 0.0
                    ) {

                        controller.setCenter(
                            GeoPoint(
                                userLat,
                                userLng
                            )
                        )
                    }


                    val tileHandler =
                        Handler(Looper.getMainLooper()) { msg ->

                            if (
                                msg.what ==
                                MapTileProviderBase.MAPTILE_SUCCESS_ID
                            ) {

                                isMapTilesLoading = false
                            }

                            true
                        }

                    tileProvider.setTileRequestCompleteHandler(
                        tileHandler
                    )


                    val marker =
                        Marker(this).apply {

                            position =
                                GeoPoint(
                                    userLat,
                                    userLng
                                )

                            setAnchor(
                                Marker.ANCHOR_CENTER,
                                Marker.ANCHOR_BOTTOM
                            )

                            title =
                                "Você está aqui"
                        }

                    userMarkerState.value =
                        marker

                    overlays.add(marker)


                    coroutineScope.launch {

                        val userBitmap =
                            loadBitmapFromPathOrUri(
                                ctx,
                                userPhotoPath
                            )

                        val customUserIcon =
                            withContext(
                                Dispatchers.Default
                            ) {

                                createCircularMarkerBitmap(
                                    sourceBitmap =
                                    userBitmap,

                                    borderColor =
                                    AndroidColor.parseColor(
                                        "#1976D2"
                                    ),

                                    sizePx = 130
                                )
                            }

                        marker.icon =
                            BitmapDrawable(
                                ctx.resources,
                                customUserIcon
                            )

                        invalidate()
                    }


                    offers.forEach { offer ->

                        if (
                            offer.latitude != 0.0 &&
                            offer.longitude != 0.0
                        ) {

                            val offerMarker =
                                Marker(this).apply {

                                    position =
                                        GeoPoint(
                                            offer.latitude,
                                            offer.longitude
                                        )

                                    setAnchor(
                                        Marker.ANCHOR_CENTER,
                                        Marker.ANCHOR_BOTTOM
                                    )

                                    title =
                                        offer.title

                                    snippet =
                                        "${offer.employerName}\n" +
                                                offer.location

                                    setOnMarkerClickListener {
                                            _, _ ->

                                        onOfferClick(
                                            offer
                                        )

                                        true
                                    }
                                }

                            overlays.add(
                                offerMarker
                            )


                            coroutineScope.launch {

                                val offerBitmap =
                                    loadBitmapFromPathOrUri(
                                        ctx,
                                        offer.imagePath
                                    )

                                val customOfferIcon =
                                    withContext(
                                        Dispatchers.Default
                                    ) {

                                        createCircularMarkerBitmap(
                                            sourceBitmap =
                                            offerBitmap,

                                            borderColor =
                                            AndroidColor.parseColor(
                                                "#388E3C"
                                            ),

                                            sizePx = 120
                                        )
                                    }

                                offerMarker.icon =
                                    BitmapDrawable(
                                        ctx.resources,
                                        customOfferIcon
                                    )

                                invalidate()
                            }
                        }
                    }
                }
            },


            update = { mapView ->

                if (
                    userLat != 0.0 &&
                    userLng != 0.0
                ) {

                    userMarkerState.value?.let { marker ->

                        marker.position =
                            GeoPoint(
                                userLat,
                                userLng
                            )
                    }
                }


                val currentRouteKey =
                    if (route.isEmpty()) {

                        null

                    } else {

                        route.hashCode()
                    }


                if (route.isEmpty()) {

                    routePolylineState.value?.let {
                            oldPolyline ->

                        mapView.overlays.remove(
                            oldPolyline
                        )
                    }

                    routePolylineState.value =
                        null

                    displayedRouteKey =
                        null
                }

                else if (
                    route.size >= 2 &&
                    currentRouteKey != displayedRouteKey
                ) {


                    routePolylineState.value?.let {
                            oldPolyline ->

                        mapView.overlays.remove(
                            oldPolyline
                        )
                    }


                    val geoPoints =
                        route.map { point ->

                            GeoPoint(
                                point.latitude,
                                point.longitude
                            )
                        }

                    val polyline =
                        Polyline().apply {

                            width = 12f

                            color =
                                AndroidColor.rgb(
                                    0,
                                    168,
                                    67
                                )

                            setPoints(
                                geoPoints
                            )
                        }

                    mapView.overlays.add(
                        polyline
                    )

                    routePolylineState.value =
                        polyline

                    displayedRouteKey =
                        currentRouteKey

                    val boundingBox =
                        org.osmdroid.util
                            .BoundingBox
                            .fromGeoPoints(
                                geoPoints
                            )

                    mapView.zoomToBoundingBox(
                        boundingBox,
                        true,
                        80
                    )
                }

                mapView.invalidate()
            },


            onRelease = { mapView ->

                mapView.onPause()


                mapView.overlays.clear()


                userMarkerState.value = null
                routePolylineState.value = null
                displayedRouteKey = null

                mapView.onDetach()
            },

            modifier = Modifier.fillMaxSize()
        )


        AnimatedVisibility(
            visible = isMapTilesLoading,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = Color.White.copy(alpha = 0.7f)
                    ),
                contentAlignment =
                Alignment.Center
            ) {

                CircularProgressIndicator(
                    color = GreenPrimary
                )
            }
        }
    }
}


private suspend fun loadBitmapFromPathOrUri(
    context: Context,
    path: String?
): Bitmap? {

    if (path.isNullOrBlank()) {
        return null
    }

    return withContext(Dispatchers.IO) {

        try {

            val imageLoader =
                ImageLoader(context)

            val request =
                ImageRequest.Builder(context)
                    .data(
                        if (
                            path.startsWith("http") ||
                            path.startsWith("content:")
                        ) {

                            path

                        } else {

                            File(path)
                        }
                    )
                    .allowHardware(false)
                    .build()

            val result =
                imageLoader.execute(request)

            if (
                result is SuccessResult
            ) {

                (
                        result.drawable
                                as? BitmapDrawable
                        )?.bitmap

            } else {

                null
            }

        } catch (e: Exception) {

            null
        }
    }
}


private fun createCircularMarkerBitmap(
    sourceBitmap: Bitmap?,
    borderColor: Int,
    sizePx: Int = 120
): Bitmap {

    val pinHeight =
        sizePx +
                (sizePx * 0.25f).toInt()

    val output =
        Bitmap.createBitmap(
            sizePx,
            pinHeight,
            Bitmap.Config.ARGB_8888
        )

    val canvas =
        Canvas(output)

    val radius =
        sizePx / 2f

    val borderWidth = 6f

    /*
     * Ponteira do marcador.
     */
    val pointerPath =
        Path().apply {

            moveTo(
                radius - 18f,
                sizePx - 8f
            )

            lineTo(
                radius,
                pinHeight.toFloat()
            )

            lineTo(
                radius + 18f,
                sizePx - 8f
            )

            close()
        }

    val pointerPaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {

            color = borderColor

            style =
                Paint.Style.FILL
        }

    canvas.drawPath(
        pointerPath,
        pointerPaint
    )


    val borderPaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {

            color = borderColor

            style =
                Paint.Style.FILL
        }

    canvas.drawCircle(
        radius,
        radius,
        radius,
        borderPaint
    )

    val contentRadius =
        radius - borderWidth

    val imagePaint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    if (sourceBitmap != null) {

        val srcRect =
            Rect(
                0,
                0,
                sourceBitmap.width,
                sourceBitmap.height
            )

        val destRect =
            RectF(
                borderWidth,
                borderWidth,
                sizePx - borderWidth,
                sizePx - borderWidth
            )

        val layer =
            canvas.saveLayer(
                0f,
                0f,
                sizePx.toFloat(),
                sizePx.toFloat(),
                null
            )

        canvas.drawCircle(
            radius,
            radius,
            contentRadius,
            imagePaint
        )

        imagePaint.xfermode =
            PorterDuffXfermode(
                PorterDuff.Mode.SRC_IN
            )

        canvas.drawBitmap(
            sourceBitmap,
            srcRect,
            destRect,
            imagePaint
        )

        imagePaint.xfermode = null

        canvas.restoreToCount(layer)

    } else {

        val bgPaint =
            Paint(Paint.ANTI_ALIAS_FLAG).apply {

                color = AndroidColor.WHITE

                style =
                    Paint.Style.FILL
            }

        canvas.drawCircle(
            radius,
            radius,
            contentRadius,
            bgPaint
        )
    }

    return output
}