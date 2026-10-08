//package com.example.konekta_mz_app.viewmodel
//
//import android.app.Application
//import android.util.Log
//import androidx.lifecycle.AndroidViewModel
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import androidx.lifecycle.viewModelScope
//import com.example.konekta_mz_app.data.local.entity.JobOffer
//import com.example.konekta_mz_app.data.repository.JobRepository
//import com.example.tourmaps.data.mock.Api
//import com.google.android.gms.maps.model.LatLng
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.launch
//import org.json.JSONObject
//import java.io.BufferedReader
//import kotlin.math.asin
//import kotlin.math.cos
//import kotlin.math.pow
//import kotlin.math.sin
//import kotlin.math.sqrt
//
//data class MapState(
//    val offers: List<JobOffer> = emptyList(),
//    val selectedOffer: JobOffer? = null,
//    val isLoading: Boolean = true,
//    val userLatitude: Double = -25.9692, // Maputo default
//    val userLongitude: Double = 32.5732,
//    val zoomLevel: Double = 19.0
//)
//
//class MapViewModel(private val jobRepository: JobRepository) : ViewModel() {
////class MapViewModel(
////    application: Application,
////    private val jobRepository: JobRepository
////) : AndroidViewModel(application) {
//
//    private val _state = MutableStateFlow(MapState())
//    val state: StateFlow<MapState> = _state.asStateFlow()
//
//    init {
//        loadOffers()
//    }
//
//    private fun loadOffers() {
//        viewModelScope.launch {
//            _state.value = _state.value.copy(isLoading = true)
//            jobRepository.getAllActiveOffers().collect { offers ->
//                _state.value = _state.value.copy(offers = offers, isLoading = false)
//            }
//        }
//    }
//
//    fun selectOffer(offer: JobOffer) {
//        _state.value = _state.value.copy(selectedOffer = offer)
//    }
//
//    fun updateLocation(latitude: Double, longitude: Double) {
//        _state.value = _state.value.copy(userLatitude = latitude, userLongitude = longitude)
//    }
//
//    class Factory(private val jobRepository: JobRepository) : ViewModelProvider.Factory {
//        @Suppress("UNCHECKED_CAST")
//        override fun <T : ViewModel> create(modelClass: Class<T>): T {
//            return MapViewModel(jobRepository) as T
//        }
//    }
////class Factory(
////    private val application: Application,
////    private val jobRepository: JobRepository
////) : ViewModelProvider.Factory {
////    @Suppress("UNCHECKED_CAST")
////    override fun <T : ViewModel> create(modelClass: Class<T>): T {
////        return MapViewModel(application, jobRepository) as T
////    }
////}
//
////    private val _caminhoFinal = MutableStateFlow<List<LatLng>>(emptyList())
////    val caminhoFinal: StateFlow<List<LatLng>> = _caminhoFinal
////
////    private val _todasRotas = MutableStateFlow<List<List<LatLng>>>(emptyList())
////    val todasRotas: StateFlow<List<List<LatLng>>> = _todasRotas
////
////    private val _isLoading = MutableStateFlow(false)
////    val isLoading: StateFlow<Boolean> = _isLoading
////
////    fun carregarEResolverRota(pontoInicial: LatLng, pontoFinal: LatLng) {
////        Log.d("fjdfjsd", "Loading")
////        Log.d("Pts", "$pontoInicial e $pontoFinal")
////        viewModelScope.launch(Dispatchers.IO) {
////            _isLoading.value = true
////
////
////            val context = getApplication<Application>().applicationContext
////            val geojsonStr = context.assets.open(Api.pathJson)
////                .bufferedReader()
////                .use(BufferedReader::readText)
////
////            val data = JSONObject(geojsonStr)
////            val features = data.getJSONArray("features")
////
////            val parsedRoutes = mutableListOf<List<LatLng>>()
////
////            for (i in 0 until features.length()) {
////                val feature = features.getJSONObject(i)
////                val coords = feature.getJSONObject("geometry").getJSONArray("coordinates")
////                val routePoints = mutableListOf<LatLng>()
////                for (j in 0 until coords.length()) {
////                    val point = coords.getJSONArray(j)
////                    routePoints.add(LatLng(point.getDouble(1), point.getDouble(0)))
////                }
////                parsedRoutes.add(routePoints)
////            }
////
////            val grafo = mutableMapOf<LatLng, MutableList<LatLng>>()
////            for (rota in parsedRoutes) {
////                for (i in 0 until rota.size - 1) {
////                    val a = rota[i]
////                    val b = rota[i + 1]
////                    grafo.getOrPut(a) { mutableListOf() }.add(b)
////                    grafo.getOrPut(b) { mutableListOf() }.add(a)
////                }
////            }
////
////            val pontoMaisProximoInicio = noMaisProximo(pontoInicial, grafo.keys)
////            val pontoMaisProximoFim = noMaisProximo(pontoFinal, grafo.keys)
////
////            val caminho = buscarCaminho(grafo, pontoMaisProximoInicio, pontoMaisProximoFim)
////
////            _todasRotas.value = parsedRoutes
////            _caminhoFinal.value = caminho
////            Log.d("Ways", caminho.toString())
////
////
////            _isLoading.value = false
////        }
////    }
////
////    private fun noMaisProximo(alvo: LatLng, nos: Collection<LatLng>): LatLng {
////        var menor = Double.MAX_VALUE
////        var maisProximo = alvo
////        for (p in nos) {
////            val d = distance(alvo, p)
////            if (d < menor) {
////                menor = d
////                maisProximo = p
////            }
////        }
////        return maisProximo
////    }
////
////    private fun distance(a: LatLng, b: LatLng): Double {
////        val R = 6371000.0
////        val lat1 = Math.toRadians(a.latitude)
////        val lat2 = Math.toRadians(b.latitude)
////        val dLat = lat2 - lat1
////        val dLon = Math.toRadians(b.longitude - a.longitude)
////        val h = sin(dLat / 2).pow(2.0) + cos(lat1) * cos(lat2) * sin(dLon / 2).pow(2.0)
////        return 2 * R * asin(sqrt(h))
////    }
////
////    private fun buscarCaminho(
////        grafo: Map<LatLng, List<LatLng>>,
////        inicio: LatLng,
////        fim: LatLng
////    ): List<LatLng> {
////        val visitados = mutableSetOf(inicio)
////        val anterior = mutableMapOf<LatLng, LatLng?>()
////        val fila = ArrayDeque<LatLng>()
////        fila.add(inicio)
////
////        while (fila.isNotEmpty()) {
////            val atual = fila.removeFirst()
////            if (atual == fim) break
////
////            for (vizinho in grafo[atual] ?: emptyList()) {
////                if (vizinho !in visitados) {
////                    visitados.add(vizinho)
////                    anterior[vizinho] = atual
////                    fila.add(vizinho)
////                }
////            }
////        }
////
////        if (!anterior.containsKey(fim)) return emptyList()
////
////        val caminho = mutableListOf<LatLng>()
////        var atual: LatLng? = fim
////        while (atual != null) {
////            caminho.add(0, atual)
////            atual = anterior[atual]
////        }
////        return caminho
////    }
//}

package com.example.konekta_mz_app.ui.screens.map

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.konekta_mz_app.data.local.entity.JobOffer
import com.example.konekta_mz_app.data.repository.JobRepository
import com.example.tourmaps.data.mock.Api
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.util.PriorityQueue
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt


data class MapState(

    val offers: List<JobOffer> = emptyList(),

    val selectedOffer: JobOffer? = null,

    val isLoading: Boolean = true,

    val userLatitude: Double = -25.9692,

    val userLongitude: Double = 32.5732,

    val zoomLevel: Double = 19.0,

    val route: List<LatLng> = emptyList(),

    val routeDistanceMeters: Double = 0.0,

    val isRouteLoading: Boolean = false,

    val routeError: String? = null
)


class MapViewModel(private val jobRepository: JobRepository) : ViewModel() {

    private val _state =
        MutableStateFlow(
            MapState()
        )

    val state: StateFlow<MapState> =
        _state.asStateFlow()


    init {
        loadOffers()
    }


    private fun loadOffers() {

        viewModelScope.launch {

            _state.value =
                _state.value.copy(
                    isLoading = true
                )

            try {

                jobRepository
                    .getAllActiveOffers()
                    .collect { offers ->

                        _state.value =
                            _state.value.copy(
                                offers = offers,
                                isLoading = false
                            )
                    }

            } catch (e: Exception) {

                Log.e(
                    "MapViewModel",
                    "Erro ao carregar ofertas",
                    e
                )

                _state.value =
                    _state.value.copy(
                        isLoading = false
                    )
            }
        }
    }


    fun selectOffer(
        offer: JobOffer
    ) {

        _state.value =
            _state.value.copy(
                selectedOffer = offer
            )
    }

    fun updateLocation(
        latitude: Double,
        longitude: Double
    ) {

        _state.value =
            _state.value.copy(
                userLatitude = latitude,
                userLongitude = longitude
            )
    }

    fun calculateRoute(
        context: Context,
        destinationLatitude: Double,
        destinationLongitude: Double
    ) {

        val currentState =
            _state.value

        if (
            currentState.userLatitude == 0.0 ||
            currentState.userLongitude == 0.0
        ) {

            _state.value =
                _state.value.copy(
                    routeError =
                    "Localização atual indisponível"
                )

            return
        }


        val start =
            LatLng(
                currentState.userLatitude,
                currentState.userLongitude
            )


        val destination =
            LatLng(
                destinationLatitude,
                destinationLongitude
            )


        viewModelScope.launch(Dispatchers.IO) {

            try {
                withContext(Dispatchers.Main) {

                    _state.value =
                        _state.value.copy(
                            isRouteLoading = true,
                            routeError = null,
                            route = emptyList(),
                            routeDistanceMeters = 0.0
                        )
                }

                val geojsonStr =
                    context.assets
                        .open(Api.pathJson)
                        .bufferedReader()
                        .use(
                            BufferedReader::readText
                        )


                val data =
                    JSONObject(
                        geojsonStr
                    )


                val features =
                    data.getJSONArray(
                        "features"
                    )

                val parsedRoutes =
                    mutableListOf<List<LatLng>>()


                for (
                i in 0 until features.length()
                ) {

                    val feature =
                        features
                            .getJSONObject(i)


                    val geometry =
                        feature
                            .getJSONObject(
                                "geometry"
                            )


                    val coords =
                        geometry
                            .getJSONArray(
                                "coordinates"
                            )


                    val routePoints =
                        mutableListOf<LatLng>()


                    for (
                    j in 0 until coords.length()
                    ) {

                        val point =
                            coords
                                .getJSONArray(j)


                        val longitude =
                            point.getDouble(0)

                        val latitude =
                            point.getDouble(1)


                        routePoints.add(
                            LatLng(
                                latitude,
                                longitude
                            )
                        )
                    }


                    if (
                        routePoints.isNotEmpty()
                    ) {

                        parsedRoutes.add(
                            routePoints
                        )
                    }
                }

                val graph =
                    mutableMapOf<
                            LatLng,
                            MutableList<LatLng>
                            >()


                for (
                route in parsedRoutes
                ) {

                    for (
                    i in 0 until route.size - 1
                    ) {

                        val a =
                            route[i]

                        val b =
                            route[i + 1]


                        graph
                            .getOrPut(a) {
                                mutableListOf()
                            }
                            .add(b)


                        graph
                            .getOrPut(b) {
                                mutableListOf()
                            }
                            .add(a)
                    }
                }


                if (graph.isEmpty()) {

                    withContext(Dispatchers.Main) {

                        _state.value =
                            _state.value.copy(
                                isRouteLoading = false,
                                routeError =
                                "Não foi possível encontrar as vias."
                            )
                    }

                    return@launch
                }


                val startNode =
                    noMaisProximo(
                        alvo = start,
                        nos = graph.keys
                    )


                val destinationNode =
                    noMaisProximo(
                        alvo = destination,
                        nos = graph.keys
                    )

                val calculatedRoute =
                    buscarRotaAStar(
                        graph = graph,
                        start = startNode,
                        goal = destinationNode
                    )

                if (calculatedRoute.isEmpty()) {

                    withContext(Dispatchers.Main) {

                        _state.value =
                            _state.value.copy(
                                isRouteLoading = false,
                                routeError =
                                "Não foi possível encontrar uma rota."
                            )
                    }

                    return@launch
                }

                val finalRoute =
                    buildList {
                        add(start)

                        calculatedRoute.forEach { point ->

                            if (lastOrNull() != point) {
                                add(point)
                            }
                        }

                        if (lastOrNull() != destination) {
                            add(destination)
                        }
                    }

                val distance =
                    calcularDistanciaRota(
                        finalRoute
                    )
                withContext(Dispatchers.Main) {

                    _state.value =
                        _state.value.copy(

                            route =
                            finalRoute,

                            routeDistanceMeters =
                            distance,

                            isRouteLoading =
                            false,

                            routeError =
                            null
                        )
                }


                Log.d(
                    "MapViewModel",
                    "Rota encontrada"
                )

                Log.d(
                    "MapViewModel",
                    "Pontos: ${calculatedRoute.size}"
                )

                Log.d(
                    "MapViewModel",
                    "Distância: $distance metros"
                )


            } catch (e: Exception) {

                Log.e(
                    "MapViewModel",
                    "Erro ao calcular rota",
                    e
                )


                withContext(Dispatchers.Main) {

                    _state.value =
                        _state.value.copy(

                            isRouteLoading =
                            false,

                            route =
                            emptyList(),

                            routeDistanceMeters =
                            0.0,

                            routeError =
                            e.message
                                ?: "Erro ao calcular rota"
                        )
                }
            }
        }
    }

    private fun noMaisProximo(
        alvo: LatLng,
        nos: Collection<LatLng>
    ): LatLng {

        var menor =
            Double.MAX_VALUE

        var maisProximo =
            alvo


        for (ponto in nos) {

            val distancia =
                distance(
                    alvo,
                    ponto
                )


            if (
                distancia < menor
            ) {

                menor =
                    distancia

                maisProximo =
                    ponto
            }
        }


        return maisProximo
    }

    private fun buscarRotaAStar(
        graph: Map<LatLng, List<LatLng>>,
        start: LatLng,
        goal: LatLng
    ): List<LatLng> {


        val openSet =
            PriorityQueue<Node>(
                compareBy {
                    it.fScore
                }
            )


        val cameFrom =
            mutableMapOf<
                    LatLng,
                    LatLng?
                    >()


        val gScore =
            mutableMapOf<
                    LatLng,
                    Double
                    >()


        val fScore =
            mutableMapOf<
                    LatLng,
                    Double
                    >()

        for (
        node in graph.keys
        ) {

            gScore[node] =
                Double.POSITIVE_INFINITY

            fScore[node] =
                Double.POSITIVE_INFINITY
        }

        gScore[start] =
            0.0


        fScore[start] =
            distance(
                start,
                goal
            )


        openSet.add(
            Node(
                point = start,
                fScore = fScore[start]!!
            )
        )


        val visited =
            mutableSetOf<LatLng>()


        while (
            openSet.isNotEmpty()
        ) {

            val current =
                openSet
                    .poll()
                    .point


            if (
                current == goal
            ) {

                return reconstruirCaminho(
                    cameFrom,
                    current
                )
            }


            if (
                !visited.add(current)
            ) {

                continue
            }


            val neighbors =
                graph[current]
                    ?: emptyList()


            for (
            neighbor in neighbors
            ) {

                val movementCost =
                    distance(
                        current,
                        neighbor
                    )


                val tentativeGScore =
                    (
                            gScore[current]
                                ?: Double.POSITIVE_INFINITY
                            ) +
                            movementCost


                if (
                    tentativeGScore <
                    (
                            gScore[neighbor]
                                ?: Double.POSITIVE_INFINITY
                            )
                ) {

                    cameFrom[neighbor] =
                        current


                    gScore[neighbor] =
                        tentativeGScore



                    val heuristic =
                        distance(
                            neighbor,
                            goal
                        )


                    val newFScore =
                        tentativeGScore +
                                heuristic


                    fScore[neighbor] =
                        newFScore


                    openSet.add(
                        Node(
                            point = neighbor,
                            fScore = newFScore
                        )
                    )
                }
            }
        }

        return emptyList()
    }

    private fun reconstruirCaminho(
        cameFrom: Map<LatLng, LatLng?>,
        current: LatLng
    ): List<LatLng> {

        val path =
            mutableListOf<LatLng>()


        var currentNode:
                LatLng? = current


        while (
            currentNode != null
        ) {

            path.add(
                currentNode
            )


            currentNode =
                cameFrom[currentNode]
        }


        path.reverse()


        return path
    }


    private fun distance(
        a: LatLng,
        b: LatLng
    ): Double {

        val earthRadius =
            6371000.0


        val lat1 =
            Math.toRadians(
                a.latitude
            )


        val lat2 =
            Math.toRadians(
                b.latitude
            )


        val deltaLat =
            Math.toRadians(
                b.latitude -
                        a.latitude
            )


        val deltaLon =
            Math.toRadians(
                b.longitude -
                        a.longitude
            )


        val h =
            sin(
                deltaLat / 2
            ).pow(2.0) +

                    cos(lat1) *
                    cos(lat2) *

                    sin(
                        deltaLon / 2
                    ).pow(2.0)


        return 2.0 *
                earthRadius *
                asin(
                    sqrt(h)
                )
    }


    private fun calcularDistanciaRota(
        route: List<LatLng>
    ): Double {

        if (
            route.size < 2
        ) {
            return 0.0
        }


        var total =
            0.0


        for (
        i in 0 until route.size - 1
        ) {

            total +=
                distance(
                    route[i],
                    route[i + 1]
                )
        }


        return total
    }


    fun clearRoute() {
        Log.d("Start clear", "Yes")
        _state.value =
            _state.value.copy(

                route =
                emptyList(),

                routeDistanceMeters =
                0.0,

                routeError =
                null
            )
    }


    private data class Node(

        val point: LatLng,

        val fScore: Double
    )


    class Factory(
        private val jobRepository: JobRepository
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            modelClass: Class<T>
        ): T {

            return MapViewModel(
                jobRepository
            ) as T
        }
    }
}
