package com.example.test_lab_week_13

import android.util.Log
import com.example.test_lab_week_13.api.MovieService
import com.example.test_lab_week_13.database.MovieDao
import com.example.test_lab_week_13.database.MovieDatabase
import com.example.test_lab_week_13.model.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class MovieRepository(
    private val movieService: MovieService,
    private val movieDatabase: MovieDatabase
) {
    private val apiKey = BuildConfig.API_KEY

    fun fetchMovies(): Flow<List<Movie>> {
        return flow {
            val movieDao = movieDatabase.movieDao()
            val savedMovies = movieDao.getMovies()

            if (savedMovies.isEmpty()) {
                val movies = movieService.getPopularMovies(apiKey).results
                movieDao.addMovies(movies)
                emit(movies)
            } else {
                emit(savedMovies)
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun fetchMoviesFromNetwork(){
        val movieDao: MovieDao = movieDatabase.movieDao()
        try{
            val popularMovies = movieService.getPopularMovies(apiKey)
            val moviesFetched = popularMovies.results
            movieDao.addMovies(moviesFetched)
        } catch (exception: Exception){
            Log.d(
                "MovieRepository",
                "Error fetching movies from network: ${exception.message}"
            )
        }
    }
}