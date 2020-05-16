package com.danish.playbutton

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playButton.playButtonListener=listener

    }

    private val listener = object : PlayButton.OnButtonListener{
        override fun onPlayButtonClicked(playButton: PlayButton) {
                playButton.setAudioFileName("audio")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        playButton.stopAudio()
    }

    override fun onPause() {
        super.onPause()
        playButton.pauseAudio()
    }
}
