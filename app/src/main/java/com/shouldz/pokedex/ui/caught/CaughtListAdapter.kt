package com.shouldz.pokedex.ui.caught

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.shouldz.pokedex.R
import com.shouldz.pokedex.data.local.CaughtPokemon
import com.shouldz.pokedex.data.model.PokemonResult
import com.shouldz.pokedex.databinding.ListItemCaughtPokemonBinding
import com.shouldz.pokedex.util.capitalizeFirstLetter

class CaughtListAdapter(private val onItemClicked: (CaughtPokemon) -> Unit) : ListAdapter<CaughtPokemon, CaughtListAdapter.CaughtPokemonViewHolder>(CaughtPokemonDiffCallback()) {

    class CaughtPokemonViewHolder(private val binding: ListItemCaughtPokemonBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(caughtPokemon: CaughtPokemon, listener: (CaughtPokemon) -> Unit) {
            binding.apply {
                // Set the Pokemon name (capitalized)
                caughtPokemonNameText.text = capitalizeFirstLetter(caughtPokemon.name)

                // Load the sprite image using Glide
                Glide.with(itemView.context)
                    .load(caughtPokemon.spriteUrl)
                    .placeholder(R.drawable.ic_pokeball_placeholder)
                    .error(R.drawable.ic_pokeball_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(caughtPokemonSpriteImage)
            }
            binding.root.setOnClickListener {
                listener(caughtPokemon)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CaughtPokemonViewHolder {
        val binding = ListItemCaughtPokemonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CaughtPokemonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CaughtPokemonViewHolder, position: Int) {
        val currentPokemon = getItem(position)
        holder.bind(currentPokemon, onItemClicked)
    }

    class CaughtPokemonDiffCallback : DiffUtil.ItemCallback<CaughtPokemon>() {
        override fun areItemsTheSame(oldItem: CaughtPokemon, newItem: CaughtPokemon): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: CaughtPokemon, newItem: CaughtPokemon): Boolean {
            return oldItem == newItem
        }
    }
}
