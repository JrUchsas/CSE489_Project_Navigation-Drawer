package com.example.navigationdrawer.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.navigationdrawer.R

class AudioFragment : Fragment() {

    private lateinit var mediaPlayer: MediaPlayer
    private val SKIP_TIME = 10000 // 10 seconds in milliseconds

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_audio, container, false)

        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.sample_audio3)

        val playButton: Button = view.findViewById(R.id.playButton)
        val pauseButton: Button = view.findViewById(R.id.pauseButton)
        val rewindButton: Button = view.findViewById(R.id.rewindButton)
        val forwardButton: Button = view.findViewById(R.id.forwardButton)

        playButton.setOnClickListener {
            mediaPlayer.start()
        }

        pauseButton.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
            }
        }

        rewindButton.setOnClickListener {
            val currentPosition = mediaPlayer.currentPosition
            val newPosition = currentPosition - SKIP_TIME
            if (newPosition > 0) {
                mediaPlayer.seekTo(newPosition)
            } else {
                mediaPlayer.seekTo(0)
            }
        }

        forwardButton.setOnClickListener {
            val currentPosition = mediaPlayer.currentPosition
            val newPosition = currentPosition + SKIP_TIME
            if (newPosition < mediaPlayer.duration) {
                mediaPlayer.seekTo(newPosition)
            } else {
                mediaPlayer.seekTo(mediaPlayer.duration)
            }
        }

        return view
    }

    override fun onStop() {
        super.onStop()
        if (this::mediaPlayer.isInitialized) {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
            mediaPlayer.release()
        }
    }
}
