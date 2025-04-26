package com.shouldz.pokedex.ui.detail

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.shouldz.pokedex.R
import com.shouldz.pokedex.data.local.CaughtPokemon
import com.shouldz.pokedex.data.model.PokemonDetailResponse
import com.shouldz.pokedex.data.repository.PokemonRepository
import kotlinx.coroutines.launch

class PokemonDetailViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "PokemonDetailViewModel"
    }

    private val repository = PokemonRepository(application)
    private val app = application

    private val _pokemonDetail = MutableLiveData<PokemonDetailResponse?>()
    val pokemonDetail: LiveData<PokemonDetailResponse?> get() = _pokemonDetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val isCaughtObject: LiveData<CaughtPokemon?> = _pokemonDetail.switchMap { detail ->
        if (detail == null) {
            MutableLiveData<CaughtPokemon?>(null) // If no detail loaded, return null
        } else {
            repository.isPokemonCaught(detail.name)
        }
    }

    val isCaught: LiveData<Boolean> = isCaughtObject.map { caughtPokemon ->
        caughtPokemon != null // Map non-null to true, null to false
    }

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
                    _error.value = app.getString(R.string.failed_to_load_details, pokemonName)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception: ${e.message}")
                _pokemonDetail.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onCatchReleaseClicked() {
        val currentDetail = _pokemonDetail.value ?: return

        viewModelScope.launch {
            if (isCaught.value == true) {
                // Release Pokemon
                try {
                    repository.releasePokemon(currentDetail.name)
                } catch (e: Exception) {
                    _error.value = app.getString(R.string.failed_to_release, currentDetail.name)
                }
            } else {
                // Catch Pokemon
                val spriteUrl = currentDetail.sprites.frontDefault ?: ""
                val pokemonToCatch = CaughtPokemon(name = currentDetail.name, spriteUrl = spriteUrl)

                try {
                    repository.catchPokemon(pokemonToCatch)
                } catch (e: Exception) {
                    _error.value = app.getString(R.string.failed_to_catch, currentDetail.name)
                }
            }
        }
    }

    fun onErrorShown() {
        _error.value = null
    }

}
