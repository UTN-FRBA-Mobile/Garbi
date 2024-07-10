package com.garbi.garbi_recolection.firebase

import MapsViewModel
import MyNotification
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.garbi.garbi_recolection.MainActivity
import com.garbi.garbi_recolection.R
import com.garbi.garbi_recolection.RouteManager

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
            Log.v(TAG,"MESSAGE RECEIVED")
            super.onMessageReceived(remoteMessage)
            val notification = remoteMessage.notification
            val title: String = notification!!.title!!
            val msg: String = notification.body!!

            val data = remoteMessage.data

            Log.v(TAG, "Se recibió notificación de comienzo de ruta con data ${data}")
            RouteManager.updateRouteModal(true)

            sendNotification(title, msg)

        }

        private fun sendNotification(title: String, msg: String) {
            Log.v(TAG,"sendNotification")
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