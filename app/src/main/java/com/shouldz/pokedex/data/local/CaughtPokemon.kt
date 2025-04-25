package com.shouldz.pokedex.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "caught_pokemon_table")
data class CaughtPokemon(

    @PrimaryKey
    @ColumnInfo(name = "pokemon_name")
    val name: String,

    @ColumnInfo(name = "sprite_url")
    val spriteUrl: String?
)