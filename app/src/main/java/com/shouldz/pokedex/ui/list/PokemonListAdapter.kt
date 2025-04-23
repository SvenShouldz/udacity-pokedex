package com.shouldz.pokedex.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.shouldz.pokedex.data.model.PokemonResult
import com.shouldz.pokedex.databinding.ListItemPokemonBinding

class PokemonListAdapter : ListAdapter<PokemonResult, PokemonListAdapter.PokemonViewHolder>(PokemonDiffCallback()) {

    // ViewHolder class
    class PokemonViewHolder(private val binding: ListItemPokemonBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(pokemonResult: PokemonResult) {
            binding.pokemonNameText.text = pokemonResult.name.replaceFirstChar { it.titlecase() }
            // TODO: Set click listener later for navigation
            // TODO: Load image using Glide later
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        val binding = ListItemPokemonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PokemonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        val currentPokemon = getItem(position)
        holder.bind(currentPokemon)
    }

    // DiffUtil Callback for ListAdapter
    class PokemonDiffCallback : DiffUtil.ItemCallback<PokemonResult>() {
        override fun areItemsTheSame(oldItem: PokemonResult, newItem: PokemonResult): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: PokemonResult, newItem: PokemonResult): Boolean {
            return oldItem.name == newItem.name
        }
    }
}