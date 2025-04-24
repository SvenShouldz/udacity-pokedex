package com.shouldz.pokedex.ui.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.shouldz.pokedex.R
import com.shouldz.pokedex.databinding.FragmentPokemonDetailBinding
import com.shouldz.pokedex.databinding.FragmentPokemonListBinding

class PokemonDetailFragment : Fragment() {

    private var _binding: FragmentPokemonDetailBinding? = null
    private val binding get() = _binding!! // Only valid between onCreateView and onDestroyView

    // Safe Args
    private val args: PokemonDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPokemonDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pokemonName = args.pokemonName

        // Set the text of the TextView (capitalize the name)
        binding.detailPokemonNameText.text = pokemonName.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase() else it.toString()
        }
        // TODO: Fetch full details from API later
        // TODO: Setup catch button logic later
        binding.navigateListButton.setOnClickListener {
            findNavController().navigate(R.id.action_pokemonDetailFragment_to_pokemonListFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }

}