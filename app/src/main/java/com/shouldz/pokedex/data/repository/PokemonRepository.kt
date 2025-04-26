package com.shouldz.pokedex.data.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import com.shouldz.pokedex.data.local.AppDatabase
import com.shouldz.pokedex.data.local.CaughtPokemon
import com.shouldz.pokedex.data.local.CaughtPokemonDao
import com.shouldz.pokedex.data.model.PokemonDetailResponse
import com.shouldz.pokedex.data.model.PokemonResult
import com.shouldz.pokedex.data.remote.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class PokemonRepository(application: Application) {

    companion object {
        private const val TAG = "PokemonRepository"
    }

    // Get the API service instance from ApiClient
    private val pokemonApiService = ApiClient.api

    // Get the DAO from the singleton Database instance
    private val caughtPokemonDao: CaughtPokemonDao

    init {
        val database = AppDatabase.getDatabase(application)
        caughtPokemonDao = database.caughtPokemonDao()
    }

    // Gets all Pokemon in range of limit/offset from API
    suspend fun getPokemonList(limit: Int, offset: Int): List<PokemonResult> {
        return withContext(Dispatchers.IO) {
            try {
                val response = pokemonApiService.getPokemonList(limit = limit, offset = offset)
                response.results
            } catch (e: IOException) {
                Log.e(TAG, "Network error fetching Pokemon list: ${e.message}")
                emptyList<PokemonResult>()
            } catch (e: Exception) {
                Log.e(TAG,"Error fetching Pokemon list: ${e.message}")
                emptyList<PokemonResult>()
            }
        }
    }

    // Gets Pokemon details by name
    suspend fun getPokemonDetail(name: String): PokemonDetailResponse? {
        return withContext(Dispatchers.IO) {
            try {
                pokemonApiService.getPokemonDetail(name = name.lowercase())
            } catch (e: IOException) {
                Log.e(TAG,"Network error fetching Pokemon detail for $name: ${e.message}")
                null // Return null on network error
            } catch (e: Exception) {
                Log.e(TAG,"Error fetching Pokemon detail for $name: ${e.message}")
                null // Return null on other errors
            }
        }
    }

    // Checks if Pokemon is caught
    fun isPokemonCaught(name: String): LiveData<CaughtPokemon?> {
        return caughtPokemonDao.getCaughtPokemonByName(name)
    }

    // Catches/Saves Pokemon to RoomDB
    suspend fun catchPokemon(pokemon: CaughtPokemon) {
        withContext(Dispatchers.IO) {
            caughtPokemonDao.insertCaughtPokemon(pokemon)
        }
    }

    // Release Pokemon
    suspend fun releasePokemon(pokemonName: String) {
        withContext(Dispatchers.IO) {
            caughtPokemonDao.deleteCaughtPokemon(pokemonName)
        }
    }

    // Get all caught Pokemon
    fun getAllCaughtPokemon(): LiveData<List<CaughtPokemon>> {
        return caughtPokemonDao.getAllCaughtPokemon()
    }

}