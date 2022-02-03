package com.example.ponggame.game.ui

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.res.Configuration
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.example.ponggame.DatabaseImpl
import com.example.ponggame.R
import com.example.ponggame.game.model.Player
import com.example.ponggame.game.model.PongTable
import com.example.ponggame.game.utils.GameThread
import com.example.ponggame.game.utils.STATE_PAUSED
import com.example.ponggame.game.utils.STATE_RUNNING
import kotlin.math.pow

class PongActivity : AppCompatActivity(), SensorEventListener {

    // Game elements
    private var mGameThread: GameThread? = null
    private lateinit var table: PongTable
    private lateinit var player: Player
    private var dx: Float = 0f

    // Sensor elements
    private lateinit var sensorManager: SensorManager
    private lateinit var accelSensor: Sensor
    var currentTime: Long = 0
    var previousTime: Long = 0
    var currentAcceleration: Float = 0f
    var previousAcceleration: Float = 0f

    // Variables to handle buttons
    private lateinit var toggleButton: ToggleButton
    var flag: Boolean = false
    private lateinit var quitButton: ImageButton

    // Alert
    private lateinit var builder: AlertDialog.Builder

    // Song & sound effects
    private lateinit var mediaUri: MediaPlayer
    private var isPlaying: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("DEPRECATION")
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_pong)
        toggleButton = findViewById(R.id.play_pause_button)
        quitButton = findViewById(R.id.quit_game_button)

        builder = AlertDialog.Builder(this)
        if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK === Configuration.UI_MODE_NIGHT_YES) {
            builder.setTitle(HtmlCompat.fromHtml("<font color='#ffffff'>Quit game</font>", HtmlCompat.FROM_HTML_MODE_LEGACY))
        }
        else {
            builder.setTitle(HtmlCompat.fromHtml("<font color='#000000'>Quit game</font>",
                HtmlCompat.FROM_HTML_MODE_LEGACY))
        }
        builder.setMessage("Do you want to quit?")

        builder.setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()
            mGameThread!!.setState(STATE_PAUSED)
            toggleButton.isChecked = true
            flag = true
            toggleButton.setBackgroundResource(0)
            toggleButton.setBackgroundResource(R.drawable.ic_play_50)
        })

        builder.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
            onBackPressed()
        })

        
        //playPauseHandler(it)
        toggleButton.setOnClickListener {
            if (!flag) {
                toggleButton.isChecked = true
                flag = true
                toggleButton.setBackgroundResource(0)
                toggleButton.setBackgroundResource(R.drawable.ic_play_50)
            } else {
                toggleButton.isChecked = false
                flag = false
                toggleButton.setBackgroundResource(0)
                toggleButton.setBackgroundResource(R.drawable.ic_pause_50)
            }
        }

        toggleButton.setOnCheckedChangeListener { _, isChecked ->
            if (toggleButton.isChecked) { // isChecked = true
                mGameThread!!.setState(STATE_PAUSED)
            } else {
                mGameThread!!.setState(STATE_RUNNING)
            }
        }

        quitButton.setOnClickListener {
            mGameThread!!.setState(STATE_PAUSED)
            val alert = builder.create()
            alert.setCancelable(false)
            alert.show()
        }

        // Sensor body
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)


        table = findViewById<View>(R.id.pongTable) as PongTable
        table.setScoreOpponent(findViewById<View>(R.id.tvScoreOpponent) as TextView)
        table.setScorePlayer(findViewById<View>(R.id.tvScorePlayer) as TextView)
        table.setStatus(findViewById<View>(R.id.tvStatus) as TextView)

        mGameThread = table.game

        // Start song
        mediaUri = MediaPlayer.create(this, R.raw.pong_theme)
        mediaUri.isLooping

    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    /**
     * These are the functions to be implemented
     * for the SensorEventListener class
     * */
    override fun onSensorChanged(sensorEvent: SensorEvent) {
        player = table.player!!
        // >0 goes to left
        this.currentAcceleration = sensorEvent.values[0]
        this.currentTime = sensorEvent.timestamp


        if (this.mGameThread?.getIntState() == STATE_RUNNING) {
            this.table.movePlayerRacquet(
                -computeDx(
                    this.currentAcceleration, this.currentTime
                ) / 899990000000000, // 12 zeroes
                player
            )
        }

        //val view = findViewById<View>(R.id.pong_constraint_layout)
        //view.findViewById<TextView>(R.id.main_text).text = xAxisValue.toString()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    fun nanosecToSec(nanoseconds: Long): Float {
        return (nanoseconds / 1000000).toFloat()
    }

    fun computeDx(currentAcc: Float, currentT: Long): Float {
        dx = currentAcc * nanosecToSec(currentT).pow(2) -
                previousAcceleration * nanosecToSec(previousTime).pow(2)
        this.previousAcceleration = currentAcc
        this.previousTime = currentT
        return dx
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onRestart() {
        if(!isPlaying){
            mediaUri = MediaPlayer.create(this, R.raw.main_theme)
            mediaUri.isLooping
        }
        Log.d("PongActivity", "On Restart launched!")
        super.onRestart()
    }

    override fun onStart() {
        if(!isPlaying){
            mediaUri.start()
            isPlaying = true
        }
        Log.d("PongActivity", "On Start launched! ${isPlaying}")
        super.onStart()
    }

    override fun onPause() {
        if(isPlaying){
            mediaUri.stop()
            isPlaying = false
        }
        Log.d("PongActivity", "On Pause launched! ${isPlaying}")
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onBackPressed() {
        DatabaseImpl.updateUserScore(this.table.getUserScore())
        super.onBackPressed()
    }

}