package com.raywenderlich.android.runtracker



//import android.content.Context
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
//import com.google.android.gms.maps.model.MarkerOptions
import com.raywenderlich.android.runtracker.databinding.ActivityMapsBinding



class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

  private lateinit var map: GoogleMap
  private lateinit var binding: ActivityMapsBinding

//  private  val locationProvider = LocationProvider(this)
//  private  val permissionManager = PermissionsManager(this, locationProvider)
private val presenter = MapPresenter(this)


  override fun onCreate(savedInstanceState: Bundle?) {
    setTheme(R.style.AppTheme)

    super.onCreate(savedInstanceState)

    binding = ActivityMapsBinding.inflate(layoutInflater)
    setContentView(binding.root)

    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
    val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
    mapFragment.getMapAsync(this)

    binding.btnStartStop.setOnClickListener {
      if (binding.btnStartStop.text == getString(R.string.start_label)) {
        startTracking()
        binding.btnStartStop.setText(R.string.stop_label)
      } else {
        stopTracking()
        binding.btnStartStop.setText(R.string.start_label)
      }
    }

    presenter.onViewCreated()

  }



  /**
   * Manipulates the map once available.
   * This callback is triggered when the map is ready to be used.
   * This is where we can add markers or lines, add listeners or move the camera. In this case,
   * we just add a marker near Sydney, Australia.
   * If Google Play services is not installed on the device, the user will be prompted to install
   * it inside the SupportMapFragment. This method will only be triggered once the user has
   * installed Google Play services and returned to the app.
   */
  override fun onMapReady(googleMap: GoogleMap) {
    map = googleMap

    //1
    presenter.ui.observe(this) { ui ->
      updateUi(ui)
    }

    //2
    presenter.onMapLoaded()
    map.uiSettings.isZoomControlsEnabled = true


  }

  private fun startTracking() {
    //1
    binding.container.txtPace.text = ""
    binding.container.txtDistance.text = ""
    //2
    binding.container.txtTime.base = SystemClock.elapsedRealtime()
    binding.container.txtTime.start()
    //3
    map.clear()

    //4
    presenter.startTracking()
  }

  private fun stopTracking() {
    presenter.stopTracking()
    binding.container.txtTime.stop()
  }

  @SuppressLint("MissingPermission")
  private fun updateUi(ui: Ui) {
    //1
    if (ui.currentLocation != null && ui.currentLocation != map.cameraPosition.target) {
      map.isMyLocationEnabled = true
      map.animateCamera(CameraUpdateFactory.newLatLngZoom(ui.currentLocation, 14f))
    }
    //2
    binding.container.txtDistance.text = ui.formattedDistance
    binding.container.txtPace.text = ui.formattedPace
    //3
    drawRoute(ui.userPath)
  }

  private fun drawRoute(locations: List<LatLng>) {
    val polylineOptions = PolylineOptions()

    map.clear()

    val points = polylineOptions.points
    points.addAll(locations)

    map.addPolyline(polylineOptions)
  }

}
