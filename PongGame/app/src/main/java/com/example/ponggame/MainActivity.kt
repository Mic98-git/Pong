package com.example.ponggame

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.net.Uri
import android.nfc.NdefRecord.createUri
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.ponggame.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var mediaUri: MediaPlayer
    lateinit var googleSignInClient: GoogleSignInClient

    private var isPlaying: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("DEPRECATION")
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // This ensures action bar (app bar) buttons, like the
        // menu option in LetterListFragment are visible.
        setupActionBarWithNavController(navController)
        supportActionBar!!.setBackgroundDrawable(
            ColorDrawable(Color.parseColor("#9243FF"))
        )

        mediaUri = MediaPlayer.create(this, R.raw.main_theme)
        mediaUri.isLooping

        /* Configure Google Sign In
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("502933370178-rhnf5ms09rn1icf3j0kfp82sj6qv8t5q.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)*/
    }

    override fun onRestart() {
        if (!isPlaying) {
            mediaUri = MediaPlayer.create(this, R.raw.main_theme)
            mediaUri.isLooping
        }
        Log.d("MainActivity", "On Restart launched!")
        super.onRestart()
    }

    override fun onStart() {
        if (!isPlaying) {
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
        if (isPlaying) {
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