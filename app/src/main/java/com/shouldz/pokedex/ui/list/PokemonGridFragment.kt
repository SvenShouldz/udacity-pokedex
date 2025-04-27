package com.shouldz.pokedex.ui.list

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
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
        setupSearchView()
        setupToolbar()
        setupListeners()
        observeViewModel()

        // Trigger initial data load
        if (viewModel.filteredPokemonList.value.isNullOrEmpty() && viewModel.error.value == null) {
            viewModel.loadPokemonList()
        }
    }

    private fun setupListeners() {
        binding.gridRetryButton.setOnClickListener {
            viewModel.retryLoadList()
        }
        binding.caughtButton.setOnClickListener {
            findNavController().navigate(R.id.action_pokemonGridFragment_to_caughtListFragment)
        }
        binding.searchViewCard.setOnClickListener {
            activateSearchView()
        }
    }

    private fun activateSearchView() {
        binding.searchView.isIconified = false // Expand the SearchView
        binding.searchView.requestFocus() // Request focus
        // Show the keyboard
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.showSoftInput(binding.searchView.findFocus(), InputMethodManager.SHOW_IMPLICIT)
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
            if (filteredList.isNotEmpty()) {
                pokemonAdapter.submitList(filteredList)
                // List has data, show RecyclerView, hide error view
                binding.pokemonRecyclerView.visibility = View.VISIBLE
                binding.searchView.visibility = View.VISIBLE
                binding.gridErrorView.visibility = View.GONE
                binding.searchEmptyText.visibility = View.GONE
            }else{
                // List is empty, hide RecyclerView, show error view
                binding.pokemonRecyclerView.visibility = View.GONE
                binding.searchEmptyText.visibility = View.VISIBLE
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Show/hide the spinner based on loading state
            binding.loadingSpinner.visibility = if (isLoading) View.VISIBLE else View.GONE
            if (isLoading) {
                // Hide everything else when loading
                binding.pokemonRecyclerView.visibility = View.GONE
                binding.searchView.visibility = View.GONE
                binding.gridErrorView.visibility = View.GONE
                binding.searchEmptyText.visibility = View.GONE
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                binding.gridErrorView.visibility = View.VISIBLE
                binding.gridErrorText.text = errorMessage
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                viewModel.onErrorShown()
            } else {
                // No error message and not loading
                if (viewModel.filteredPokemonList.value?.isNotEmpty() == true) {
                    binding.gridErrorView.visibility = View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.pokemonRecyclerView.adapter = null
        _binding = null // Avoid memory leaks
    }
}