//package com.example.konekta_mz_app.util
//
//import android.Manifest
//import android.content.Context
//import android.content.pm.PackageManager
//import android.location.Geocoder
//import android.os.Looper
//import androidx.core.content.ContextCompat
//import com.google.android.gms.location.FusedLocationProviderClient
//import com.google.android.gms.location.LocationCallback
//import com.google.android.gms.location.LocationRequest
//import com.google.android.gms.location.LocationResult
//import com.google.android.gms.location.LocationServices
//import com.google.android.gms.location.Priority
//import kotlinx.coroutines.suspendCancellableCoroutine
//import java.util.Locale
//import kotlin.coroutines.resume
//
//class LocationHelper(private val context: Context) {
//
//    private val fusedLocationClient: FusedLocationProviderClient =
//        LocationServices.getFusedLocationProviderClient(context)
//
//    fun hasLocationPermission(): Boolean {
//        return ContextCompat.checkSelfPermission(
//            context,
//            Manifest.permission.ACCESS_FINE_LOCATION
//        ) == PackageManager.PERMISSION_GRANTED
//    }
//
//    suspend fun getCurrentLocation(): Pair<Double, Double>? {
//        if (!hasLocationPermission()) return null
//
//        return suspendCancellableCoroutine { continuation ->
//            val locationRequest = LocationRequest.Builder(
//                Priority.PRIORITY_HIGH_ACCURACY,
//                10000L
//            ).apply {
//                setWaitForAccurateLocation(false)
//                setMinUpdateIntervalMillis(5000L)
//            }.build()
//
//            val locationCallback = object : LocationCallback() {
//                override fun onLocationResult(result: LocationResult) {
//                    result.lastLocation?.let { location ->
//                        fusedLocationClient.removeLocationUpdates(this)
//                        if (continuation.isActive) {
//                            continuation.resume(Pair(location.latitude, location.longitude))
//                        }
//                    }
//                }
//            }
//
//            try {
//                fusedLocationClient.requestLocationUpdates(
//                    locationRequest,
//                    locationCallback,
//                    Looper.getMainLooper()
//                )
//            } catch (e: SecurityException) {
//                if (continuation.isActive) {
//                    continuation.resume(null)
//                }
//            }
//
//            continuation.invokeOnCancellation {
//                fusedLocationClient.removeLocationUpdates(locationCallback)
//            }
//        }
//    }
//
//    fun getLocationName(latitude: Double, longitude: Double): String {
//        return try {
//            val geocoder = Geocoder(context, Locale.getDefault())
//            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
//            if (!addresses.isNullOrEmpty()) {
//                val address = addresses[0]
//                val city = address.locality ?: address.subAdminArea ?: ""
//                val country = address.countryName ?: ""
//                if (city.isNotEmpty() && country.isNotEmpty()) {
//                    "$city, $country"
//                } else if (city.isNotEmpty()) {
//                    city
//                } else if (country.isNotEmpty()) {
//                    country
//                } else {
//                    "Lat: ${"%.4f".format(latitude)}, Lng: ${"%.4f".format(longitude)}"
//                }
//            } else {
//                "Lat: ${"%.4f".format(latitude)}, Lng: ${"%.4f".format(longitude)}"
//            }
//        } catch (e: Exception) {
//            "Lat: ${"%.4f".format(latitude)}, Lng: ${"%.4f".format(longitude)}"
//        }
//    }
//}

package com.example.konekta_mz_app.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume

class LocationHelper(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private var locationCallback: LocationCallback? = null

    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }


    suspend fun getCurrentLocation(): Pair<Double, Double>? {
        if (!hasLocationPermission()) return null

        return suspendCancellableCoroutine { continuation ->

            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                10_000L
            ).apply {
                setWaitForAccurateLocation(false)
                setMinUpdateIntervalMillis(5_000L)
            }.build()

            val callback = object : LocationCallback() {

                override fun onLocationResult(result: LocationResult) {

                    result.lastLocation?.let { location ->

                        fusedLocationClient.removeLocationUpdates(this)

                        if (continuation.isActive) {
                            continuation.resume(
                                Pair(
                                    location.latitude,
                                    location.longitude
                                )
                            )
                        }
                    }
                }
            }

            try {
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    callback,
                    Looper.getMainLooper()
                )
            } catch (e: SecurityException) {

                if (continuation.isActive) {
                    continuation.resume(null)
                }
            }

            continuation.invokeOnCancellation {
                fusedLocationClient.removeLocationUpdates(callback)
            }
        }
    }


    fun startLocationUpdates(
        onLocationChanged: (latitude: Double, longitude: Double) -> Unit
    ) {
        if (!hasLocationPermission()) return

        stopLocationUpdates()

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5_000L
        ).apply {
            setWaitForAccurateLocation(false)

            setMinUpdateIntervalMillis(2_000L)

            setMinUpdateDistanceMeters(2f)
        }.build()

        locationCallback = object : LocationCallback() {

            override fun onLocationResult(result: LocationResult) {

                result.lastLocation?.let { location ->

                    onLocationChanged(
                        location.latitude,
                        location.longitude
                    )
                }
            }
        }

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback!!,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            locationCallback = null
        }
    }

    fun stopLocationUpdates() {
        locationCallback?.let { callback ->
            fusedLocationClient.removeLocationUpdates(callback)
        }

        locationCallback = null
    }

    fun getLocationName(
        latitude: Double,
        longitude: Double
    ): String {

        return try {

            val geocoder = Geocoder(
                context,
                Locale.getDefault()
            )

            val addresses = geocoder.getFromLocation(
                latitude,
                longitude,
                1
            )

            if (!addresses.isNullOrEmpty()) {

                val address = addresses[0]

                val city =
                    address.locality
                        ?: address.subAdminArea
                        ?: ""

                val country =
                    address.countryName
                        ?: ""

                when {
                    city.isNotEmpty() && country.isNotEmpty() ->
                        "$city, $country"

                    city.isNotEmpty() ->
                        city

                    country.isNotEmpty() ->
                        country

                    else ->
                        "Lat: ${"%.4f".format(latitude)}, " +
                                "Lng: ${"%.4f".format(longitude)}"
                }

            } else {

                "Lat: ${"%.4f".format(latitude)}, " +
                        "Lng: ${"%.4f".format(longitude)}"
            }

        } catch (e: Exception) {

            "Lat: ${"%.4f".format(latitude)}, " +
                    "Lng: ${"%.4f".format(longitude)}"
        }
    }
}