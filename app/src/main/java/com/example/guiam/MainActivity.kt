package com.example.guiam

import android.Manifest
import android.content.ContentValues
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.guiam.databinding.ActivityMainBinding
import com.example.guiam.databinding.NavHeaderBinding
import com.example.guiam.models.Cities
import com.github.theapache64.twyper.SwipedOutDirection
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
        lateinit var mainActivity: MainActivity
        //lateinit var room: RoomDatabase.ListDb
        const val LOCATION_REQUEST_CODE = 0

        var CitieSelected: String = ""

        var ListPlaces: MutableList<String> = mutableListOf()

        var place: String = "LISTA"

        lateinit var sampleData: List<Cities>

        //Creamos la lista a mostrar y le damos los valores
        val listaFija = mutableStateListOf<Cities>()
        val listaMostrar: List<Cities> = listaFija


        fun createPoli(cords: String) {
            mainActivity.createPolylines(cords)
        }

        fun filtrarLista(value: TextFieldValue) {
            val listaFiltrada =
                if (value.text.isEmpty() || value.text.isBlank()) {
                    sampleData
                } else {
                    val resultList = ArrayList<Cities>()
                    for (ciudades in sampleData) {
                        if (ciudades.name.lowercase().contains(value.text.lowercase())) {
                            resultList.add(ciudades)
                        }
                    }
                    resultList
                }
            listaFija.clear()
            listaFija.addAll(listaFiltrada)
        }
    }


    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityMainBinding
    private lateinit var menuBinding: NavHeaderBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var polygon: Polygon

    private var mapaStyled = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        menuBinding = NavHeaderBinding.inflate(layoutInflater)
        mainActivity = this

        createMapFragment()

        setUserInfo()

        //room = databaseBuilder(this, RoomDatabase.ListDb::class.java, "Lists")
        //    .build()

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

        binding.fab.setOnClickListener(View.OnClickListener {
            ocultarTodo()
            var mapStile: Int
            if (mapaStyled) {
                mapStile = R.raw.map_style2
                mapaStyled = false
            } else {
                mapStile = R.raw.map_style
                mapaStyled = true
            }
            map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this, mapStile
                )
            )
        })

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
                }
                R.id.nav_profile -> {
                    ocultarTodo()
                    Toast.makeText(this, "Perfil Proximamente", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_list -> {
                    ocultarTodo()
                    binding.listFragment.isVisible = true

                    val f2 = ListFragment()
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.listFragment, f2)
                    transaction.addToBackStack(null)
                    transaction.commit()

                }
                R.id.nav_discover -> {
                    ocultarTodo()
                    binding.swipeFragment.isVisible = true
                }
            }
            false
        })

    }


    public fun ocultarTodo() {
        binding.vlSearch.isVisible = false
        binding.swipeFragment.isVisible = false
        binding.listFragment.isVisible = false
    }


    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        //map.uiSettings.isRotateGesturesEnabled = false
        //Hacer o no Zoom
        map.uiSettings.isZoomControlsEnabled = false
        map.cameraPosition.bearing
        map.uiSettings.isMyLocationButtonEnabled = false

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


        polygon = map.addPolygon(PolygonOptions().add(LatLng(0.0, 0.0), LatLng(0.0, 0.0)))
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

    fun createPolylines(cords: String) {
        //Remove the previous Polygon
        polygon.remove()

        //We receive a string with all the coordinates,
        //we have to separate them first in pairs and then in Latitude and Longitude
        //we go through the list and add the vertices of the polygon
        val corsPairs = cords.split(" ")
        val polygonOptions = PolygonOptions()

        var cord: Boolean = true
        var lat: Double = 0.0
        var lon: Double = 0.0

        for (cors in corsPairs) {
            val cor = cors.split(",")
            if (cord) {
                lat = cor[1].toDouble()
                lon = cor[0].toDouble()
                cord = false
            }
            polygonOptions.add(LatLng(cor[1].toDouble(), cor[0].toDouble()))
        }

        //Move the camera to the new position
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(LatLng(lat, lon), 12f),
            4000,
            null
        )

        //We give functionality when clicking on the polygon
        map.setOnPolygonClickListener {
            Toast.makeText(this, place, Toast.LENGTH_SHORT).show()
            ocultarTodo()
            binding.swipeFragment.isVisible = true
        }

        //We add the polygon and give it style and values
        polygon = map.addPolygon( polygonOptions )
        polygon.fillColor = 0xFFae3e97.toInt()
        polygon.strokeWidth = 1.0F
        polygon.strokeColor = 0xFF000000.toInt()
        polygon.isClickable = true

    }

    fun gestionarLista(item: Char, direction: SwipedOutDirection) {
        if (direction.name == "RIGHT") {
            ListPlaces.add(item.toString())
        }
    }

}