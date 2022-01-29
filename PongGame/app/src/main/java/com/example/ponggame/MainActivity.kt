package com.example.ponggame

import android.media.MediaPlayer
import android.net.Uri
import android.nfc.NdefRecord.createUri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.ponggame.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var mediaUri: MediaPlayer

    private var isPlaying: Boolean = false

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

        mediaUri = MediaPlayer.create(this, R.raw.main_theme)
        mediaUri.isLooping
    }

    override fun onRestart() {
        if(!isPlaying){
            mediaUri = MediaPlayer.create(this, R.raw.main_theme)
            mediaUri.isLooping
        }
        Log.d("MainActivity", "On Restart launched!")
        super.onRestart()
    }

    override fun onStart() {
        if(!isPlaying){
            mediaUri.start()
            isPlaying = true
        }
        Log.d("MainActivity", "On Start launched! ${isPlaying}")
        super.onStart()
    }

    override fun onPause() {
        Log.d("MainActivity", "On Pause launched!")
        super.onPause()
    }

    override fun onStop() {
        if(isPlaying){
            mediaUri.stop()
            isPlaying = false
        }
        Log.d("MainActivity", "On Stop launched! ${isPlaying}")
        super.onStop()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

}