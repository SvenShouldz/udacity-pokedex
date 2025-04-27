package com.shouldz.pokedex.ui.caught

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.shouldz.pokedex.data.local.CaughtPokemon
import com.shouldz.pokedex.data.repository.PokemonRepository
import kotlinx.coroutines.launch

class CaughtListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PokemonRepository(application)

    val caughtPokemonList: LiveData<List<CaughtPokemon>> = repository.getAllCaughtPokemon()

    // Function to release a Pokemon
    fun releasePokemon(pokemonName: String) {
        viewModelScope.launch {
            try {
                repository.releasePokemon(pokemonName)
            } catch (e: Exception) {
                Log.e("CaughtListViewModel", "Failed to release $pokemonName", e)
                throw e
            }
        }
    }

}