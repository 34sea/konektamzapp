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

package com.example.konekta_mz_app.ui.screens.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.konekta_mz_app.data.local.entity.JobOffer
import com.example.konekta_mz_app.util.LocationHelper
import com.example.konekta_mz_app.viewmodel.MapViewModel
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
import java.io.File

private val TextDark = androidx.compose.ui.graphics.Color(0xFF111827)
private val GreenPrimary = androidx.compose.ui.graphics.Color(0xFF00A843)

@Composable
fun MapScreen(
    viewModel: MapViewModel,
    currentUserPhotoPath: String? = null,
    onOfferClick: (Long) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val locationHelper = remember { LocationHelper(context) }
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
    }

    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            val loc = locationHelper.getCurrentLocation()
            loc?.let { (lat, lng) ->
                viewModel.updateLocation(lat, lng)
            }
        }
    }

    // Substituído Scaffold por Box para remover paddings/insets aninhados
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Conteúdo Principal do Mapa ou Loader
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = GreenPrimary)
            }
        } else {
            OsmMapView(
                context = context,
                offers = state.offers,
                userLat = state.userLatitude,
                userLng = state.userLongitude,
                userPhotoPath = currentUserPhotoPath,
                zoomLevel = if (state.zoomLevel == 0.0) 17.0 else state.zoomLevel,
                onOfferClick = onOfferClick
            )
        }

        // Barra Superior / Top Bar Customizada (Substitui o TopAppBar do Scaffold)
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .statusBarsPadding()
//                .padding(16.dp)
//        ) {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(56.dp)
//                    .shadow(4.dp, RoundedCornerShape(16.dp))
//                    .background(
//                        color = androidx.compose.ui.graphics.Color.White,
//                        shape = RoundedCornerShape(16.dp)
//                    )
//                    .padding(horizontal = 16.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    text = "Mapa de Empregos",
//                    fontSize = 18.sp,
//                    fontWeight = FontWeight.Bold,
//                    color = TextDark
//                )
//            }
//        }

        // Botão Flutuante (FAB) para Minha Localização
        FloatingActionButton(
            onClick = {
                if (!hasPermission) {
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            },
            containerColor = androidx.compose.ui.graphics.Color.White,
            contentColor = GreenPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 24.dp, end = 16.dp)
        ) {
            Icon(Icons.Default.MyLocation, contentDescription = "Minha Localização")
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
    onOfferClick: (Long) -> Unit
) {
    val scope = rememberCoroutineScope()
    var isMapTilesLoading by remember { mutableStateOf(true) }

    DisposableEffect(Unit) {
        Configuration.getInstance().apply {
            userAgentValue = "com.seuapp.map/1.0 (Android)"
            osmdroidBasePath = context.filesDir
            osmdroidTileCache = context.cacheDir
        }
        onDispose { }
    }

    val openStreetMapTileSource = remember {
        object : OnlineTileSourceBase(
            "OpenStreetMapStandard",
            0, 19, 256, ".png",
            arrayOf(
                "https://a.tile.openstreetmap.org/",
                "https://b.tile.openstreetmap.org/",
                "https://c.tile.openstreetmap.org/"
            )
        ) {
            override fun getTileURLString(pMapTileIndex: Long): String {
                return baseUrl +
                        MapTileIndex.getZoom(pMapTileIndex) + "/" +
                        MapTileIndex.getX(pMapTileIndex) + "/" +
                        MapTileIndex.getY(pMapTileIndex) + mImageFilenameEnding
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                MapView(ctx).apply {
                    setTileSource(openStreetMapTileSource)
                    setMultiTouchControls(true)
                    isTilesScaledToDpi = true

                    controller.setZoom(zoomLevel)
                    controller.setCenter(GeoPoint(userLat, userLng))

                    // Registrar callback para monitorar o download de tiles
                    tileProvider.setTileRequestCompleteHandler(Handler(Looper.getMainLooper()) { msg ->
                        if (msg.what == MapTileProviderBase.MAPTILE_SUCCESS_ID) {
                            // Quando a primeira leva de tiles for concluída com sucesso
                            isMapTilesLoading = false
                        }
                        true
                    })

                    // Marcador do Utilizador (Com Foto de Perfil)
                    val userMarker = Marker(this).apply {
                        position = GeoPoint(userLat, userLng)
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = "Você está aqui"
                    }
                    overlays.add(userMarker)

                    scope.launch {
                        val userBitmap = loadBitmapFromPathOrUri(ctx, userPhotoPath)
                        val customUserIcon = createCircularMarkerBitmap(
                            sourceBitmap = userBitmap,
                            borderColor = Color.parseColor("#1976D2"),
                            sizePx = 130
                        )
                        userMarker.icon = BitmapDrawable(ctx.resources, customUserIcon)
                        invalidate()
                    }

                    // Marcadores das Ofertas de Emprego (Com Foto da Empresa/Vaga)
                    offers.forEach { offer ->
                        if (offer.latitude != 0.0 && offer.longitude != 0.0) {
                            val offerMarker = Marker(this).apply {
                                position = GeoPoint(offer.latitude, offer.longitude)
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                title = offer.title
                                snippet = "${offer.employerName}\n${offer.location}"
                                setOnMarkerClickListener { _, _ ->
                                    onOfferClick(offer.id)
                                    true
                                }
                            }
                            overlays.add(offerMarker)

                            scope.launch {
                                val offerBitmap = loadBitmapFromPathOrUri(ctx, offer.imagePath)
                                val customOfferIcon = createCircularMarkerBitmap(
                                    sourceBitmap = offerBitmap,
                                    borderColor = Color.parseColor("#388E3C"),
                                    sizePx = 120
                                )
                                offerMarker.icon = BitmapDrawable(ctx.resources, customOfferIcon)
                                invalidate()
                            }
                        }
                    }
                }
            },
            update = { mapView ->
                if (userLat != 0.0 && userLng != 0.0) {
                    mapView.controller.setCenter(GeoPoint(userLat, userLng))
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Overlay do Loader enquanto os tiles do mapa carregam
        AnimatedVisibility(
            visible = isMapTilesLoading,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = GreenPrimary)
            }
        }
    }
}

private suspend fun loadBitmapFromPathOrUri(context: Context, path: String?): Bitmap? {
    if (path.isNullOrBlank()) return null
    return withContext(Dispatchers.IO) {
        try {
            val imageLoader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(if (path.startsWith("http") || path.startsWith("content:")) path else File(path))
                .allowHardware(false)
                .build()

            val result = imageLoader.execute(request)
            if (result is SuccessResult) {
                (result.drawable as? BitmapDrawable)?.bitmap
            } else null
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
    val pinHeight = sizePx + (sizePx * 0.25f).toInt()
    val output = Bitmap.createBitmap(sizePx, pinHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(output)

    val radius = sizePx / 2f
    val borderWidth = 6f

    val pointerPath = Path().apply {
        moveTo(radius - 18f, sizePx - 8f)
        lineTo(radius, pinHeight.toFloat())
        lineTo(radius + 18f, sizePx - 8f)
        close()
    }
    val pointerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = borderColor
        style = Paint.Style.FILL
    }
    canvas.drawPath(pointerPath, pointerPaint)

    val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = borderColor
        style = Paint.Style.FILL
    }
    canvas.drawCircle(radius, radius, radius, borderPaint)

    val contentRadius = radius - borderWidth
    val imagePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    if (sourceBitmap != null) {
        val srcRect = Rect(0, 0, sourceBitmap.width, sourceBitmap.height)
        val destRect = RectF(
            borderWidth,
            borderWidth,
            sizePx - borderWidth,
            sizePx - borderWidth
        )

        val layer = canvas.saveLayer(0f, 0f, sizePx.toFloat(), sizePx.toFloat(), null)
        canvas.drawCircle(radius, radius, contentRadius, imagePaint)
        imagePaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(sourceBitmap, srcRect, destRect, imagePaint)
        imagePaint.xfermode = null
        canvas.restoreToCount(layer)
    } else {
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }
        canvas.drawCircle(radius, radius, contentRadius, bgPaint)
    }

    return output
}