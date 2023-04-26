package com.example.maprealtime

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.example.maprealtime.databinding.ActivityMainBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

private const val TAG = "myLocation"
class MainActivity : AppCompatActivity(),OnMapReadyCallback {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var myMap:GoogleMap? = null
    lateinit var fusedLocationProviderClient:FusedLocationProviderClient
    lateinit var locationRequest:LocationRequest
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.my_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create()

        locationRequest.interval = 500
        locationRequest.fastestInterval = 250
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallBack,Looper.getMainLooper())

    }
    var marker:Marker? = null
    val locationCallBack = object : LocationCallback(){
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)
            Log.d(TAG, "onLocationResult: ${p0.lastLocation?.latitude},${p0.lastLocation?.longitude}")
            binding.txt.text = "${p0.lastLocation?.latitude},${p0.lastLocation?.longitude}"

            if (myMap!=null){
                if (marker==null){
                    marker = myMap!!.addMarker(MarkerOptions().title("Bizi joylashuv").position(LatLng(p0.lastLocation?.latitude!!,p0.lastLocation?.longitude!!)))
                }else{
                    marker?.position = LatLng(p0.lastLocation?.latitude!!,p0.lastLocation?.longitude!!)
                }
                if (touch)
                myMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition(LatLng(p0.lastLocation?.latitude!!,p0.lastLocation?.longitude!!),18f,0f,p0.lastLocation?.bearing!!)))
            }
        }
    }

    var touch = true
    override fun onMapReady(p0: GoogleMap) {
        myMap = p0

        binding.txt.setOnClickListener { touch = true }

        myMap?.setOnCameraIdleListener {
            touch = false
            Toast.makeText(this, "ID click", Toast.LENGTH_SHORT).show()
        }
    }
}