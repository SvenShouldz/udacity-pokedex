package com.shouldz.pokedex.ui.caught

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.shouldz.pokedex.data.local.CaughtPokemon
import com.shouldz.pokedex.data.repository.PokemonRepository

class CaughtListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PokemonRepository(application)

    val caughtPokemonList: LiveData<List<CaughtPokemon>> = repository.getAllCaughtPokemon()

}