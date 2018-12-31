package com.test.movies.data

import android.arch.persistence.room.Room
import android.content.Context
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.test.movies.BuildConfig
import com.test.movies.data.api.Api
import com.test.movies.data.database.AppDatabase
import com.test.movies.utils.enqueue
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class Repository(context: Context) {
    private val api: Api
    private val database: AppDatabase

    companion object {
        private const val BASE_URL = "https://api.themoviedb.org/3/movie/"
        private const val MOVIE_URL_IMAGE_SMALL = "https://image.tmdb.org/t/p/w500/"
        private const val MOVIE_URL_IMAGE_ORIGINAL = "https://image.tmdb.org/t/p/original/"

        private var apiKey = ""

        fun init(apiKey: String) {
            this.apiKey = apiKey
        }

        fun getSmallImageUrl(path: String) = MOVIE_URL_IMAGE_SMALL + path

        fun getOriginImageUrl(path: String) = MOVIE_URL_IMAGE_ORIGINAL + path
    }

    init {
        val gson = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()
        database = Room.databaseBuilder(context, AppDatabase::class.java, "database")
            .allowMainThreadQueries()
            .build()
        api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .apply {
                if (BuildConfig.DEBUG) client(
                    OkHttpClient.Builder().apply {
                        connectTimeout(5, TimeUnit.SECONDS)
                        readTimeout(5, TimeUnit.SECONDS)
                    }.addInterceptor(
                        HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
                    ).build()
                )
            }
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build().create(Api::class.java)
    }

    fun getMovies(callback: (Boolean, ArrayList<Movie>?) -> Unit) {
        api.getPopularMovies(apiKey).enqueue { popularMovies ->
            if (popularMovies != null) {
                callback(true, popularMovies.results)
            } else {
                callback(false, getFavourites())
            }
        }
    }

    fun getMovie(movieId: Long, callback: (Boolean, Movie?) -> Unit) {
        api.getMovie(movieId, apiKey).enqueue { movie ->
            if (movie != null) {
                callback(true, movie)
                if (getFavorite(movieId) != null)
                    update(movie)
            } else callback(false, getFavorite(movieId))
        }
    }

    fun save(movie: Movie) {
        database.movieDao().insert(movie)
    }

    fun delete(movie: Movie) {
        database.movieDao().delete(movie)
    }

    fun getFavorite(movieId: Long) = database.movieDao().getById(movieId)

    private fun update(movie: Movie) {
        database.movieDao().update(movie)
    }

    private fun getFavourites() = ArrayList(database.movieDao().getAll())
}