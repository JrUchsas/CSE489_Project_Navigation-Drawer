package com.example.navigationdrawer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class CustomBroadcastInputActivity : AppCompatActivity() {

    companion object {
        const val CUSTOM_BROADCAST_ACTION = "com.example.navigationdrawer.CUSTOM_BROADCAST"
        const val EXTRA_MESSAGE = "extra_message"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_broadcast_input)

        val messageEditText: EditText = findViewById(R.id.custom_message_edit_text)
        val sendButton: Button = findViewById(R.id.send_custom_broadcast_button)

        sendButton.setOnClickListener {
            val message = messageEditText.text.toString()
            val intent = Intent(CUSTOM_BROADCAST_ACTION)
            intent.putExtra(EXTRA_MESSAGE, message)
            sendBroadcast(intent)

            // Optionally, navigate to the third activity immediately after sending
            val thirdActivityIntent = Intent(this, CustomBroadcastReceiverActivity::class.java)
            startActivity(thirdActivityIntent)
        }
    }
}