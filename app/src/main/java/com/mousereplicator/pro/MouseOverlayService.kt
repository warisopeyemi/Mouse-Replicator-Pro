package com.mousereplicator.pro

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MouseOverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var panel: LinearLayout
    private lateinit var params: WindowManager.LayoutParams

    override fun onBind(intent: Intent?): IBinder? = null

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        if (!::windowManager.isInitialized) {
            windowManager =
                getSystemService(WINDOW_SERVICE) as WindowManager

            createController()
        }

        return START_NOT_STICKY
    }

    private fun createController() {

        panel = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(8), dp(8), dp(8), dp(8))

            background = GradientDrawable().apply {
                setColor(0xEE202124.toInt())
                cornerRadius = dp(14).toFloat()
            }
        }

        val title = TextView(this).apply {
            text = "MOUSE REPLICATOR  •  MOVE ME"
            setTextColor(0xFFFFFFFF.toInt())
            setPadding(dp(8), dp(10), dp(8), dp(10))
        }

        panel.addView(title)

        title.setOnTouchListener(object : View.OnTouchListener {

            private var startX = 0
            private var startY = 0
            private var touchX = 0f
            private var touchY = 0f

            override fun onTouch(
                view: View,
                event: MotionEvent
            ): Boolean {

                when (event.action) {

                    MotionEvent.ACTION_DOWN -> {
                        startX = params.x
                        startY = params.y
                        touchX = event.rawX
                        touchY = event.rawY
                        return true
                    }

                    MotionEvent.ACTION_MOVE -> {
                        params.x =
                            startX + (event.rawX - touchX).toInt()

                        params.y =
                            startY + (event.rawY - touchY).toInt()

                        windowManager.updateViewLayout(
                            panel, params
                        )
                        return true
                    }
                }

                return true
            }
        })

        addRow(
            "TAP",
            "DOUBLE",
            "HOLD"
        ) { action ->

            when (action) {
                "TAP" -> runMouse { it.tapCenter() }
                "DOUBLE" -> runMouse { it.doubleTapCenter() }
                "HOLD" -> runMouse { it.holdCenter() }
            }
        }

        addRow(
            "DRAG",
            "PAGE DOWN",
            "PAGE UP"
        ) { action ->

            when (action) {
                "DRAG" -> runMouse { it.dragDemo() }
                "PAGE DOWN" -> runMouse { it.scrollDown() }
                "PAGE UP" -> runMouse { it.scrollUp() }
            }
        }

        addRow("HIDE") {
            stopSelf()
        }

        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = dp(15)
            y = dp(180)
        }

        windowManager.addView(panel, params)
    }

    private fun runMouse(
        action: (MouseAccessibilityService) -> Unit
    ) {
        val service = MouseAccessibilityService.instance

        if (service == null) {
            Toast.makeText(
                this,
                "Enable Mouse Accessibility first",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            action(service)
        }
    }

    private fun addRow(
        vararg labels: String,
        action: (String) -> Unit
    ) {

        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        labels.forEach { label ->

            val button = Button(this).apply {
                text = label
                textSize = 10f

                setOnClickListener {
                    action(label)
                }
            }

            row.addView(
                button,
                LinearLayout.LayoutParams(
                    0,
                    dp(48),
                    1f
                ).apply {
                    setMargins(dp(2), dp(2), dp(2), dp(2))
                }
            )
        }

        panel.addView(row)
    }

    override fun onDestroy() {
        if (::windowManager.isInitialized &&
            ::panel.isInitialized &&
            panel.isAttachedToWindow
        ) {
            windowManager.removeView(panel)
        }

        super.onDestroy()
    }
}
