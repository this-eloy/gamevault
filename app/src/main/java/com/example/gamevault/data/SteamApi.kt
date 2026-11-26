package com.example.gamevault.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// biblioteca do usuário
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

// busca na loja
data class StoreSearchResponse(
    val total: Int?,
    val items: List<StoreSearchItem>?
)

data class StoreSearchItem(
    val id: Int,
    val name: String?,
    val type: String?,
    val tiny_image: String?
)

interface SteamStoreApi {
    @GET("api/storesearch/")
    suspend fun search(
        @Query("term") term: String,
        @Query("l") language: String = "portuguese",
        @Query("cc") country: String = "BR"
    ): StoreSearchResponse
}

object SteamStoreClient {
    val api: SteamStoreApi = Retrofit.Builder()
        .baseUrl("https://store.steampowered.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(SteamStoreApi::class.java)
}

fun steamHeaderUrl(appId: Int) =
    "https://cdn.cloudflare.steamstatic.com/steam/apps/$appId/header.jpg"