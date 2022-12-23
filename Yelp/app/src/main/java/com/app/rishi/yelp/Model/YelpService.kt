package com.app.rishi.yelp.Model

import retrofit2.Call
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface YelpService {

    @GET("autocomplete")
    fun getAutocomplete(
        @Header("Authorization") authHeader: String,
        @Query("text") searchTerm: String): Call<YelpAutoCompleteResult>

    @GET("businesses/search")
    fun searchRestaurants(
        @Header("Authorization") authHeader: String,
        @Query("location") location: String,
        @Query("term") searchTerm: String,
        @Query("categories") category:String,
        @Query("radius") radius:String,
        @Query("latitude") lat:String,
        @Query("longitude") lon:String
        ): Call<YelpSearchResult>

    @GET("businesses/{id}")
    fun getDetails(
        @Header("Authorization") authHeader: String,
        @Path("id") id: String): Call<YelpRestaurantDetail>

    @GET("businesses/{id}/reviews")
    fun getReviews(
        @Header("Authorization") authHeader: String,
        @Path("id") id: String): Call<YelpReviews>
}