package com.shouldz.pokedex.ui.caught

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.shouldz.pokedex.R
import com.shouldz.pokedex.databinding.FragmentCaughtListBinding
import com.shouldz.pokedex.databinding.FragmentPokemonDetailBinding

class CaughtListFragment : Fragment() {

    private var _binding: FragmentCaughtListBinding? = null
    private val binding get() = _binding!! // Only valid between onCreateView and onDestroyView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCaughtListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.navigateListButton.setOnClickListener {
            findNavController().navigate(R.id.action_caughtListFragment_to_pokemonListFragment)
        }
        binding.navigateDetailButton.setOnClickListener {
            findNavController().navigate(R.id.action_caughtListFragment_to_pokemonDetailFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }

}