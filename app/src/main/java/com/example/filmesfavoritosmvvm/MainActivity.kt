package com.example.filmesfavoritosmvvm

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.filmesfavoritosmvvm.databinding.ActivityMainBinding
import com.example.filmesfavoritosmvvm.databinding.DialogEditMovieBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.Locale
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MovieViewModel by viewModels()
    private val movieAdapter = MovieAdapter(::showEditMovieDialog)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.moviesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.moviesRecyclerView.adapter = movieAdapter

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.movies.collect { movies ->
                    movieAdapter.submitList(movies)
                    binding.movieCount.text = resources.getQuantityString(
                        R.plurals.movie_count,
                        movies.size,
                        movies.size
                    )
                }
            }
        }
    }

    private fun showEditMovieDialog(movie: Movie) {
        val dialogBinding = DialogEditMovieBinding.inflate(LayoutInflater.from(this))
        dialogBinding.nameInput.setText(movie.name)
        dialogBinding.ratingInput.setText(
            String.format(Locale.getDefault(), "%.1f", movie.rating)
        )
        dialogBinding.photoUrlInput.setText(movie.photoUrl)
        dialogBinding.photoPreview.loadMoviePhoto(movie.photoUrl)

        dialogBinding.photoUrlInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                dialogBinding.photoPreview.loadMoviePhoto(
                    dialogBinding.photoUrlInput.text.toString().trim()
                )
            }
        }

        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle(R.string.edit_movie_title)
            .setView(dialogBinding.root)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.save_changes, null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val name = dialogBinding.nameInput.text.toString().trim()
                val rating = dialogBinding.ratingInput.text.toString()
                    .trim()
                    .replace(',', '.')
                    .toDoubleOrNull()
                val photoUrl = dialogBinding.photoUrlInput.text.toString().trim()

                dialogBinding.nameInput.error = null
                dialogBinding.ratingInput.error = null
                dialogBinding.photoUrlInput.error = null

                when {
                    name.isEmpty() -> dialogBinding.nameInput.error = getString(R.string.name_required)
                    rating == null || rating !in 0.0..10.0 -> dialogBinding.ratingInput.error = getString(R.string.rating_invalid)
                    photoUrl.isEmpty() || !Patterns.WEB_URL.matcher(photoUrl).matches() -> dialogBinding.photoUrlInput.error = getString(R.string.photo_url_invalid)
                    else -> {
                        viewModel.updateMovie(
                            movie.copy(name = name, rating = rating, photoUrl = photoUrl)
                        )
                        dialog.dismiss()
                        Toast.makeText(this, R.string.movie_updated, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        dialog.show()
    }
}

private fun android.widget.ImageView.loadMoviePhoto(url: String) {
    load(url) {
        crossfade(true)
        placeholder(R.drawable.movie_placeholder)
        error(R.drawable.movie_placeholder)
    }
}
