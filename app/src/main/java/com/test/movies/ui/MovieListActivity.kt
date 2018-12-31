package com.test.movies.ui

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.test.movies.App
import com.test.movies.R
import com.test.movies.data.Movie
import com.test.movies.ui.components.adapters.MoviesAdapter
import com.test.movies.utils.makeVisibleOrGone
import com.test.movies.utils.showSystemMessage
import kotlinx.android.synthetic.main.activity_movie_list.*
import kotlinx.android.synthetic.main.template_progress.*

class MovieListViewModel : ViewModel() {
    data class MoviesState(val isLoading: Boolean, val success: Boolean, val movies: ArrayList<Movie>?)

    val movies = MutableLiveData<MoviesState>()

    init {
        reload()
    }

    fun reload() {
        movies.value = MoviesState(true, true, movies.value?.movies ?: ArrayList())
        App.data.getMovies { success, data -> movies.value = MoviesState(false, success, data) }
    }
}

class MovieListActivity : AppCompatActivity() {

    private val model by lazy { ViewModelProviders.of(this).get(MovieListViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_list)
        initView()
    }

    private fun initView() {
        movieListContent.layoutManager = LinearLayoutManager(this)
        movieListContent.adapter = MoviesAdapter(ArrayList()) {
            startActivity(MovieDetailActivity.getIntent(this, it))
        }
        movieListReload.setOnClickListener { model.reload() }
        movieListRefresh.setOnRefreshListener { model.reload() }

        model.movies.observe(this, Observer { data ->
            movieListRefresh.isRefreshing = false
            templateProgress.makeVisibleOrGone(data!!.isLoading)
            movieListReload.makeVisibleOrGone(!data.isLoading && data.movies.isNullOrEmpty())
            movieListEmpty.makeVisibleOrGone(!data.isLoading && data.movies != null && data.movies.isEmpty())
            (!data.isLoading && !data.movies.isNullOrEmpty()).let { hasContent ->
                movieListContent.makeVisibleOrGone(hasContent)
                (movieListContent.adapter as MoviesAdapter).updateData(data.movies!!)
            }
            if (!data.success) movieListEmpty.showSystemMessage(getString(R.string.app_connection_problem))
        })
    }
}