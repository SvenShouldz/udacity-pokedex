package com.shouldz.pokedex.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PokemonDetailResponse(
    @Json(name = "id")
    val id: Int,

    @Json(name = "name")
    val name: String,

    @Json(name = "height")
    val height: Int, // Height in decimetres

    @Json(name = "weight")
    val weight: Int, // Weight in hectograms

    @Json(name = "sprites")
    val sprites: Sprites, // List of Sprites object

    @Json(name = "stats")
    val stats: List<StatSlot>, // List of StatSlot objects

    @Json(name = "types")
    val types: List<TypeSlot> // List of TypeSlot objects

)