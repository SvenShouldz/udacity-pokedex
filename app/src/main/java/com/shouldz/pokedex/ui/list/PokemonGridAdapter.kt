package com.shouldz.pokedex.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.shouldz.pokedex.R
import com.shouldz.pokedex.data.model.PokemonResult
import com.shouldz.pokedex.databinding.ListItemPokemonBinding

class PokemonGridAdapter(private val onItemClicked: (PokemonResult) -> Unit) :
    ListAdapter<PokemonResult, PokemonGridAdapter.PokemonViewHolder>(PokemonDiffCallback()) {

    // ViewHolder class
    class PokemonViewHolder(private val binding: ListItemPokemonBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(pokemonResult: PokemonResult, listener: (PokemonResult) -> Unit) {
            binding.pokemonNameText.text = pokemonResult.name.replaceFirstChar { it.titlecase() }

            // Extracting pokemonId & add to URL string (due to PokeAPIs endpoint limitations)
            val pokemonId = pokemonResult.url.split("/").filter { it.isNotEmpty() }.lastOrNull()
            val spriteUrl = if (pokemonId != null) {
                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$pokemonId.png"
            } else {
                null
            }

            Glide.with(itemView.context)
                .load(spriteUrl)
                .placeholder(R.drawable.ic_pokeball_placeholder)
                .error(R.drawable.ic_pokeball_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.pokemonSpriteImage)


            binding.root.setOnClickListener {
                listener(pokemonResult)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        val binding =
            ListItemPokemonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PokemonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        val currentPokemon = getItem(position)
        holder.bind(currentPokemon, onItemClicked)
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