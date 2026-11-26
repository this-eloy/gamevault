package com.example.gamevault.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.gamevault.R
import com.example.gamevault.data.AppDatabase
import com.example.gamevault.databinding.ActivityDetailBinding
import com.example.gamevault.model.Game
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_GAME_ID = "game_id"
    }

    private lateinit var binding: ActivityDetailBinding
    private lateinit var db: AppDatabase
    private var game: Game? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.get(this)

        loadData()
        setupListeners()
    }

    private fun loadData() {
        val gameId = intent.getIntExtra(EXTRA_GAME_ID, -1)
        if (gameId == -1) {
            finish()
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val loadedGame = db.gameDao().getById(gameId)
            withContext(Dispatchers.Main) {
                if (loadedGame == null) {
                    finish()
                } else {
                    game = loadedGame
                    setupViews(loadedGame)
                }
            }
        }
    }

    private fun setupViews(game: Game) {
        binding.tvNome.text = game.name
        binding.imgCapa.load(game.imageUri)
        binding.tvOrigem.text = if (game.source == Game.SOURCE_STEAM) {
            getString(R.string.detalhe_origem_steam)
        } else {
            getString(R.string.detalhe_origem_manual)
        }
        binding.tvTempoJogado.text = formatPlaytime(game.playtimeMinutes)
        binding.ratingNota.rating = game.rating
        updateRatingText(game.rating)
    }

    private fun setupListeners() {
        binding.ratingNota.setOnRatingBarChangeListener { _, rating, fromUser ->
            updateRatingText(rating)
            if (fromUser) {
                saveRating(rating)
            }
        }

        binding.btnAbrirSteam.setOnClickListener {
            val currentGame = game ?: return@setOnClickListener
            val url = if (currentGame.appId != null) {
                "https://store.steampowered.com/app/${currentGame.appId}"
            } else {
                "https://store.steampowered.com/search/?term=${Uri.encode(currentGame.name)}"
            }

            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = url.toUri()
            openExternalIntent(intent)
        }

        binding.btnCompartilhar.setOnClickListener {
            val currentGame = game ?: return@setOnClickListener
            val text = getString(
                R.string.detalhe_compartilhar_texto,
                currentGame.name,
                formatPlaytime(currentGame.playtimeMinutes)
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
            }
            openExternalIntent(Intent.createChooser(intent, getString(R.string.detalhe_compartilhar)))
        }

        binding.btnVoltar.setOnClickListener {
            finish()
        }
    }

    private fun saveRating(rating: Float) {
        val currentGame = game ?: return
        val updatedGame = currentGame.copy(rating = rating)
        game = updatedGame

        lifecycleScope.launch(Dispatchers.IO) {
            db.gameDao().update(updatedGame)
        }
    }

    private fun updateRatingText(rating: Float) {
        binding.tvNota.text = if (rating > 0f) {
            getString(R.string.detalhe_nota_valor, rating)
        } else {
            getString(R.string.detalhe_sem_nota)
        }
    }

    private fun openExternalIntent(intent: Intent) {
        try {
            startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(this, R.string.detalhe_erro_intent, Toast.LENGTH_SHORT).show()
        }
    }

    private fun formatPlaytime(minutes: Int): String {
        if (minutes <= 0) return getString(R.string.detalhe_sem_tempo)
        val hours = minutes / 60
        val remainingMinutes = minutes % 60
        return when {
            hours > 0 && remainingMinutes > 0 ->
                getString(R.string.detalhe_tempo_horas_min, hours, remainingMinutes)
            hours > 0 ->
                getString(R.string.detalhe_tempo_horas, hours)
            else ->
                getString(R.string.detalhe_tempo_min, remainingMinutes)
        }
    }
}
