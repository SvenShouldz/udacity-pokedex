package com.shouldz.pokedex.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PokemonResult(
    @Json(name = "name")
    val name: String,

    @Json(name = "url")
    val url: String
)