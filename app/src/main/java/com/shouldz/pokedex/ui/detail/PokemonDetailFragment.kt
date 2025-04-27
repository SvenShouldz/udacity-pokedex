package com.shouldz.pokedex.ui.detail

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar
import com.shouldz.pokedex.R
import com.shouldz.pokedex.data.model.PokemonDetailResponse
import com.shouldz.pokedex.databinding.FragmentPokemonDetailBinding
import com.shouldz.pokedex.util.NotificationUtils
import com.shouldz.pokedex.util.capitalizeFirstLetter
import com.shouldz.pokedex.util.formatStatsList
import com.shouldz.pokedex.util.formatTypeList
import java.util.Locale

class PokemonDetailFragment : Fragment() {

    private var _binding: FragmentPokemonDetailBinding? = null
    private val binding get() = _binding!! // Only valid between onCreateView and onDestroyView

    private val viewModel: PokemonDetailViewModel by viewModels()

    private var activityToolbar: MaterialToolbar? = null

    // Permission Request for Notifications
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permission granted!
                catchPokemonAndNotify()
            } else {
                // Permission denied!
                Toast.makeText(
                    context,
                    "Notification permission denied. Cannot show catch notifications.",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.onCatchReleaseClicked()
            }
        }

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
        activityToolbar = activity?.findViewById(R.id.main_toolbar)

        // Setup toolbar
        setupToolbar()
        // Setup button
        setupCatchReleaseButton()
        // Observe changes from the ViewModel
        observeViewModel()
        // Load the details for this Pokemon
        if (viewModel.pokemonDetail.value == null) {
            viewModel.loadPokemonDetail(args.pokemonName)
        }
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
            val isCurrentlyCaught = viewModel.isCaught.value ?: false // Get current state

            if (isCurrentlyCaught) {
                // If already caught, just release (no permission needed)
                viewModel.onCatchReleaseClicked()
            } else {
                // If not caught, check/request permission before catching
                checkAndRequestNotificationPermission()
            }
        }
    }

    private fun checkAndRequestNotificationPermission() {
        // Check only needed on Android 13 (API 33) and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                // Check if permission is already granted
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    catchPokemonAndNotify()
                }

                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }

                else -> {
                    // Directly ask for the permission
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            // Permission not needed for versions below Android 13
            catchPokemonAndNotify()
        }
    }

    private fun catchPokemonAndNotify() {
        viewModel.onCatchReleaseClicked()
        val pokemonName = args.pokemonName
        NotificationUtils.sendPokemonCaughtNotification(requireContext(), pokemonName)
    }

    private fun observeViewModel() {
        // Observer for loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.detailLoadingSpinner.visibility = if (isLoading) View.VISIBLE else View.GONE
            // When loading starts, hide content and error
            if (isLoading) {
                binding.pokemonErrorText.text = ""
                binding.contentGroup.visibility = View.INVISIBLE

            }
        }
        // Observer for Pokemon details (Success state)
        viewModel.pokemonDetail.observe(viewLifecycleOwner) { detail ->
            activityToolbar?.title = getString(R.string.finding_pokemon)
            if (detail != null) {
                // Success: Bind data, show content, hide error
                binding.pokemonErrorText.text = ""
                bindData(detail)
                binding.contentGroup.visibility = View.VISIBLE

                activityToolbar?.title = capitalizeFirstLetter(detail.name)
            }
        }

        // Observer for caught status
        viewModel.isCaught.observe(viewLifecycleOwner) { isCaught ->
            binding.catchReleaseButton.text = if (isCaught) {
                getString(R.string.release_pokemon)
            } else {
                getString(R.string.catch_pokemon)
            }
        }

        // Observer for error messages (Error state)
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                binding.pokemonErrorText.text = errorMessage
                binding.contentGroup.visibility = View.INVISIBLE

                activityToolbar?.title = getString(R.string.pokemon_not_found)
                // Show Toast
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
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
            detailPokemonStatsText.text = formatStatsList(detail.stats, offset = 0, count = 3)
            detailPokemonStatsSecondary.text = formatStatsList(detail.stats, offset = 3, count = 3)

            // Set Height (Convert decimetres to meters)
            val heightInMeters = detail.height / 10.0
            detailPokemonHeightText.text =
                String.format(Locale.getDefault(), "%.1f m", heightInMeters)

            // Set Weight (Convert hectograms to kilograms)
            val weightInKilograms = detail.weight / 10.0

            detailPokemonWeightText.text =
                String.format(Locale.getDefault(), "%.1f kg", weightInKilograms)

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }

}