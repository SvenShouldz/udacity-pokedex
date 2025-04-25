package com.shouldz.pokedex.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shouldz.pokedex.data.model.PokemonDetailResponse
import com.shouldz.pokedex.data.repository.PokemonRepository
import kotlinx.coroutines.launch

class PokemonDetailViewModel : ViewModel() {

    private val repository = PokemonRepository()

    private val _pokemonDetail = MutableLiveData<PokemonDetailResponse?>()
    val pokemonDetail: LiveData<PokemonDetailResponse?> get() = _pokemonDetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun loadPokemonDetail(pokemonName: String) {
        // Don't reload if already loading or data exists
        if (_isLoading.value == true || _pokemonDetail.value?.name == pokemonName) {
            return
        }

        _pokemonDetail.value = null
        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val detail = repository.getPokemonDetail(name = pokemonName)
                _pokemonDetail.value = detail
                if (detail == null) {
                    // If repository returned null
                    _error.value = "Failed to load details for $pokemonName"
                }
            } catch (e: Exception) {
                println("Exception in DetailViewModel: ${e.message}")
                _error.value = "An unexpected error occurred."
                _pokemonDetail.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onErrorShown() {
        _error.value = null
    }

    // --- TODO: Add logic for Catch/Release button later ---

}
