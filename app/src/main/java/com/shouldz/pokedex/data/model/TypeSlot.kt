package com.shouldz.pokedex.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TypeSlot(
    @Json(name = "slot")
    val slot: Int,

    @Json(name = "type")
    val type: TypeInfo
)
