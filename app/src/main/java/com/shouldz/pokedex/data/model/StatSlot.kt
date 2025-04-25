package com.shouldz.pokedex.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StatSlot(
    @Json(name = "base_stat")
    val baseStat: Int,

    @Json(name = "effort")
    val effort: Int,

    @Json(name = "stat")
    val stat: StatInfo
)