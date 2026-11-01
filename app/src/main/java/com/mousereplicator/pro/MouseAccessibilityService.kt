package com.mousereplicator.pro

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent

class MouseAccessibilityService : AccessibilityService() {

    companion object {
        @Volatile
        var instance: MouseAccessibilityService? = null
            private set
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

    override fun onInterrupt() {}

    override fun onDestroy() {
        instance = null
        super.onDestroy()
    }

    private fun performPath(
        path: Path,
        duration: Long
    ) {
        val stroke = GestureDescription.StrokeDescription(
            path, 0, duration
        )

        val gesture = GestureDescription.Builder()
            .addStroke(stroke)
            .build()

        dispatchGesture(gesture, null, null)
    }

    private fun screenCenter(): Pair<Float, Float> {
        val metrics = resources.displayMetrics

        return Pair(
            metrics.widthPixels / 2f,
            metrics.heightPixels / 2f
        )
    }

    fun tapCenter() {
        val (x, y) = screenCenter()

        val path = Path().apply {
            moveTo(x, y)
            lineTo(x + 1f, y)
        }

        performPath(path, 80)
    }

    fun doubleTapCenter() {
        tapCenter()

        Handler(Looper.getMainLooper()).postDelayed({
            tapCenter()
        }, 180)
    }

    fun holdCenter() {
        val (x, y) = screenCenter()

        val path = Path().apply {
            moveTo(x, y)
            lineTo(x + 1f, y)
        }

        performPath(path, 800)
    }

    fun dragDemo() {
        val (x, y) = screenCenter()

        val path = Path().apply {
            moveTo(x - 150f, y)
            lineTo(x + 150f, y)
        }

        performPath(path, 900)
    }

    fun scrollDown() {
        val metrics = resources.displayMetrics
        val x = metrics.widthPixels / 2f

        val path = Path().apply {
            moveTo(x, metrics.heightPixels * 0.75f)
            lineTo(x, metrics.heightPixels * 0.30f)
        }

        performPath(path, 500)
    }

    fun scrollUp() {
        val metrics = resources.displayMetrics
        val x = metrics.widthPixels / 2f

        val path = Path().apply {
            moveTo(x, metrics.heightPixels * 0.30f)
            lineTo(x, metrics.heightPixels * 0.75f)
        }

        performPath(path, 500)
    }
}
