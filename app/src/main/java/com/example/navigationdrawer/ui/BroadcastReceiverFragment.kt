package com.example.navigationdrawer.ui

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.navigationdrawer.R

class BroadcastReceiverFragment : Fragment() {

    private lateinit var spinner: Spinner
    private lateinit var editText: EditText
    private lateinit var button: Button
    private lateinit var batteryLevel: TextView

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(requireContext(), "Notification permission granted", Toast.LENGTH_SHORT).show()
                sendCustomBroadcast()
            } else {
                Toast.makeText(requireContext(), "Notification permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    private val customReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val message = intent?.getStringExtra("message")
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
            if (level != -1 && scale != -1) {
                val batteryPct = level / scale.toFloat()
                batteryLevel.text = "Battery Level: ${"%.0f".format(batteryPct * 100)}%"
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_broadcast_receiver, container, false)

        spinner = view.findViewById(R.id.spinner)
        editText = view.findViewById(R.id.editText)
        button = view.findViewById(R.id.button)
        batteryLevel = view.findViewById(R.id.batteryLevel)

        val options = arrayOf("Custom", "Battery Notification")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, options)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0) { // Custom
                    editText.visibility = View.VISIBLE
                    button.visibility = View.VISIBLE
                    batteryLevel.visibility = View.GONE
                } else { // Battery Notification
                    editText.visibility = View.GONE
                    button.visibility = View.GONE
                    batteryLevel.visibility = View.VISIBLE
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                editText.visibility = View.VISIBLE
                button.visibility = View.VISIBLE
                batteryLevel.visibility = View.GONE
            }
        }

        button.setOnClickListener {
            if (spinner.selectedItemPosition == 0) {
                checkNotificationPermissionAndBroadcast()
            }
        }

        // Ensure the initial state is correct
        spinner.setSelection(0)

        return view
    }

    private fun checkNotificationPermissionAndBroadcast() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    sendCustomBroadcast()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    Toast.makeText(requireContext(), "Notification permission is needed to show custom alerts.", Toast.LENGTH_LONG).show()
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            sendCustomBroadcast()
        }
    }

    private fun sendCustomBroadcast() {
        val intent = Intent("com.example.cse489assignment.CUSTOM_BROADCAST")
        intent.putExtra("message", editText.text.toString())
        intent.setPackage(requireActivity().packageName)
        requireActivity().sendBroadcast(intent)

        // Add a confirmation toast
        Toast.makeText(requireContext(), "Message Sent", Toast.LENGTH_SHORT).show()

        // Clear the text field after sending for better user experience
        editText.text.clear()
    }

    override fun onResume() {
        super.onResume()
        // Register custom receiver
        val customFilter = IntentFilter("com.example.cse489assignment.CUSTOM_BROADCAST")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireActivity().registerReceiver(customReceiver, customFilter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            requireActivity().registerReceiver(customReceiver, customFilter)
        }

        // Register battery receiver
        val batteryFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireActivity().registerReceiver(batteryReceiver, batteryFilter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            requireActivity().registerReceiver(batteryReceiver, batteryFilter)
        }
    }

    override fun onPause() {
        super.onPause()
        requireActivity().unregisterReceiver(customReceiver)
        requireActivity().unregisterReceiver(batteryReceiver)
    }
}
