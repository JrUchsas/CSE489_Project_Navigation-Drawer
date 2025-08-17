package com.example.navigationdrawer.ui

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.navigationdrawer.R

class ImageScaleFragment : Fragment() {

    private lateinit var imageView: ZoomableImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_image_scale, container, false)
        imageView = view.findViewById(R.id.zoomableImageView)

        // Load the image directly from the raw resources
        val inputStream = resources.openRawResource(R.raw.sample_image1)
        val image = BitmapFactory.decodeStream(inputStream)

        // Use a post block to ensure the view has been measured before centering the image
        imageView.post {
            imageView.setImageAndCenter(image)
        }

        return view
    }
}
