package com.test.movies.data

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.*

data class MovieListResponse(val results: ArrayList<Movie>)

@Entity
data class Movie(
    @PrimaryKey
    val id: Long,
    val title: String,
    val popularity: String,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("backdrop_path")
    val backdropPath: String?,
    val overview: String,
    @SerializedName("release_date")
    val releaseDate: String,
    val budget: String?,
    val homepage: String?,
    val revenue: String?,
    val runtime: String?
)

