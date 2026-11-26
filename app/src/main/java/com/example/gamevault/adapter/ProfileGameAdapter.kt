package com.example.gamevault.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.gamevault.databinding.ItemGameMiniBinding
import com.example.gamevault.model.Game

// card compacto para as listas horizontais do perfil
class ProfileGameAdapter(
    private val onItemClick: (Game) -> Unit
) : RecyclerView.Adapter<ProfileGameAdapter.VH>() {

    private var games: List<Game> = emptyList()

    inner class VH(val binding: ItemGameMiniBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemGameMiniBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        val game = games[position]
        holder.binding.txtGameName.text = game.name
        holder.binding.imgGame.load(game.imageUri)
        holder.itemView.setOnClickListener { onItemClick(game) }
    }

    override fun getItemCount() = games.size

    fun submitList(newList: List<Game>) {
        games = newList
        notifyDataSetChanged()
    }
}