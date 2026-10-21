package com.example.filmesfavoritosmvvm

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.filmesfavoritosmvvm.databinding.ItemMovieBinding

class MovieAdapter(
    private val onEditMovie: (Movie) -> Unit
) : ListAdapter<Movie, MovieAdapter.MovieViewHolder>(MovieDiffCallback) {

    class MovieViewHolder(val binding: ItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = getItem(position)
        with(holder.binding) {
            movieName.text = movie.name
            movieRating.text = root.context.getString(
                R.string.rating_format,
                movie.rating
            )
            movieImage.load(movie.photoUrl) {
                crossfade(true)
                placeholder(R.drawable.movie_placeholder)
                error(R.drawable.movie_placeholder)
            }
            editMovieButton.setOnClickListener { onEditMovie(movie) }
        }
    }

    private object MovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean =
            oldItem == newItem
    }
}
