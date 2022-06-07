package com.example.guiam

import android.Manifest
import android.content.ContentValues
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import com.example.guiam.databinding.ActivityMainBinding
import com.example.guiam.databinding.NavHeaderBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.navigation.NavigationBarView


enum class ProviderType {
    BASIC,
    GOOGLE
}

class MainActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener {
    companion object {
        const val LOCATION_REQUEST_CODE = 0

        lateinit var sampleData: List<Cities>
    }


    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityMainBinding
    private lateinit var menuBinding: NavHeaderBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        menuBinding = NavHeaderBinding.inflate(layoutInflater)

        createMapFragment()

        toolbar("Mi Menu")
        setUserInfo()
    }

    private fun setUserInfo() {
        val bundle : Bundle? = intent.extras
        val email : String? = bundle?.getString("email")
        val provider: String? = bundle?.getString("provider")


        val prefs: SharedPreferences.Editor? = getSharedPreferences(getString(R.string.prefs_file), MODE_PRIVATE).edit()
        prefs?.putString("email", email)
        prefs?.putString("provider", provider)
        prefs?.apply()

        menuBinding.tvEmail.text = email
        menuBinding.tvNombre.text = provider

        binding.bottomNavigation.selectedItemId = R.id.nav_map

  /*      binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_search -> {
                    ocultarTodo()
                    binding.vlSearch.isVisible = true
                    createPolylines()
                }
                R.id.nav_profile -> {
                    ocultarTodo()
                    Toast.makeText(this, "Home Item reselected", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_list -> {
                    ocultarTodo()
                    binding.drawerLayout.openDrawer(GravityCompat.START)
                }
                R.id.nav_discover -> {

                }
            }
        }*/

        binding.bottomNavigation.setOnItemSelectedListener(NavigationBarView.OnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_search -> {
                    ocultarTodo()
                    binding.vlSearch.isVisible = true
                    createPolylines()


                }
                R.id.nav_profile -> {
                    ocultarTodo()
                    Toast.makeText(this, "Home Item reselected", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_list -> {
                    ocultarTodo()
//                    binding.drawerLayout.openDrawer(GravityCompat.START)
                }
                R.id.nav_discover -> {
                    ocultarTodo()
                }
                R.id.nav_map -> {
                    ocultarTodo()
                }
            }
            false
        })

    }


    private fun ocultarTodo() {
        binding.vlSearch.isVisible = false
    }

    private fun toolbar(title: String) {
        setSupportActionBar(binding.toolbar)
        val ab = supportActionBar
        if(ab != null) {
            ab.setHomeAsUpIndicator(R.drawable.ic_menu)
            ab.setDisplayHomeAsUpEnabled(true)
            ab.setLogo(R.drawable.logo2)
            ab.title = title
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                binding.drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        //map.uiSettings.isRotateGesturesEnabled = false
        //Hacer o no Zoom
        map.uiSettings.isZoomControlsEnabled = false
        map.cameraPosition.bearing

        //createPolylines()
        //createMarker()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener {
            if (it != null) {
                map.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 10f),
                    4000,
                    null
                )
            } else {
                Log.d("LOG_TAG", "No se pudo obtener la ubicación")
            }
        }

        map.setOnMyLocationButtonClickListener(this)
        enableLocation()

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            val success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this, R.raw.map_style
                )
            )
            if (!success) {
                Log.e(ContentValues.TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(ContentValues.TAG, "Can't find style. Error: ", e)
        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        TODO("Not yet implemented")
    }

    private fun createMapFragment() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    //PERMISOS
    private fun enableLocation() {
        if (!::map.isInitialized) return
        if (isPermissionsGranted()) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            map.isMyLocationEnabled = true
        } else {
            requestLocationPermission()
        }
    }

    private fun isPermissionsGranted() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(this, "Ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE)
        }
    }

    private fun createPolylines(){

        val corsString = "-0.403360992669946,39.415985107422 -0.405613005161285,39.4128799438477 -0.406691014766693,39.4157524108887 -0.416732996702194,39.4139823913575 -0.420953989028874,39.4194984436036 -0.42077100276947,39.4241256713868 -0.425523012876511,39.4259872436523 -0.426028996706009,39.4305572509766 -0.41967698931694,39.431453704834 -0.420830994844437,39.4351348876953 -0.415152996778488,39.4360694885254 -0.402814000844955,39.4369239807129 -0.401867985725289,39.4280319213867 -0.401899993419647,39.4268684387208 -0.403596013784409,39.4190063476564 -0.403360992669946,39.415985107422"
        val corsPairs = corsString.split(" ")

        //val polylinePaiporta = PolylineOptions()
        val polygonOptions = PolygonOptions()

       // val layer = KmlLayer(map, R.raw.esp, getApplicationContext())
        //layer.addLayerToMap()

        for (cors in corsPairs) {
            val cor = cors.split(",")
            polygonOptions.add(LatLng(cor[1].toDouble(), cor[0].toDouble()))
        }

       // polylinePaiporta.color(0xFFFFFFFF.toInt())
       // polylinePaiporta.width(1.0F)


//        val polylineOptions = PolylineOptions()
//            .add(LatLng(39.42283,-0.41542))
//            .add(LatLng( 39.42243, -0.41542))
//            .add(LatLng( 39.42243, -0.41502))
//            .add(LatLng( 39.42283, -0.41502))
//        val polyline = map.addPolyline(polylinePaiporta)
//        polyline.
        val polygon = map.addPolygon( polygonOptions )
        polygon.fillColor = 0xFFae3e97.toInt()
        polygon.strokeWidth = 1.0F
        polygon.strokeColor = 0xFF000000.toInt()
    }

}