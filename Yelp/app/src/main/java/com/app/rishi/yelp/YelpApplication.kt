package com.app.rishi.yelp

import android.app.Application
import com.app.rishi.yelp.Util.ModelPreferencesManager

class YelpApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ModelPreferencesManager.with(this)
    }
}