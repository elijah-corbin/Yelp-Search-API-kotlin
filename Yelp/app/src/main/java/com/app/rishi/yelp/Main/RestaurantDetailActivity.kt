package com.app.rishi.yelp.Main

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.tabs.TabLayout
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import com.app.rishi.yelp.Adapter.MyFragmentPagerAdapter
import com.app.rishi.yelp.Adapter.ReviewAdapter
import com.app.rishi.yelp.Adapter.SliderAdapter
import com.app.rishi.yelp.Fragment.MapFragment
import com.app.rishi.yelp.Fragment.OverviewFragment
import com.app.rishi.yelp.Fragment.ReviewsFragment
import com.app.rishi.yelp.Model.YelpRestaurantDetail
import com.app.rishi.yelp.Model.YelpReview
import com.app.rishi.yelp.Model.YelpReviews
import com.app.rishi.yelp.Model.YelpService
import com.app.rishi.yelp.R
import com.app.rishi.yelp.Util.Constant
import kotlinx.android.synthetic.main.fragment_restaurant_overview.*
import kotlinx.android.synthetic.main.fragment_restaurant_reviews.*
import kotlinx.android.synthetic.main.activity_restaurant_details.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.Flow


private const val BASE_URL = "https://api.yelp.com/v3/"

private const val TAG = "RestaurantDetailActivity"
class RestaurantDetailActivity : AppCompatActivity() {

    public lateinit var id: String
    public lateinit var name:String
    public lateinit var resUrl:String

    public var lat:Float = 0.0f
    public var lon:Float = 0.0f

    lateinit var photosAdapter: SliderAdapter
    lateinit var pagerAdapter: MyFragmentPagerAdapter
    val photos = mutableListOf<String>()
    val reviews = mutableListOf<YelpReview>()

    @ExperimentalStdlibApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_details)

        id = intent.getSerializableExtra(RESTAURANT_ID) as String
        name = intent.getSerializableExtra(RESTAURANT_NAME) as String
        lat = intent.getSerializableExtra(RESTAURANT_LAT) as Float
        lon = intent.getSerializableExtra(RESTAURANT_LON) as Float

        setTitle(name)

        val viewPager = findViewById<View>(R.id.viewpager) as ViewPager
        pagerAdapter = MyFragmentPagerAdapter(supportFragmentManager, this)
        viewPager.adapter = pagerAdapter

        pagerAdapter.addFragment(OverviewFragment(), "Business Details")
        pagerAdapter.addFragment(MapFragment(), "Map Location")
        pagerAdapter.addFragment(ReviewsFragment(), "Reviews")

        pagerAdapter.notifyDataSetChanged()

        val tabLayout = findViewById<View>(R.id.sliding_tabs) as TabLayout
        tabLayout.setupWithViewPager(viewPager)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        Log.i("TAG", "onCreateOptionsMenu")
        val inflater = menuInflater
        inflater.inflate(R.menu.detil_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        if (id == R.id.action_facebook) {

            if (!resUrl.isEmpty()){

                // Do something
                val url = Intent(Intent.ACTION_SEND)
                url.setPackage(Constant.FACEBOOK_PACKAGE_NAME)
                url.putExtra(Intent.EXTRA_TEXT, resUrl)
                url.setType("text/plain")

                try {
                    startActivity(url)
                }catch (e:Exception){
                    Toast.makeText(this@RestaurantDetailActivity, "facebook not installed", Toast.LENGTH_LONG).show()
                }
            }
            return true
        }
        if (id == R.id.action_twitter){

            if (!resUrl.isEmpty()){

                // Do something
                val url = Intent(Intent.ACTION_SEND)
                url.setPackage(Constant.TWITTER_PACKAGE_NAME)
                url.putExtra(Intent.EXTRA_TEXT, resUrl)
                url.setType("text/plain")

                try {
                    startActivity(url)
                }catch (e:Exception){
                    Toast.makeText(this@RestaurantDetailActivity, "twitter not installed", Toast.LENGTH_LONG).show()
                }
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}
