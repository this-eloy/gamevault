package com.example.gamevault.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.gamevault.R
import com.example.gamevault.adapter.ProfileGameAdapter
import com.example.gamevault.data.AppDatabase
import com.example.gamevault.databinding.ActivityProfileBinding
import com.example.gamevault.model.Game
import com.example.gamevault.model.Profile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class ProfileActivity : AppCompatActivity() {

    companion object {
        private const val PREVIEW_LIMIT = 20
    }

    private lateinit var binding: ActivityProfileBinding
    private val db by lazy { AppDatabase.get(this) }

    private var profile = Profile()
    private val catalogAdapter = ProfileGameAdapter { openGameDetail(it) }
    private val steamAdapter = ProfileGameAdapter { openGameDetail(it) }

    private val pickPhoto =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) savePhoto(uri)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerCatalogados.layoutManager =
            LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        binding.recyclerCatalogados.adapter = catalogAdapter
        binding.recyclerSteam.layoutManager =
            LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        binding.recyclerSteam.adapter = steamAdapter

        val openPicker = {
            pickPhoto.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }

        binding.btnEditarPerfil.setOnClickListener { setEditMode(true) }
        binding.btnCancelarEdicao.setOnClickListener {
            bindProfileViews()
            setEditMode(false)
        }
        binding.cardAvatar.setOnClickListener { openPicker() }
        binding.btnAlterarFoto.setOnClickListener { openPicker() }
        binding.btnSalvarPerfil.setOnClickListener { saveProfile() }

        setEditMode(false)
        setupBottomNav()
        loadProfile()
    }

    override fun onResume() {
        super.onResume()
        loadGames()
    }

    private fun setEditMode(editing: Boolean) {
        binding.layoutVisualizacao.isVisible = !editing
        binding.btnEditarPerfil.isVisible = !editing
        binding.layoutEdicao.isVisible = editing
        binding.cardAvatar.isClickable = editing
        binding.cardAvatar.isFocusable = editing

        if (!editing) {
            currentFocus?.clearFocus()
            WindowCompat.getInsetsController(window, binding.root).hide(WindowInsetsCompat.Type.ime())
        }
    }

    private fun setupBottomNav() {
        binding.bottomNavigation.selectedItemId = R.id.nav_perfil
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_perfil -> true
                R.id.nav_biblioteca -> {
                    finish()
                    false
                }
                else -> false
            }
        }
    }

    private fun loadProfile() {
        lifecycleScope.launch {
            profile = db.profileDao().get() ?: Profile()
            bindProfileViews()
            showPhoto()
        }
    }

    private fun bindProfileViews() {
        binding.etNome.setText(profile.name)
        binding.etDescricao.setText(profile.description)
        binding.tvNome.text = profile.name.ifBlank { getString(R.string.perfil_sem_nome) }
        binding.tvDescricao.text = profile.description.ifBlank { getString(R.string.perfil_sem_descricao) }
    }

    private fun saveProfile() {
        val updated = profile.copy(
            name = binding.etNome.text?.toString().orEmpty().trim(),
            description = binding.etDescricao.text?.toString().orEmpty().trim()
        )
        lifecycleScope.launch {
            db.profileDao().save(updated)
            profile = updated
            bindProfileViews()
            setEditMode(false)
            Toast.makeText(this@ProfileActivity, R.string.perfil_salvo, Toast.LENGTH_SHORT).show()
        }
    }

    private fun savePhoto(uri: Uri) {
        lifecycleScope.launch {
            val newPath = withContext(Dispatchers.IO) {
                runCatching {
                    val file = File(filesDir, "profile_${System.currentTimeMillis()}.jpg")
                    contentResolver.openInputStream(uri)!!.use { input ->
                        file.outputStream().use { output -> input.copyTo(output) }
                    }
                    file.absolutePath
                }.getOrNull()
            }

            if (newPath == null) {
                Toast.makeText(this@ProfileActivity, R.string.perfil_foto_erro, Toast.LENGTH_SHORT).show()
                return@launch
            }

            val oldPath = profile.photoPath
            profile = profile.copy(photoPath = newPath)
            db.profileDao().save(profile)
            oldPath?.let { withContext(Dispatchers.IO) { File(it).delete() } }
            showPhoto()
        }
    }

    private fun showPhoto() {
        val file = profile.photoPath?.let { File(it) }?.takeIf { it.exists() }
        if (file != null) {
            binding.imgAvatar.load(file)
        } else {
            binding.imgAvatar.setImageDrawable(null)
        }
    }

    private fun openGameDetail(game: Game) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_GAME_ID, game.id)
        startActivity(intent)
    }

    private fun loadGames() {
        lifecycleScope.launch {
            val dao = db.gameDao()
            val catalogCount = dao.countBySource(Game.SOURCE_MANUAL)
            val steamCount = dao.countBySource(Game.SOURCE_STEAM)

            binding.tvStatTotal.text = (catalogCount + steamCount).toString()
            binding.tvStatCatalogados.text = catalogCount.toString()
            binding.tvStatSteam.text = steamCount.toString()

            binding.tvQtdCatalogados.text = catalogCount.toString()
            binding.tvQtdSteam.text = steamCount.toString()

            catalogAdapter.submitList(dao.getTopBySource(Game.SOURCE_MANUAL, PREVIEW_LIMIT))
            steamAdapter.submitList(dao.getTopBySource(Game.SOURCE_STEAM, PREVIEW_LIMIT))

            binding.tvVazioCatalogados.isVisible = catalogCount == 0
            binding.recyclerCatalogados.isVisible = catalogCount > 0
            binding.tvVazioSteam.isVisible = steamCount == 0
            binding.recyclerSteam.isVisible = steamCount > 0
        }
    }
}