package com.example.gamevault.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class OwnedGamesResponse(val response: OwnedGamesBody)

data class OwnedGamesBody(
    val game_count: Int?,
    val games: List<SteamGame>?
)

data class SteamGame(
    val appid: Int,
    val name: String?,
    val playtime_forever: Int?
)

interface SteamApi {
    @GET("IPlayerService/GetOwnedGames/v1/")
    suspend fun getOwnedGames(
        @Query("key") key: String,
        @Query("steamid") steamId: String,
        @Query("include_appinfo") includeAppInfo: Int = 1,
        @Query("format") format: String = "json"
    ): OwnedGamesResponse
}

object SteamClient {
    val api: SteamApi = Retrofit.Builder()
        .baseUrl("https://api.steampowered.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(SteamApi::class.java)
}