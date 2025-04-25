package com.shouldz.pokedex.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.appbar.MaterialToolbar
import com.shouldz.pokedex.R
import com.shouldz.pokedex.data.model.PokemonResult
import com.shouldz.pokedex.databinding.FragmentPokemonGridBinding
import com.shouldz.pokedex.ui.list.PokemonGridFragmentDirections.Companion.actionPokemonGridFragmentToPokemonDetailFragment

class PokemonGridFragment : Fragment() {

    private var _binding: FragmentPokemonGridBinding? = null
    private val binding get() = _binding!! // Only valid between onCreateView and onDestroyView

    private val viewModel: PokemonGridViewModel by viewModels()
    private lateinit var pokemonAdapter: PokemonGridAdapter

    private var activityToolbar: MaterialToolbar? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPokemonGridBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activityToolbar = activity?.findViewById(R.id.main_toolbar)

        setupRecyclerView()
        observeViewModel()
        setupSearchView()
        setupToolbar()

        // Trigger initial data load
        viewModel.loadPokemonList()

        binding.caughtButton.setOnClickListener {
            findNavController().navigate(R.id.action_pokemonGridFragment_to_caughtListFragment)
        }
    }

    private fun setupToolbar() {
        activityToolbar?.apply {
            title = getString(R.string.app_name)
            navigationIcon = null
            visibility = View.VISIBLE
        }
    }

    private fun setupRecyclerView() {
        pokemonAdapter = PokemonGridAdapter { clickedPokemon ->
            navigateToDetail(clickedPokemon)
        }
        binding.pokemonRecyclerView.apply {
            adapter = pokemonAdapter
            // Using GridLayoutManager for a grid layout
            layoutManager = GridLayoutManager(requireContext(), 3)
            setHasFixedSize(true)
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchView.clearFocus() // hide keyboard on submit
                return true
            }

            // Called every time the text in the SearchView changes
            override fun onQueryTextChange(newText: String?): Boolean {
                // Passing query to viewModel
                viewModel.setSearchQuery(newText.orEmpty())
                return true
            }
        })
    }

    private fun navigateToDetail(pokemon: PokemonResult) {
        // Using Safe Args
        val action = PokemonGridFragmentDirections
            .actionPokemonGridFragmentToPokemonDetailFragment(pokemon.name)
        findNavController().navigate(action)
    }

    private fun observeViewModel() {
        viewModel.filteredPokemonList.observe(viewLifecycleOwner) { filteredList ->
            // Submit list to adapter when it changes
            pokemonAdapter.submitList(filteredList)
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