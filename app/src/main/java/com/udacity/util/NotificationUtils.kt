package com.udacity.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.udacity.DetailActivity
import com.udacity.MainActivity
import com.udacity.R
//Notification ID
private val NOTIFICATION_ID = 0

fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context){

    val contentIntent = Intent(applicationContext, DetailActivity::class.java)
    contentIntent.putExtra(MainActivity.FILE_NAME, MainActivity.fileName)
    contentIntent.putExtra(MainActivity.FILE_STATUS, MainActivity.fileStatus)

    //Create PendingIntent
    val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            NOTIFICATION_ID,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
    )

//Builds the Notification
    val builder = NotificationCompat.Builder(
            applicationContext, applicationContext.getString(R.string.download_channel_id))

            .setSmallIcon(R.drawable.ic_assistant_black_24dp)
            .setContentTitle(applicationContext.getString(R.string.notification_title))
            .setContentText(messageBody)
            //We pass the pending Intent to the Notification
            .setContentIntent(pendingIntent)
            //The Notification dismisses itself when takes you to the App.
            .setAutoCancel(true)
            .addAction(R.drawable.ic_assistant_black_24dp, applicationContext.getString(R.string.see_downloaded_file), pendingIntent)
            //To support API 25 or lower we need to also call here setPriority
            .setPriority(NotificationCompat.PRIORITY_HIGH)
    //Notification built
    notify(NOTIFICATION_ID, builder.build())
}

fun NotificationManager.cancelNotification(){
    cancel(NOTIFICATION_ID)
}