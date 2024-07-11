package com.garbi.garbi_recolection.firebase

import MapsViewModel
import MyNotification
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.garbi.garbi_recolection.MainActivity
import com.garbi.garbi_recolection.R
import com.garbi.garbi_recolection.RouteManager

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseMessagingService : FirebaseMessagingService() {
        override fun onMessageReceived(remoteMessage: RemoteMessage) {
            super.onMessageReceived(remoteMessage)

            val title = getString(R.string.route_notification_title)
            val msg = getString(R.string.route_notification_msg)

            val data = remoteMessage.data
            val waypoints = data["waypoints"]

            Log.v("ROUTE", "Se recibió notificación de comienzo de ruta con data ${data}")
            RouteManager.updateRouteModal(true)
            RouteManager.updateRouteWaypoints(waypoints!!)

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
