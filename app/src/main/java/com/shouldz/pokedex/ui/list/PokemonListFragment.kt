package com.shouldz.pokedex.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.shouldz.pokedex.R
import com.shouldz.pokedex.databinding.FragmentPokemonListBinding

class PokemonListFragment : Fragment() {

    private var _binding: FragmentPokemonListBinding? = null
    private val binding get() = _binding!! // Only valid between onCreateView and onDestroyView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPokemonListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.navigateDetailButton.setOnClickListener {
            findNavController().navigate(R.id.action_pokemonListFragment_to_pokemonDetailFragment)
        }
        binding.navigateCaughtButton.setOnClickListener {
            findNavController().navigate(R.id.action_pokemonListFragment_to_caughtListFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }
}