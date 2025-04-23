package com.shouldz.pokedex.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shouldz.pokedex.data.model.PokemonResult
import com.shouldz.pokedex.data.repository.PokemonRepository
import kotlinx.coroutines.launch

class PokemonListViewModel : ViewModel() {

    private val repository = PokemonRepository()

    private val _pokemonList = MutableLiveData<List<PokemonResult>>()
    val pokemonList: LiveData<List<PokemonResult>> get() = _pokemonList

    // Holds the loading state (true when loading, false otherwise)
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    companion object {
        private const val POKEMON_LIMIT = 151 // Fetch first 151 Pokemon (Gen 1)
        private const val POKEMON_OFFSET = 0
    }

    fun loadPokemonList() {
        // Don't reload if already loading or data exists
        if (_isLoading.value == true || !_pokemonList.value.isNullOrEmpty()) {
            return
        }

        _isLoading.value = true

        viewModelScope.launch {
            try {
                val results =
                    repository.getPokemonList(limit = POKEMON_LIMIT, offset = POKEMON_OFFSET)
                _pokemonList.value = results
            } catch (e: Exception) {
                // Handle error
                println("Exception in ViewModel: ${e.message}")
                _pokemonList.value = emptyList()
            } finally {
                // Set loading state to false after fetching
                _isLoading.value = false
            }
        }
    }
}