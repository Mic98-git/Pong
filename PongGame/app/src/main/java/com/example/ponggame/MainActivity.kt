package com.example.ponggame

import android.media.MediaPlayer
import android.net.Uri
import android.nfc.NdefRecord.createUri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.ponggame.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var mediaUri: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // This ensures action bar (app bar) buttons, like the
        // menu option in LetterListFragment are visible.
        setupActionBarWithNavController(navController)
        /*
        mediaUri = MediaPlayer.create(this, R.raw.main_theme)
        mediaUri.isLooping
        mediaUri.start()
         */
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

}