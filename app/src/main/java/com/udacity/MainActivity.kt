package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.udacity.util.sendNotification
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private var stringUrl: String = ""

    private lateinit var downloadManager: DownloadManager
    private lateinit var notificationManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        createChannel(
                getString(R.string.download_channel_id),
                getString(R.string.download_channel_name)
        )

        custom_button.setOnClickListener {
            when (radio_group.checkedRadioButtonId) {
                R.id.glide_image -> {
                    stringUrl = GLIDE_URL
                    fileName = getString(R.string.glide_image_text)
                    download()
                    //Button state will trigger the Animation
                    //We could set it for each radio button as in this case or
                    //only one time in the download method to write less code

                }
                R.id.load_app_repository -> {
                    stringUrl = LOAD_APP_URL
                    fileName = getString(R.string.load_app_git_repository)
                    download()
                }
                R.id.retrofit_http -> {
                    stringUrl = RETROFIT_URL
                    fileName = getString(R.string.retrofit_http_client)
                    download()
                }
                else -> Toast.makeText(applicationContext, "Please make a choice", Toast.LENGTH_SHORT).show()
            }
            //Initialize the Notification Manager
            notificationManager = ContextCompat.getSystemService(applicationContext,
                    NotificationManager::class.java) as NotificationManager
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (id == downloadID) {

                fileStatus = ""

                val cursor: Cursor = downloadManager.query(DownloadManager.Query().setFilterById(downloadID))
                if (cursor.moveToFirst()) {
                    val status = cursor.getInt(
                            cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                    )
                    when (status) {
                        DownloadManager.STATUS_FAILED -> {
                            fileStatus = getString(R.string.download_failed)
                        }
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            fileStatus = getString(R.string.download_success)
                        }
                    }
                    notificationManager.sendNotification(stringUrl, applicationContext)
                }
            }
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        //Channels are available from level 26 API and above
        //We use a channel Check
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(channelId,
                    channelName,
                    //LOW priority makes no sound
                    //DEFAULT makes a sound
                    //HIGH Makes a sound and appears as a heads-up notification
                    NotificationManager.IMPORTANCE_HIGH)
                    //Disable badges for this Channel
                    .apply {
                        setShowBadge(false)
                    }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.app_name)
            //Register the channel with the System. You cannot change the importance
            // or notification behaviours after this
            notificationManager = this.getSystemService(
                    NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun download() {
//        custom_button.buttonState = ButtonState.Loading

        val request =
                DownloadManager.Request(Uri.parse(stringUrl))
                        //Title to be displayed in the Download folder
                        .setTitle(fileName)
                        .setDescription(getString(R.string.app_description))
                        //TODO
                        //uncomment after setting back minSKD 24 since my physical device uses API 23
//                        .setRequiresCharging(false)
                        .setAllowedOverMetered(true)
                        .setAllowedOverRoaming(true)

        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
                downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    companion object {
        private const val LOAD_APP_URL =
                "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val GLIDE_URL = "https://github.com/bumptech/glide/archive/master.zip"
        private const val RETROFIT_URL = "https://github.com/square/retrofit/archive/master.zip"

        lateinit var fileName: String
        lateinit var fileStatus: String

        const val FILE_NAME = "fileName"
        const val FILE_STATUS = "status"
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}
