package com.harsh.testingapi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.os.Handler
class SplashActivity : AppCompatActivity() {
    private val splashTimeOut: Long = 3000 // 3 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            // Start MainActivity after the specified delay
            startActivity(Intent(this@SplashActivity, TabLayoutActivity::class.java))
            finish() // Close SplashActivity to prevent it from being displayed when back button is pressed
        }, splashTimeOut)
    }
}