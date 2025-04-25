package com.shouldz.pokedex.data.repository

import com.shouldz.pokedex.data.model.PokemonDetailResponse
import com.shouldz.pokedex.data.model.PokemonResult
import com.shouldz.pokedex.data.remote.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class PokemonRepository {

    // Get the API service instance from ApiClient
    private val pokemonApiService = ApiClient.api

    suspend fun getPokemonList(limit: Int, offset: Int): List<PokemonResult> {
        return withContext(Dispatchers.IO) {
            try {
                val response = pokemonApiService.getPokemonList(limit = limit, offset = offset)
                response.results
            } catch (e: IOException) {
                println("Network error fetching Pokemon list: ${e.message}")
                emptyList<PokemonResult>()
            } catch (e: Exception) {
                println("Error fetching Pokemon list: ${e.message}")
                emptyList<PokemonResult>()
            }
        }
    }

    suspend fun getPokemonDetail(name: String): PokemonDetailResponse? {
        return withContext(Dispatchers.IO) {
            try {
                pokemonApiService.getPokemonDetail(name = name.lowercase())
            } catch (e: IOException) {
                println("Network error fetching Pokemon detail for $name: ${e.message}")
                null // Return null on network error
            } catch (e: Exception) {
                println("Error fetching Pokemon detail for $name: ${e.message}")
                null // Return null on other errors
            }
        }
    }

}