package com.app.rishi.yelp.Fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.rishi.yelp.Adapter.ReviewAdapter

import com.app.rishi.yelp.Main.RestaurantDetailActivity

import com.app.rishi.yelp.Model.YelpReview
import com.app.rishi.yelp.Model.YelpReviews
import com.app.rishi.yelp.Model.YelpService
import com.app.rishi.yelp.R
import com.app.rishi.yelp.Util.Constant
import kotlinx.android.synthetic.main.fragment_restaurant_reviews.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ReviewsFragment : Fragment() {

    lateinit var  activity : RestaurantDetailActivity
    val reviews = mutableListOf<YelpReview>()
    lateinit var rvReviews: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_restaurant_reviews, container, false)
        rvReviews = rootView.findViewById(R.id.rvReviews) as RecyclerView
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getReview()
    }

    private fun getReview(){

        val retrofit =
            Retrofit.Builder().baseUrl(Constant.BASE_URL).addConverterFactory(GsonConverterFactory.create())
                .build()
        val yelpService = retrofit.create(YelpService::class.java)
        yelpService.getReviews("Bearer ${Constant.API_KEY}", activity.id)
            .enqueue(object : Callback<YelpReviews> {
                override fun onResponse(
                    call: Call<YelpReviews>,
                    response: Response<YelpReviews>
                ) {
                    Log.i("TAG", "onResponse $response")
                    val body = response.body()
                    if (body == null) {
                        Log.w("TAG", "Did not receive valid response body from Yelp API... exiting")
                        return
                    }
                    bindReviews()
                    reviews.addAll(body.reviews)
                    //pagerAdapter.notifyDataSetChanged()
                }

                override fun onFailure(call: Call<YelpReviews>, t: Throwable) {
                    Log.i("TAG", "onFailure $t")
                }
            })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as RestaurantDetailActivity
    }

    fun bindReviews() {

        rvReviews.adapter = ReviewAdapter(activity, reviews)
        rvReviews.layoutManager = LinearLayoutManager(activity)
        rvReviews.addItemDecoration(
            DividerItemDecoration(
                rvReviews.context,
                DividerItemDecoration.VERTICAL
            )
        )
    }
}