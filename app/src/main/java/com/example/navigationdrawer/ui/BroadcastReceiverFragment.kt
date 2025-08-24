package com.example.navigationdrawer.ui

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
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
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.navigationdrawer.R

class BroadcastReceiverFragment : Fragment() {

    // UI elements
    private lateinit var spinner: Spinner // Keep if still used for other purposes, otherwise remove
    private lateinit var idInputEditText: EditText
    
    private lateinit var actualBatteryTextView: TextView
    private lateinit var calculatedBatteryTextView: TextView

    // Battery related
    private var currentActualBatteryPercentage: Int = 0 // Store actual battery percentage

    // Notification constants
    private val NOTIFICATION_CHANNEL_ID = "battery_notification_channel"
    private val NOTIFICATION_ID = 101

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(requireContext(), "Notification permission granted", Toast.LENGTH_SHORT).show()
                showBatteryNotification() // Try showing notification after permission
            } else {
                Toast.makeText(requireContext(), "Notification permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    // Existing customReceiver (keep as is if still needed for other functionality)
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
                val batteryPct = (level / scale.toFloat() * 100).toInt()
                currentActualBatteryPercentage = batteryPct // Store the actual percentage
                actualBatteryTextView.text = "Actual Battery: $batteryPct%"
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_broadcast_receiver, container, false)

        // Initialize UI elements with new IDs
        spinner = view.findViewById(R.id.spinner) // Keep if spinner is still used
        idInputEditText = view.findViewById(R.id.idInputEditText)
        actualBatteryTextView = view.findViewById(R.id.actualBatteryTextView)
        calculatedBatteryTextView = view.findViewById(R.id.calculatedBatteryTextView)

        // Spinner setup (adjust or remove if not needed for other features)
        val options = arrayOf("Custom", "Battery Notification") // Keep if spinner is still used
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, options)
        spinner.adapter = adapter

        // Spinner listener to handle custom broadcast and battery notification
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> { // "Custom" selected
                        val customIntent = Intent("com.example.cse489assignment.CUSTOM_BROADCAST")
                        customIntent.putExtra("message", "Custom Broadcast Received!")
                        requireContext().sendBroadcast(customIntent)
                        Toast.makeText(requireContext(), "Custom Broadcast Sent!", Toast.LENGTH_SHORT).show()
                    }
                    1 -> { // "Battery Notification" selected
                        // This option is handled by the EditText's TextWatcher
                        // No direct action needed here, as the notification is triggered by ID input
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No action needed
            }
        }
        spinner.setSelection(1) // Select "Battery Notification" by default

        idInputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val inputId = s.toString()
                if (inputId == "38") {
                    val calculatedPercentage = (currentActualBatteryPercentage + 10) % 238
                    calculatedBatteryTextView.text = "Display Percentage: $calculatedPercentage%"
                    checkNotificationPermissionAndShowBatteryNotification()
                } else {
                    // If input is not 38, show actual battery percentage in calculatedBatteryTextView
                    calculatedBatteryTextView.text = "Display Percentage: $currentActualBatteryPercentage%"
                    // Optionally, you might want to cancel any previous notification here
                    // NotificationManagerCompat.from(requireContext()).cancel(NOTIFICATION_ID)
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        createNotificationChannel() // Create channel when fragment is created

        return view
    }

    private fun checkNotificationPermissionAndShowBatteryNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    showBatteryNotification()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    Toast.makeText(requireContext(), "Notification permission is needed to show battery alerts.", Toast.LENGTH_LONG).show()
                    requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {
                    requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            showBatteryNotification()
        }
    }

    private fun showBatteryNotification() {
        val inputId = idInputEditText.text.toString()
        val calculatedPercentage: Int

        if (inputId == "38") {
            calculatedPercentage = (currentActualBatteryPercentage + 10) % 238
        } else {
            calculatedPercentage = currentActualBatteryPercentage
        }

        calculatedBatteryTextView.text = "Calculated Battery (for notification): $calculatedPercentage%"

        val notificationManager = NotificationManagerCompat.from(requireContext())
        val builder = NotificationCompat.Builder(requireContext(), NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Use a suitable icon
            .setContentTitle("Battery Status")
            .setContentText("Display Percentage: $calculatedPercentage%")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Battery Notifications"
            val descriptionText = "Notifications for battery percentage"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onResume() {
        super.onResume()
        // Register custom receiver (if still needed)
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
        requireActivity().unregisterReceiver(customReceiver) // Unregister if still needed
        requireActivity().unregisterReceiver(batteryReceiver)
    }
}
