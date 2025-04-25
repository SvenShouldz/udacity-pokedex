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
import com.bumptech.glide.Glide
import com.shouldz.pokedex.R
import com.shouldz.pokedex.data.model.PokemonDetailResponse
import com.shouldz.pokedex.databinding.FragmentPokemonDetailBinding
import java.util.Locale

class PokemonDetailFragment : Fragment() {

    private var _binding: FragmentPokemonDetailBinding? = null
    private val binding get() = _binding!! // Only valid between onCreateView and onDestroyView

    private val viewModel: PokemonDetailViewModel by viewModels()

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

        // Observe changes from the ViewModel
        observeViewModel()

        // load the details for this Pokemon
        viewModel.loadPokemonDetail(pokemonName)

        binding.navigateListButton.setOnClickListener {
            findNavController().navigate(R.id.action_pokemonDetailFragment_to_pokemonListFragment)
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
            } else {
                if (viewModel.isLoading.value == false) {
                    binding.contentGroup.visibility =
                        View.INVISIBLE
                }
            }
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
            detailPokemonNameText.text = detail.name.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase() else it.toString()
            }

            // Load Image using Glide
            Glide.with(this@PokemonDetailFragment)
                .load(detail.sprites.frontDefault) // Get URL from sprites object
                .placeholder(R.drawable.ic_launcher_background) // TODO: Replace with a proper placeholder drawable
                .error(R.drawable.ic_launcher_foreground) // TODO: Replace with a proper error drawable
                .into(detailPokemonImage)


            detailPokemonTypesText.text = detail.types.joinToString(" / ") { typeSlot ->
                typeSlot.type.name.replaceFirstChar { it.titlecase() }
            }

            // Set Stats
            detailPokemonStatsText.text = detail.stats.joinToString("\n") { statSlot ->
                val name = formatStatName(statSlot.stat.name)
                val value = statSlot.baseStat
                "$name: $value"
            }

            // Set Height (Convert decimetres to meters)
            val heightInMeters = detail.height / 10.0
            detailPokemonHeightText.text = String.format(Locale.getDefault(), "%.1f m", heightInMeters)

            // Set Weight (Convert hectograms to kilograms)
            val weightInKilograms = detail.weight / 10.0

            detailPokemonWeightText.text = String.format(Locale.getDefault(), "%.1f kg", weightInKilograms)

            // TODO: Update Catch/Release button state based on local DB later
        }
    }

    private fun formatStatName(rawStatName: String): String {
        return rawStatName.split("-").joinToString(" ") { word ->
            word.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }

}