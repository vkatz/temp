package com.test.movies.ui.components.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.test.movies.R
import com.test.movies.data.Movie
import com.test.movies.data.Repository
import com.test.movies.utils.formatAsDate
import com.test.movies.utils.glide
import com.test.movies.utils.loadInto
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.movie_list_item.*
import java.util.*

class MoviesAdapter(private var movies: ArrayList<Movie>, private val onMovieClickListener: (Long) -> Unit) :
    RecyclerView.Adapter<MoviesAdapter.ItemViewHolder>() {

    fun updateData(movies: ArrayList<Movie>) {
        this.movies = movies
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder =
        ItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.movie_list_item, parent, false))

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    override fun getItemCount(): Int = movies.size

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), LayoutContainer {

        override val containerView: View? = itemView
        private var data: Movie? = null

        init {
            itemView.setOnClickListener {
                data?.id?.let { id -> onMovieClickListener(id) }
            }
        }

        fun bind(movie: Movie) {
            data = movie
            movieItemTitle.text = movie.title
            movieItemPopularity.text = movie.popularity
            movieItemReleaseDate.text = movie.releaseDate.formatAsDate()
            movieItemOverview.text = movie.overview
            if (movie.posterPath != null && movie.posterPath.isNotEmpty())
                itemView.context.glide(Repository.getSmallImageUrl(movie.posterPath)).loadInto(movieItemImage)
        }
    }
}