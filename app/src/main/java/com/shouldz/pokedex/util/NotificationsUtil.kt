package com.shouldz.pokedex.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.shouldz.pokedex.MainActivity
import com.shouldz.pokedex.R

object NotificationUtils {

    private const val CHANNEL_ID = "pokemon_caught_channel"
    private const val CHANNEL_NAME = "Pokemon Caught"
    private const val NOTIFICATION_ID_PREFIX = "POKEMON_CAUGHT_"

    const val EXTRA_DESTINATION = "NAVIGATE_TO_DESTINATION"
    const val DESTINATION_CAUGHT_LIST = "caught_list"
    const val TAG = "NotificationUtils"

    fun createNotificationChannel(context: Context) {
        val descriptionText = "Notifications shown when a Pokemon is caught"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
            description = descriptionText
            lightColor = Color.RED
            enableVibration(true)
            vibrationPattern = longArrayOf(100, 200, 300)
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun sendPokemonCaughtNotification(context: Context, pokemonName: String) {

        // Permission Check
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // TIRAMISU is API 33
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.e(TAG, "POST_NOTIFICATIONS permission not granted. Cannot send notification.")
                Toast.makeText(context, "Notification permission needed", Toast.LENGTH_SHORT).show()
                return
            }
        }

        // Intent when notification is tapped
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(EXTRA_DESTINATION, DESTINATION_CAUGHT_LIST)
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            pokemonName.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_pokeball)
            .setContentTitle(
                context.getString(
                    R.string.notification_title,
                    capitalizeFirstLetter(pokemonName)
                )
            )
            .setContentText(
                context.getString(
                    R.string.notification_text,
                    capitalizeFirstLetter(pokemonName)
                )
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_EVENT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        val notificationId = (NOTIFICATION_ID_PREFIX + pokemonName).hashCode()

        try {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        } catch (e: SecurityException) {
            Log.e(TAG,"Missing POST_NOTIFICATIONS permission ${e.message}")
        }
    }
}
