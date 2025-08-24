package com.example.navigationdrawer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CustomBroadcastReceiverActivity : AppCompatActivity() {

    private lateinit var receivedMessageTextView: TextView
    private lateinit var customReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_broadcast_receiver)

        receivedMessageTextView = findViewById(R.id.received_message_text_view)

        customReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == CustomBroadcastInputActivity.CUSTOM_BROADCAST_ACTION) {
                    val message = intent.getStringExtra(CustomBroadcastInputActivity.EXTRA_MESSAGE)
                    receivedMessageTextView.text = "Received Message: $message"
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter(CustomBroadcastInputActivity.CUSTOM_BROADCAST_ACTION)
        registerReceiver(customReceiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(customReceiver)
    }
}