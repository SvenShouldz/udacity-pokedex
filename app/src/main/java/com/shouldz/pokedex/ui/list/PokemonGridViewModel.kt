package com.shouldz.pokedex.ui.list

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.shouldz.pokedex.R
import com.shouldz.pokedex.data.model.PokemonResult
import com.shouldz.pokedex.data.repository.PokemonRepository
import com.shouldz.pokedex.data.repository.PokemonRepository.Companion
import kotlinx.coroutines.launch
import java.io.IOException

class PokemonGridViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PokemonRepository(application)
    private val app = application

    private val _filteredPokemonList = MediatorLiveData<List<PokemonResult>>()
    val filteredPokemonList: LiveData<List<PokemonResult>> get() = _filteredPokemonList

    // Holds the loading state (true when loading, false otherwise)
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    // Handling errors
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    // Holds unfiltered list fetched from API
    private val _originalPokemonList = MutableLiveData<List<PokemonResult>>()
    val originalPokemonList: LiveData<List<PokemonResult>> get() = _originalPokemonList

    private val _searchQuery = MutableLiveData<String>("")

    companion object {
        private const val POKEMON_LIMIT = 151 // Fetch first 151 Pokemon (Gen 1)
        private const val POKEMON_OFFSET = 0
        private const val TAG = "PokemonGridViewModel"
    }

    init {
        _filteredPokemonList.addSource(_originalPokemonList) { list ->
            _filteredPokemonList.value = filterList(list, _searchQuery.value)
        }
        _filteredPokemonList.addSource(_searchQuery) { query ->
            _filteredPokemonList.value = filterList(_originalPokemonList.value, query)
        }
    }

    fun loadPokemonList() {
        // Don't reload if already loading or data exists
        if (_isLoading.value == true || !_originalPokemonList.value.isNullOrEmpty()) {
            return
        }

        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                val results =
                    repository.getPokemonList(limit = POKEMON_LIMIT, offset = POKEMON_OFFSET)
                _originalPokemonList.value = results
            }catch (e: IOException) {
                _error.value = app.getString(R.string.error_network_connection)
                _originalPokemonList.value = emptyList()
            } catch (e: Exception) {
                // Handle error
                Log.e(TAG,"Exception in ViewModel: ${e.message}")
                _originalPokemonList.value = emptyList()
            } finally {
                // Set loading state to false after fetching
                _isLoading.value = false
            }
        }
    }

    fun retryLoadList() {
        // Clear the current list to ensure loadPokemonList fetches again
        _originalPokemonList.value = emptyList()
        loadPokemonList()
    }

    fun onErrorShown() {
        _error.value = null
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private fun filterList(list: List<PokemonResult>?, query: String?): List<PokemonResult> {
        val currentList = list ?: emptyList()
        val currentQuery = query ?: ""

        return if (currentQuery.isBlank()) {
            currentList
        } else {
            // filter original list based on query
            currentList.filter { pokemon ->
                pokemon.name.contains(currentQuery, ignoreCase = true)
            }
        }
    }
}