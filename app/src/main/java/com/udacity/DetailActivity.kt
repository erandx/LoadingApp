package com.udacity

import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.udacity.MainActivity.Companion.FILE_NAME
import com.udacity.MainActivity.Companion.FILE_STATUS
import com.udacity.util.cancelNotification
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val notificationManager = ContextCompat.getSystemService(applicationContext,
        NotificationManager::class.java)
        //Cancels the Notification when we press "See File"
        notificationManager?.cancelNotification()

        val fileName = intent.getStringExtra(FILE_NAME)
        val status = intent.getStringExtra(FILE_STATUS)

        file_name_text.text = fileName.toString()
        status_name_text.text = status.toString()

        //Intent to bring the user to the Main Activity
        ok_button.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}
