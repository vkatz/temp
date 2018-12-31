package com.test.movies.ui

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.test.movies.App
import com.test.movies.R
import com.test.movies.data.Movie
import com.test.movies.data.Repository
import com.test.movies.utils.*
import kotlinx.android.synthetic.main.activity_movie_detail.*
import kotlinx.android.synthetic.main.template_progress.*

class MovieDetailViewModel : ViewModel() {
    companion object {
        const val INVALID_ID = -1L
    }

    data class MoviesDataState(val isLoading: Boolean, val success: Boolean, val movie: Movie?)

    private var movieId: Long = INVALID_ID

    val movie = MutableLiveData<MoviesDataState>()

    init {
        movie.value = MoviesDataState(true, true, null)
    }

    fun setMovieId(id: Long) {
        movieId = id
        if (movie.value?.movie == null)
            reload()
    }

    fun reload() {
        if (movieId == INVALID_ID) return
        movie.value = MoviesDataState(true, true, null)
        App.data.getMovie(movieId) { success, data -> movie.value = MoviesDataState(false, success, data) }
    }

    fun setFavorite(favorite: Boolean) {
        movie.value?.movie ?: return
        if (favorite) App.data.save(movie.value!!.movie!!)
        else App.data.delete(movie.value!!.movie!!)
    }

    fun isFavorite() = movie.value?.movie?.let { App.data.getFavorite(it.id) != null } ?: false

}

class MovieDetailActivity : AppCompatActivity() {

    companion object {
        const val MOVIE_ID = "MOVIE_ID"

        fun getIntent(context: Context, id: Long) = Intent(context, MovieDetailActivity::class.java).apply {
            putExtra(MOVIE_ID, id)
        }
    }

    private val model by lazy { ViewModelProviders.of(this).get(MovieDetailViewModel::class.java) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)
        model.setMovieId(intent.getLongExtra(MOVIE_ID, MovieDetailViewModel.INVALID_ID))
        initView()
    }

    private fun initView() {
        setSupportActionBar(movieDetailToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        movieDetailToolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.color_white))
        movieDetailToolbar.setNavigationOnClickListener { finish() }
        movieDetailReload.setOnClickListener { model.reload() }

        model.movie.observe(this, Observer { data ->
            templateProgress.makeVisibleOrGone(data!!.isLoading)
            movieDetailReload.makeVisibleOrGone(!data.isLoading && data.movie == null)
            (data.movie != null).let { hasContent ->
                movieDetailContent.makeVisibleOrGone(hasContent)
                if (hasContent) fillData(data.movie!!)
            }
            if (!data.success) movieDetailContent.showSystemMessage(getString(R.string.app_connection_problem))
        })
    }

    private fun fillData(movie: Movie) {
        supportActionBar?.title = movie.title
        movieDetailTitle.text = movie.title
        movieDetailOverview.text = movie.overview

        if (movie.popularity.isBlank()) movieDetailPopularityGroup.makeGone()
        else movieDetailPopularity.text = movie.popularity
        if (movie.releaseDate.isBlank()) movieDetailReleaseDateGroup.makeGone()
        else movieDetailReleaseDate.text = movie.releaseDate.formatAsDate()
        if (movie.budget.isNullOrBlank()) movieDetailBudgetGroup.makeGone()
        else movieDetailBudget.text = movie.budget
        if (movie.homepage.isNullOrBlank()) movieDetailHomepageGroup.makeGone()
        else movieDetailHomepage.text = movie.homepage
        if (movie.revenue.isNullOrBlank()) movieDetailRevenueGroup.makeGone()
        else movieDetailRevenue.text = movie.revenue
        if (movie.runtime.isNullOrBlank()) movieDetailRuntimeGroup.makeGone()
        else movieDetailRuntime.text = movie.runtime

        if (movie.backdropPath != null && movie.backdropPath.isNotEmpty())
            glide(Repository.getSmallImageUrl(movie.backdropPath)).loadInto(movieDetailBackgroundImage)

        if (movie.posterPath != null && movie.posterPath.isNotEmpty())
            glide(Repository.getOriginImageUrl(movie.posterPath)).loadInto(movieDetailImage)

        movieDetailFavBtn.isChecked = model.isFavorite()
        movieDetailFavBtn.setOnClickListener { model.setFavorite(movieDetailFavBtn.isChecked) }
    }
}