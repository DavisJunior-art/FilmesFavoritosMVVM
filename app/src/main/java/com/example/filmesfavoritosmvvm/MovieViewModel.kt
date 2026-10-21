package com.example.filmesfavoritosmvvm

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MovieViewModel : ViewModel() {
    private val _movies = MutableStateFlow(
        listOf(
            Movie(
                id = 1,
                name = "Interestelar",
                rating = 9.0,
                photoUrl = "https://images.unsplash.com/photo-1446776811953-b23d57bd21aa?auto=format&fit=crop&w=900&q=85"
            ),
            Movie(
                id = 2,
                name = "A Viagem de Chihiro",
                rating = 9.5,
                photoUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?auto=format&fit=crop&w=900&q=85"
            ),
            Movie(
                id = 3,
                name = "O Poderoso Chefão",
                rating = 9.2,
                photoUrl = "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=900&q=85"
            )
        )
    )

    val movies: StateFlow<List<Movie>> = _movies.asStateFlow()

    fun updateMovie(updatedMovie: Movie) {
        _movies.value = _movies.value.map { movie ->
            if (movie.id == updatedMovie.id) updatedMovie else movie
        }
    }
}
