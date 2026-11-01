package com.mousereplicator.pro

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(32, 32, 32, 32)
        }

        val title = TextView(this).apply {
            text = "MOUSE REPLICATOR PRO"
            textSize = 23f
            gravity = Gravity.CENTER
        }

        status = TextView(this).apply {
            text = "Version 0.1\n\nSet up your permissions to begin."
            textSize = 15f
            gravity = Gravity.CENTER
            setPadding(0, 30, 0, 30)
        }

        layout.addView(title)
        layout.addView(status)

        addButton(layout, "1. ENABLE ACCESSIBILITY") {
            startActivity(
                Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            )
        }

        addButton(layout, "2. GRANT OVERLAY PERMISSION") {
            startActivity(
                Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
            )
        }

        addButton(layout, "3. START FLOATING MOUSE") {

            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(
                    this,
                    "Grant overlay permission first",
                    Toast.LENGTH_SHORT
                ).show()

            } else {
                startService(
                    Intent(this, MouseOverlayService::class.java)
                )

                status.text =
                    "Mouse controller started.\n\n" +
                    "Switch to another application to test it."
            }
        }

        setContentView(layout)
    }

    private fun addButton(
        layout: LinearLayout,
        label: String,
        action: () -> Unit
    ) {
        val button = Button(this).apply {
            text = label
            setOnClickListener { action() }
        }

        layout.addView(
            button,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )
    }
}
