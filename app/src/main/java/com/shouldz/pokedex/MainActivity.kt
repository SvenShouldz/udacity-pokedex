package com.shouldz.pokedex

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.shouldz.pokedex.databinding.ActivityMainBinding
import com.shouldz.pokedex.util.NotificationUtils

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        NotificationUtils.createNotificationChannel(applicationContext)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        navController = navHostFragment.navController
        handleIntent(intent)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.hasExtra(NotificationUtils.EXTRA_DESTINATION) == true) {
            val destination = intent.getStringExtra(NotificationUtils.EXTRA_DESTINATION)
            if (destination == NotificationUtils.DESTINATION_CAUGHT_LIST) {
                if (navController.currentDestination?.id != R.id.caughtListFragment) {
                    navController.navigate(R.id.action_global_caughtListFragment)
                }
                intent.removeExtra(NotificationUtils.EXTRA_DESTINATION)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

}