package com.example.navigationdrawer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CustomBroadcastSenderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_broadcast_sender)

        val messageEditText: EditText = findViewById(R.id.messageEditText)
        val sendBroadcastButton: Button = findViewById(R.id.sendBroadcastButton)

        sendBroadcastButton.setOnClickListener {
            val message = messageEditText.text.toString()
            if (message.isNotBlank()) {
                val customIntent = Intent("com.example.cse489assignment.CUSTOM_BROADCAST")
                customIntent.putExtra("message", message)
                sendBroadcast(customIntent)
                Toast.makeText(this, "Custom Broadcast Sent: \"$message\"", Toast.LENGTH_LONG).show()
                finish() // Close this activity after sending broadcast
            } else {
                Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show()
            }
        }
    }
}