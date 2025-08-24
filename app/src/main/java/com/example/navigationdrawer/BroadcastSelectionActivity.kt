package com.example.navigationdrawer

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class BroadcastSelectionActivity : AppCompatActivity() {

    private lateinit var broadcastSpinner: Spinner
    private lateinit var proceedButton: Button
    private var selectedOption: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_broadcast_selection)

        broadcastSpinner = findViewById(R.id.broadcast_spinner)
        proceedButton = findViewById(R.id.proceed_button)

        val options = arrayOf("Custom broadcast receiver", "System battery notification receiver")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        broadcastSpinner.adapter = adapter

        broadcastSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedOption = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        proceedButton.setOnClickListener {
            val intent: Intent
            if (selectedOption == "Custom broadcast receiver") {
                intent = Intent(this, CustomBroadcastInputActivity::class.java)
            } else {
                intent = Intent(this, BatteryBroadcastReceiverActivity::class.java)
            }
            startActivity(intent)
        }
    }
}