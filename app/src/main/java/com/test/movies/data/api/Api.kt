package com.test.movies.data.api

import com.test.movies.data.Movie
import com.test.movies.data.MovieListResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Api {
    @GET("popular/")
    fun getPopularMovies(@Query("api_key") apiKey: String): Call<MovieListResponse>

    @GET("{movie_id}")
    fun getMovie(@Path("movie_id") movieId: Long, @Query("api_key") apiKey: String): Call<Movie>
}