package com.vishal.list_creater_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.vishal.list_creater_app.Main.MainActivity

class splashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.splash_screen)
        Thread(Runnable {
            Thread.sleep(3000)
            val intent= Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }).start()
    }
}