package com.shouldz.pokedex.data.remote

import com.shouldz.pokedex.data.model.PokemonDetailResponse
import com.shouldz.pokedex.data.model.PokemonListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokeApiService {

    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int,   // maximum number of Pokemon results
        @Query("offset") offset: Int  // starting index from which to return Pokemon results
    ): PokemonListResponse

    @GET("pokemon/{name}")
    suspend fun getPokemonDetail(
        @Path("name") name: String
    ): PokemonDetailResponse

}