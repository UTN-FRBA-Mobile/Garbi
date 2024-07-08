package com.garbi.garbi_recolection.firebase

import MyNotification
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.garbi.garbi_recolection.MainActivity
import com.garbi.garbi_recolection.R

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseMessagingService : FirebaseMessagingService() {
    class MyFirebaseMessagingService : FirebaseMessagingService() {
        override fun onNewToken(p0: String) {
            super.onNewToken(p0)
        }

        companion object {
            private const val TAG = "MyFirebaseMsgService"
        }

        override fun onMessageReceived(remoteMessage: RemoteMessage) {
            super.onMessageReceived(remoteMessage)
            val notification = remoteMessage.notification
            val title: String = notification!!.title!!
            val msg: String = notification.body!!

            sendNotification(title, msg)
        }

        private fun sendNotification(title: String, msg: String) {
            val intent = Intent(this, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                this,
                MyNotification.NOTIFICATION_ID,
                intent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
            )
            val notification = MyNotification(this, MyNotification.CHANNEL_ID_NOTIFICATIONS)
            notification.build(R.drawable.garbi,title, msg, pendingIntent)
            notification.addChannel("Notificaciones")
            notification.createChannelGroup(
                MyNotification.CHANNEL_GROUP_GENERAL,
                R.string.notification_channel_group_general
            )
            notification.show(MyNotification.NOTIFICATION_ID)
        }
    }
}