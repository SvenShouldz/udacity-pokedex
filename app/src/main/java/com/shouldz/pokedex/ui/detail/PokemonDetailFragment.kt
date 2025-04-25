package com.shouldz.pokedex.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.appbar.MaterialToolbar
import com.shouldz.pokedex.R
import com.shouldz.pokedex.data.model.PokemonDetailResponse
import com.shouldz.pokedex.databinding.FragmentPokemonDetailBinding
import com.shouldz.pokedex.util.capitalizeFirstLetter
import com.shouldz.pokedex.util.formatStatName
import com.shouldz.pokedex.util.formatStatsList
import com.shouldz.pokedex.util.formatTypeList
import java.util.Locale

class PokemonDetailFragment : Fragment() {

    private var _binding: FragmentPokemonDetailBinding? = null
    private val binding get() = _binding!! // Only valid between onCreateView and onDestroyView

    private val viewModel: PokemonDetailViewModel by viewModels()

    private var activityToolbar: MaterialToolbar? = null

    // Safe Args
    private val args: PokemonDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPokemonDetailBinding.inflate(inflater, container, false)
        // Set Placeholder Image
        _binding?.detailPokemonImage?.setImageResource(R.drawable.ic_pokeball_placeholder)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pokemonName = args.pokemonName

        activityToolbar = activity?.findViewById(R.id.main_toolbar)

        // Setup toolbar
        setupToolbar()

        // Setup button
        setupCatchReleaseButton()

        // Observe changes from the ViewModel
        observeViewModel()

        // Load the details for this Pokemon
        viewModel.loadPokemonDetail(pokemonName)
    }

    private fun setupToolbar() {
        activityToolbar?.let { toolbar ->
            val navController = findNavController()
            val appBarConfiguration = AppBarConfiguration(navController.graph)
            toolbar.setupWithNavController(navController, appBarConfiguration)
            toolbar.visibility = View.VISIBLE
        }
    }

    private fun setupCatchReleaseButton() {
        binding.catchReleaseButton.setOnClickListener {
            viewModel.onCatchReleaseClicked()
        }
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.detailLoadingSpinner.visibility = if (isLoading) View.VISIBLE else View.GONE
            // Hide content while loading
            binding.contentGroup.visibility = if (isLoading) View.INVISIBLE else View.VISIBLE
        }

        // Observe Pokemon details
        viewModel.pokemonDetail.observe(viewLifecycleOwner) { detail ->
            if (detail != null) {
                bindData(detail)
                binding.contentGroup.visibility = View.VISIBLE
                activityToolbar?.title = capitalizeFirstLetter(detail.name)
            } else {
                if (viewModel.isLoading.value == false) {
                    binding.contentGroup.visibility =
                        View.INVISIBLE
                    // Reset image to placeholder if details fail to load after trying
                    binding.detailPokemonImage.setImageResource(R.drawable.ic_pokeball_placeholder)
                }
                activityToolbar?.title = getString(R.string.pokemon_not_found)
            }
        }

        viewModel.isCaught.observe(viewLifecycleOwner) { isCaught ->
            // Update button text based on caught status
            binding.catchReleaseButton.text = if (isCaught) {
                getString(R.string.release_pokemon)
            } else {
                getString(R.string.catch_pokemon)
            }
            // TODO: Add different color states
        }

        // Observe error messages
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                // simple Toast message for the error
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                viewModel.onErrorShown()
            }
        }
    }

    private fun bindData(detail: PokemonDetailResponse) {
        binding.apply {
            // Set Name (Capitalized)
            detailPokemonNameText.text = capitalizeFirstLetter(detail.name)

            // Load Image using Glide
            Glide.with(this@PokemonDetailFragment)
                .load(detail.sprites.frontDefault) // Get URL from sprites object
                .error(R.drawable.ic_pokeball_placeholder)
                .into(detailPokemonImage)

            // Set Types
            detailPokemonTypesText.text = formatTypeList(detail.types)

            // Set Stats
            detailPokemonStatsText.text = formatStatsList(detail.stats)

            // Set Height (Convert decimetres to meters)
            val heightInMeters = detail.height / 10.0
            detailPokemonHeightText.text = String.format(Locale.getDefault(), "%.1f m", heightInMeters)

            // Set Weight (Convert hectograms to kilograms)
            val weightInKilograms = detail.weight / 10.0

            detailPokemonWeightText.text = String.format(Locale.getDefault(), "%.1f kg", weightInKilograms)

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }

}