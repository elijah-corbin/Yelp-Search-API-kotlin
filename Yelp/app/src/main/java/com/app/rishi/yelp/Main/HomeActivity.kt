package com.app.rishi.yelp.Main;

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.rishi.yelp.Adapter.KeywordAdapter
import com.app.rishi.yelp.Adapter.RestaurantsAdapter
import com.app.rishi.yelp.Model.*
import com.app.rishi.yelp.R
import com.app.rishi.yelp.Util.Constant
import kotlinx.android.synthetic.main.activity_home.*


import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Flow

const val RESTAURANT_ID = "RESTAURANT_ID"
const val RESTAURANT_NAME = "RESTAURANT_NAME"
const val RESTAURANT_LAT = "RESTAURANT_LAT"
const val RESTAURANT_LON = "RESTAURANT_LON"

class HomeActivity : AppCompatActivity (),LocationListener{

    private lateinit var yelpService : YelpService
    private lateinit var retrofit : Retrofit
    private val BASE_URL = "https://api.yelp.com/v3/"
    private val API_KEY = "lR-eskg2rr8aNV_HMW5N3v8XSSME8IwuPSAPQtGklX-jx4eNkx36OxdOdOiSxbCNrzDv" +
            "HMnXam73Hfqhizx5bwLoL-j1pIoiAW5wrZAr9m1CQfY2Ngxv33JYB4SiX3Yx"
    private lateinit var terms:MutableList<YelpTerm>

    private lateinit var btnSubmit: Button
    private lateinit var btnClear: Button

    private lateinit var edtLocation:EditText
    private lateinit var edtDistance:EditText
    private lateinit var ckLocation:CheckBox

    private lateinit var autoKeyword:AutoCompleteTextView
    private lateinit var arrayAdapter: KeywordAdapter
    private lateinit var keywords :MutableList<String>

    private lateinit var locationManager: LocationManager
    private val PERMISSION_REQUEST_ACCESS_FINE_LOCATION = 2

    private lateinit var adapter : RestaurantsAdapter
    private lateinit var restaurants : MutableList<YelpRestaurant>

    private var termIn:String = ""
    private  var categoryIn:String = "default"
    private  var distanceInMi:String= ""
    private var locationIn:String = ""
    var lat: Float = 0.0f
    var lon: Float = 0.0f

    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        terms = mutableListOf()
        keywords = mutableListOf()
        restaurants = mutableListOf<YelpRestaurant>()
        val cateories = resources.getStringArray(R.array.categories)

        val spinner = findViewById<Spinner>(R.id.spinner)

        if (spinner != null){

            val adapter = ArrayAdapter(this@HomeActivity, R.layout.auto_complete_item,cateories)
            spinner.adapter = adapter
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                    categoryIn = cateories[p2]
                    //Toast.makeText(this@HomeActivity, cateories[p2],Toast.LENGTH_LONG).show()
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }
            }
        }
        retrofit =
            Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
                .build()
        yelpService = retrofit.create(YelpService::class.java)
        autoKeyword = findViewById(R.id.autocompleteKeyword) as AutoCompleteTextView
        autoKeyword.threshold =1

        autoKeyword.doOnTextChanged { text, start, before, count ->

            val searchWord:String
            try {

                searchWord = text.toString();
                getKeywords(searchWord)

            }catch ( e:Exception){

            }

        }

        getLocation()

        rvRestaurants.layoutManager = LinearLayoutManager(this)
        adapter = RestaurantsAdapter(this, restaurants, object : RestaurantsAdapter.OnClickListener {
            override fun onItemClick(position: Int) {
                Log.i("TAG", "onItemClick $position")
                // When the user taps on a view in RV, navigate to new activity

                lat = restaurants[position].coordinate.lat
                lon = restaurants[position].coordinate.lon

                val intent = Intent(this@HomeActivity, RestaurantDetailActivity::class.java)
                intent.putExtra(RESTAURANT_ID, restaurants[position].id)
                intent.putExtra(RESTAURANT_NAME, restaurants[position].name);
                intent.putExtra(RESTAURANT_LAT, lat)
                intent.putExtra(RESTAURANT_LON, lon)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        })
        rvRestaurants.adapter = adapter
        rvRestaurants.addItemDecoration(
            DividerItemDecoration(
                rvRestaurants.context,
                DividerItemDecoration.VERTICAL
            )
        )


        autoKeyword.setOnItemClickListener{ parent, _, position, _ ->
            val keyWord = parent.getItemAtPosition(position) as YelpTerm
            autoKeyword.setText(keyWord.text)
            termIn = keyWord.text
        }
        btnSubmit = findViewById(R.id.btnSubmit) as Button

        edtDistance = findViewById(R.id.edtDistance) as EditText

        edtLocation = findViewById(R.id.edtLocation) as EditText
        ckLocation = findViewById(R.id.ckLocation) as CheckBox

        ckLocation.setOnClickListener(View.OnClickListener {

            if (ckLocation.isChecked){
                edtLocation.visibility = View.GONE
            }else{

                edtLocation.visibility = View.VISIBLE
            }

        })


        val latIn = "%2f".format(lat)
        val lonIn = "%2f".format(lon)

        btnSubmit.setOnClickListener(View.OnClickListener {

            if (autoKeyword.text.isEmpty()){
                autoKeyword.setError(getString(R.string.required))
            }
            if (edtLocation.text.isEmpty()){
                edtLocation.setError(getString(R.string.loc_err))
            }
            if (!edtDistance.text.isEmpty()){
                var mi = edtDistance.text.toString().toInt() *1854
                if (mi > 40000){
                    edtDistance.setError(getString(R.string.dis_err))
                }else{
                    distanceInMi = mi.toString()
                }

            }else{
                distanceInMi = ""
            }


            if (ckLocation.isChecked){
                //Toast.makeText(this@HomeActivity, lat.toString() ,Toast.LENGTH_LONG).show()

                if (locationIn.isEmpty()){
                    Toast(this@HomeActivity).showCustomToast("Location Not Found", this@HomeActivity)
                }
                businessSearch(termIn,categoryIn,distanceInMi,locationIn,"","")
            }else{
                locationIn = edtLocation.text.toString()
                businessSearch(termIn,categoryIn,distanceInMi,locationIn,"","")
            }

        })

        btnClear = findViewById(R.id.btnClear) as Button
        btnClear.setOnClickListener(View.OnClickListener {

            edtLocation.setText("");
            edtDistance.setText("")
            autoKeyword.setText("")
            spinner.setSelection(0)
            restaurants.clear()
            adapter.notifyDataSetChanged()
            keywords.clear()

        })

        //businessSearch("Avocado Toast","default","20000","New York","","")

    }

    fun Toast.showCustomToast(message: String, activity: Activity)
    {
        val layout = activity.layoutInflater.inflate (
            R.layout.custom_toast_layout,
            activity.findViewById(R.id.toast_container)
        )

        // set the text of the TextView of the message
        val textView = layout.findViewById<TextView>(R.id.toText)
        textView.text = message

        // use the application extension function
        this.apply {
            setGravity(Gravity.BOTTOM, 0, 40)
            duration = Toast.LENGTH_LONG
            view = layout
            show()
        }
    }

    private fun getAddress(lat: Double, lng: Double): String {
        val geocoder = Geocoder(this)
        try {

            val list = geocoder.getFromLocation(lat, lng, 1)

            if (list[0].adminArea.isEmpty()){

            }
            return list[0].adminArea
        }catch (e :Exception){

        }


        return ""
    }

    fun getLocation() {

        var locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_ACCESS_FINE_LOCATION)
            return
        }
        locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_ACCESS_FINE_LOCATION) {
            when (grantResults[0]) {
                PackageManager.PERMISSION_GRANTED -> getLocation()
                //PackageManager.PERMISSION_DENIED -> //Tell to user the need of grant permission
            }
        }
    }

    private fun businessSearch(term:String, category:String,distance:String,location:String,lat:String, lon:String) {
        yelpService.searchRestaurants("Bearer ${Constant.API_KEY}",
            location,
            term,
            category,
            distance,
            lat,
            lon)
            .enqueue(object : Callback<YelpSearchResult> {
                override fun onResponse(
                    call: Call<YelpSearchResult>,
                    response: Response<YelpSearchResult>
                ) {
                    Log.i("TAG", "onResponse $response")
                    val body = response.body()
                    if (body == null) {
                        Log.w("TAG", "Did not receive valid response body from Yelp API... exiting")
                        return
                    }
                    restaurants.clear()
                    restaurants.addAll(body.restaurants)
                    Log.i("TAG", "$restaurants")

                    adapter.notifyDataSetChanged()
                }

                override fun onFailure(call: Call<YelpSearchResult>, t: Throwable) {
                    Log.i("TAG", "onFailure $t")

                    Toast(this@HomeActivity).showCustomToast("No Results", this@HomeActivity)
                }
            })
    }

    private fun getKeywords(keyword: String){

        yelpService.getAutocomplete("Bearer ${Constant.API_KEY}",keyword )
            .enqueue(object : Callback<YelpAutoCompleteResult> {
                override fun onResponse(
                    call: Call<YelpAutoCompleteResult>,
                    response: Response<YelpAutoCompleteResult>
                ){

                    val body = response.body()

                    Log.d("Keyword response ===>", body.toString())
                   if (body == null){
                       return
                   }
                    terms.clear()
                    terms.addAll(body.terms)

                   for (term in terms){
                      // keywords(term.text);
                       keywords.add(term.text);
                   }

                   arrayAdapter =  KeywordAdapter(this@HomeActivity,R.layout.auto_complete_item,terms)
                    autoKeyword.setAdapter(arrayAdapter)

                }
               override fun onFailure(call: Call<YelpAutoCompleteResult>, t: Throwable) {
                    Log.i("TAG", "onFailure $t")


                }
            })
    }

    override fun onLocationChanged(location: Location) {

        lat = location.latitude.toFloat()
        lon = location.longitude.toFloat()
        //locationIn = getAddress(43.3547, -70.547)

        if (lat != 0.0f || lon != 0.0f){
            locationIn = getAddress(lat.toDouble(), lon.toDouble())
        }

        Log.d("loaction==>","address: " + locationIn + " , Longitude: " + location.longitude)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        Log.i("TAG", "onCreateOptionsMenu")
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        if (id == R.id.action_reservation) {

            // Do something
            val intent = Intent(this@HomeActivity, ReservationActivity::class.java)

            startActivity(intent)

            return true
        }
        return super.onOptionsItemSelected(item)
    }
}