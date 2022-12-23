package com.app.rishi.yelp.Fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.app.rishi.yelp.Main.RestaurantDetailActivity
import com.app.rishi.yelp.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment() {

    lateinit var  activity : RestaurantDetailActivity
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_restaurant_map, container, false)

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?  //use SuppoprtMapFragment for using in fragment instead of activity  MapFragment = activity   SupportMapFragment = fragment


        mapFragment!!.getMapAsync { mMap ->
            mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

            mMap.clear() //clear old markers

            val googlePlex = CameraPosition.builder()
                .target(LatLng(activity.lat.toDouble(), activity.lon.toDouble()))
                .zoom(10f)
                .bearing(0f)
                .tilt(45f)
                .build()

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(googlePlex), 10000, null)

           mMap.addMarker(
               MarkerOptions()
                    .position(LatLng(activity.lat.toDouble(), activity.lon.toDouble()))
                    .title(activity.name)

            )
//
//            mMap.addMarker(
//                MarkerOptions()
//                    .position(LatLng(37.4629101, -122.2449094))
//                    .title("Iron Man")
//                    .snippet("His Talent : Plenty of money")
//            )
//
//            mMap.addMarker(
//                MarkerOptions()
//                    .position(LatLng(37.3092293, -122.1136845))
//                    .title("Captain America")
//            )
        }
        return rootView
    }



    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as RestaurantDetailActivity
    }
}