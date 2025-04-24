package com.shouldz.pokedex.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.shouldz.pokedex.data.model.PokemonResult
import com.shouldz.pokedex.databinding.FragmentPokemonListBinding

class PokemonListFragment : Fragment() {

    private var _binding: FragmentPokemonListBinding? = null
    private val binding get() = _binding!! // Only valid between onCreateView and onDestroyView

    private val viewModel: PokemonListViewModel by viewModels()
    private lateinit var pokemonAdapter: PokemonListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPokemonListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()

        // Trigger initial data load
        viewModel.loadPokemonList()

    }

    private fun setupRecyclerView() {
        pokemonAdapter = PokemonListAdapter { clickedPokemon ->
            navigateToDetail(clickedPokemon)
        }
        binding.pokemonRecyclerView.apply {
            adapter = pokemonAdapter
            // Using GridLayoutManager for a grid layout
            layoutManager = GridLayoutManager(requireContext(), 3)
        }
    }

    private fun navigateToDetail(pokemon: PokemonResult) {
        // Using Safe Args
        val action = PokemonListFragmentDirections
            .actionPokemonListFragmentToPokemonDetailFragment(pokemon.name) // Pass the name
        findNavController().navigate(action)
    }

    private fun observeViewModel() {
        viewModel.pokemonList.observe(viewLifecycleOwner) { pokemonList ->
            // Submit list to adapter when it changes
            pokemonAdapter.submitList(pokemonList)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Show/hide the progress bar based on loading state
            val visibility = if (isLoading) {
                View.VISIBLE
            } else {
                View.GONE
            }
            binding.loadingSpinner.visibility = visibility
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }
}