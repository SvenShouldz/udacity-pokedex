package com.shouldz.pokedex.ui.caught

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.shouldz.pokedex.R
import com.shouldz.pokedex.databinding.FragmentCaughtListBinding

class CaughtListFragment : Fragment() {

    private var _binding: FragmentCaughtListBinding? = null
    private val binding get() = _binding!! // Only valid between onCreateView and onDestroyView

    private val viewModel: CaughtListViewModel by viewModels()

    private lateinit var caughtAdapter: CaughtListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCaughtListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupToolbar() {
        val navController = findNavController()
        binding.caughtListToolbar.setupWithNavController(navController)
        binding.caughtListToolbar.setTitle(R.string.caught_pokemon_title)
    }

    private fun setupRecyclerView() {
        // Initialize the adapter
        caughtAdapter = CaughtListAdapter()

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }

}