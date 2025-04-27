package com.shouldz.pokedex.ui.caught

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.MaterialToolbar
import com.shouldz.pokedex.R
import com.shouldz.pokedex.data.local.CaughtPokemon
import com.shouldz.pokedex.data.model.PokemonResult
import com.shouldz.pokedex.databinding.FragmentCaughtListBinding

class CaughtListFragment : Fragment() {

    private var _binding: FragmentCaughtListBinding? = null
    private val binding get() = _binding!! // Only valid between onCreateView and onDestroyView

    private val viewModel: CaughtListViewModel by viewModels()

    private lateinit var caughtAdapter: CaughtListAdapter

    private var activityToolbar: MaterialToolbar? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCaughtListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activityToolbar = activity?.findViewById(R.id.main_toolbar)

        setupToolbar()
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupToolbar() {
        activityToolbar?.let { toolbar ->
            val navController = findNavController()
            val appBarConfiguration = AppBarConfiguration(navController.graph)
            toolbar.setupWithNavController(navController, appBarConfiguration)
            toolbar.title = getString(R.string.caught_pokemon_title)
            toolbar.visibility = View.VISIBLE
        }
    }

    private fun setupRecyclerView() {
        // Initialize the adapter
        caughtAdapter = CaughtListAdapter { clickedPokemon ->
            navigateToDetail(clickedPokemon)
        }

        binding.caughtPokemonRecyclerView.apply {
            adapter = caughtAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun observeViewModel() {
        viewModel.caughtPokemonList.observe(viewLifecycleOwner) { caughtList ->
            caughtAdapter.submitList(caughtList)

            binding.emptyListText.visibility = if (caughtList.isNullOrEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }

            binding.caughtPokemonRecyclerView.visibility = if (caughtList.isNullOrEmpty()) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }
    }

    private fun navigateToDetail(pokemon: CaughtPokemon) {
        // Using Safe Args
        val action = CaughtListFragmentDirections
            .actionCaughtListFragmentToPokemonDetailFragment(pokemon.name)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }

}