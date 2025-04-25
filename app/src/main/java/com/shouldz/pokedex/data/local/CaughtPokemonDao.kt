package com.shouldz.pokedex.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CaughtPokemonDao {

    // Add caught Pokemon
    @Insert(onConflict = OnConflictStrategy.REPLACE) // Replace Pokemon if already exists
    suspend fun insertCaughtPokemon(caughtPokemon: CaughtPokemon)

    // Delete caught Pokemon
    @Query("DELETE FROM caught_pokemon_table WHERE pokemon_name = :pokemonName")
    suspend fun deleteCaughtPokemon(pokemonName: String)

    // Gets caught Pokemon by name
    @Query("SELECT * FROM caught_pokemon_table WHERE pokemon_name = :pokemonName LIMIT 1")
    fun getCaughtPokemonByName(pokemonName: String): LiveData<CaughtPokemon?>

    // Gets all caught Pokemon's
    @Query("SELECT * FROM caught_pokemon_table ORDER BY pokemon_name ASC")
    fun getAllCaughtPokemon(): LiveData<List<CaughtPokemon>>

}
