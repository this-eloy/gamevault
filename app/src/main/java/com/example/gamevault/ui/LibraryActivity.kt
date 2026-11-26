package com.example.gamevault.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gamevault.BuildConfig
import com.example.gamevault.R
import com.example.gamevault.adapter.GameAdapter
import com.example.gamevault.data.AppDatabase
import com.example.gamevault.data.SteamClient
import com.example.gamevault.databinding.ActivityLibraryBinding
import com.example.gamevault.model.Game
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LibraryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLibraryBinding
    private lateinit var adapter: GameAdapter
    private val dao by lazy { AppDatabase.get(this).gameDao() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = GameAdapter(emptyList()) { game ->
            // Depois: abrir DetailActivity com Intent explícita
        }
        binding.recyclerGames.layoutManager = LinearLayoutManager(this)
        binding.recyclerGames.adapter = adapter

        binding.btnSync.setOnClickListener { syncFromSteam() }

        setupBottomNav() // <- faltava
    }

    override fun onResume() {
        super.onResume()
        loadGames()
    }

    // <- faltava essa função inteira
    private fun setupBottomNav() {
        binding.bottomNavigation.selectedItemId = R.id.nav_biblioteca
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_biblioteca -> true
                R.id.nav_perfil -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    false
                }
                else -> false
            }
        }
    }

    private fun loadGames() {
        lifecycleScope.launch {
            adapter.submitList(dao.getAll())
        }
    }

    private fun syncFromSteam() {
        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val result = SteamClient.api.getOwnedGames(
                        BuildConfig.STEAM_API_KEY,
                        BuildConfig.STEAM_ID
                    )
                    result.response.games.orEmpty().forEach { steamGame ->
                        if (dao.getByAppId(steamGame.appid) == null) {
                            dao.insert(
                                Game(
                                    appId = steamGame.appid,
                                    name = steamGame.name ?: "?",
                                    imageUri = "https://cdn.cloudflare.steamstatic.com/steam/apps/${steamGame.appid}/header.jpg",
                                    playtimeMinutes = steamGame.playtime_forever ?: 0,
                                    source = Game.SOURCE_STEAM
                                )
                            )
                        }
                    }
                }
                loadGames()
            } catch (e: Exception) {
                Toast.makeText(this@LibraryActivity, R.string.sync_error, Toast.LENGTH_LONG).show()
            }
        }
    }
}