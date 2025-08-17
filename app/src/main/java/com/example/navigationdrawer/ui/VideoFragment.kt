package com.example.navigationdrawer.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.MediaController
import android.widget.VideoView
import androidx.fragment.app.Fragment
import com.example.navigationdrawer.R

class VideoFragment : Fragment() {

    private lateinit var videoView: VideoView
    private val SKIP_TIME = 10000 // 10 seconds in milliseconds

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_video, container, false)
        videoView = view.findViewById(R.id.videoView)
        val rewindButton: Button = view.findViewById(R.id.rewindButton)
        val forwardButton: Button = view.findViewById(R.id.forwardButton)

        // Set up the default media controller for play/pause and seek bar
        val mediaController = MediaController(requireContext())
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)

        // Set the video path and start playing
        val uri = Uri.parse("android.resource://${requireActivity().packageName}/${R.raw.sample_video1}")
        videoView.setVideoURI(uri)
        videoView.start()

        // Set up custom skip buttons
        rewindButton.setOnClickListener {
            val currentPosition = videoView.currentPosition
            val newPosition = currentPosition - SKIP_TIME
            if (newPosition > 0) {
                videoView.seekTo(newPosition)
            } else {
                videoView.seekTo(0)
            }
        }

        forwardButton.setOnClickListener {
            val currentPosition = videoView.currentPosition
            val newPosition = currentPosition + SKIP_TIME
            if (newPosition < videoView.duration) {
                videoView.seekTo(newPosition)
            } else {
                videoView.seekTo(videoView.duration)
            }
        }

        return view
    }

    override fun onPause() {
        super.onPause()
        // Pause the video when the fragment is not visible
        videoView.pause()
    }
}