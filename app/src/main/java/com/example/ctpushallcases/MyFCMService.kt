package com.example.ctpushallcases

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.clevertap.android.sdk.CleverTapAPI
import com.clevertap.android.sdk.pushnotification.fcm.CTFcmMessageHandler
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.lang.Exception

class MyFCMService: FirebaseMessagingService() {

    private val TAG = "MyFCMService"

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        message.data.apply {
            try {
                if (size > 0) {
                    val extras = Bundle()
                    for ((key, value) in this) {
                        extras.putString(key, value)
                    }
                    Log.d(TAG, "payload ${extras.toString()}")
                    val info = CleverTapAPI.getNotificationInfo(extras)

                    if (info.fromCleverTap) {
                        if (extras.containsKey("type")){
                            if (extras.getString("type") == "custom_render"){
                                //this is custom notification that will be rendered using custom code
                                renderNotification(extras)
                            }else{
                                //CleverTap will render this notification
                                CTFcmMessageHandler().createNotification(applicationContext, message)
                            }
                        }else{
                            CTFcmMessageHandler().createNotification(applicationContext, message)
                        }
                    } else {
                        // not from CleverTap handle yourself or pass to another provider
                    }
                }
            } catch (t: Throwable) {
                Log.d(TAG, "Error parsing FCM message", t)
            }
        }
    }

    private fun renderNotification(extras: Bundle) {
        Log.d("MyFCMService", "Custom rendering the notification")
        val notifId = 100
        val endTimerString = extras.getString("end_time")
        var endTimer = 0
        try {
            if (endTimerString != null) {
                endTimer = endTimerString.toInt()
            }
        }catch (exception : Exception){
            Log.d("MyFCMService", "Failed to convert end timer to long")
            endTimer = 0
        }
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val builder = NotificationCompat.Builder(this, extras.getString("wzrk_cid")!!)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(extras.getString("nt")!!)
            .setContentText(extras.getString("nm")!!)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setGroup(extras.getString("my_group"))

        if (endTimer != 0){
            builder.setTimeoutAfter(endTimer*60*1000L)
        }
        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                /**
                 * Permission code is not implemented.
                 * Please enable permissions manually
                 */
                Log.d(TAG, "No permission to post notification")
                return
            }
            notify(notifId, builder.build())
        }
        CleverTapAPI.getDefaultInstance(applicationContext)?.pushNotificationViewedEvent(extras)
    }

}
