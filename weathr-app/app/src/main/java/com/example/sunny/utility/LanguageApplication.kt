package com.example.sunny.utility

import android.app.Application
import com.example.sunny.R
import com.google.android.libraries.places.api.Places
import com.yariksoffice.lingver.Lingver

class LanguageApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.google_maps_key))
        }

        Lingver.init(this)
    }

}
