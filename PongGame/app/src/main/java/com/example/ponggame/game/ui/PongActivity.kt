package com.example.ponggame.game.ui

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ponggame.R
import com.example.ponggame.game.model.Player
import com.example.ponggame.game.model.PongTable
import com.example.ponggame.game.utils.GameThread
import com.example.ponggame.game.utils.PHY_RACQUET_SPEED
import com.example.ponggame.game.utils.STATE_RUNNING

class PongActivity : AppCompatActivity(), SensorEventListener {
    private var mGameThread: GameThread? = null

    lateinit var table: PongTable
    private  var xAxisValue: Float = 0f
    private lateinit var player: Player

    // Sensor elements
    private lateinit var sensorManager: SensorManager
    private lateinit var accelSensor: Sensor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pong)


        table = findViewById<View>(R.id.pongTable) as PongTable
        table.setScoreOpponent(findViewById<View>(R.id.tvScoreOpponent) as TextView)
        table.setScorePlayer(findViewById<View>(R.id.tvScorePlayer) as TextView)
        table.setStatus(findViewById<View>(R.id.tvStatus) as TextView)


        mGameThread = table.game

        // Sensor body
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    /**
     * These are the functions to be implemented
     * for the SensorEventListener class
     * */
    override fun onSensorChanged(sensorEvent: SensorEvent) {
        player = table.player!!
        // >0 goes to left
         xAxisValue = sensorEvent.values[0]

        //val view = findViewById<View>(R.id.pong_constraint_layout)
        //view.findViewById<TextView>(R.id.main_text).text = xAxisValue.toString()

        if(this.mGameThread?.getIntState() == STATE_RUNNING) {
            this.table.movePlayer(
                player,
                player!!.bounds.left - (xAxisValue * 200f),
                player!!.bounds.top
            )
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

}