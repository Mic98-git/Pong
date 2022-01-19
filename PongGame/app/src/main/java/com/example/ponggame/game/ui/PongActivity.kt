package com.example.ponggame.game.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.ponggame.R
import com.example.ponggame.game.model.PongTable
import com.example.ponggame.game.utils.GameThread

class PongActivity : AppCompatActivity() {
    private var mGameThread: GameThread? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pong)


        val table: PongTable = findViewById<View>(R.id.pongTable) as PongTable
        table.setScoreOpponent(findViewById<View>(R.id.tvScoreOpponent) as TextView)
        table.setScorePlayer(findViewById<View>(R.id.tvScorePlayer) as TextView)
        table.setStatus(findViewById<View>(R.id.tvStatus) as TextView)


        mGameThread = table.game
    }

}