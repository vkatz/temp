package com.test.movies

import android.app.Application
import com.test.movies.data.Repository

open class App : Application() {

    companion object {
        lateinit var data: Repository
            private set
    }

    override fun onCreate() {
        super.onCreate()
        Repository.init(getString(R.string.api_key))
        data = Repository(this)
    }
}