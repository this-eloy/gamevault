package com.example.gamevault.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.gamevault.databinding.ItemGameBinding
import com.example.gamevault.model.Game

class GameAdapter(
    private var games: List<Game>,
    private val onItemClick: (Game) -> Unit
) : RecyclerView.Adapter<GameAdapter.GameViewHolder>() {

    inner class GameViewHolder(val binding: ItemGameBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val binding = ItemGameBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return GameViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
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