import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import com.garbi.garbi_recolection.R

class MyNotification(private val context: Context, private val channelId: String) {
    private val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(context, channelId)
    private val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private var channel: NotificationChannel? = null

    fun build(imgID: Int, title: String?, content: String?, pendingIntent: PendingIntent) {
        notificationBuilder.setSmallIcon(imgID)
            .setContentTitle(title)
            .setContentText(content)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(content))
    }

    fun createChannelGroup(groupId: String?, groupNameId: Int) {
        val groupName: CharSequence = context.getString(groupNameId)
        notificationManager.createNotificationChannelGroup(NotificationChannelGroup(
            groupId,
            groupName
        ))
        channel!!.group = groupId
    }

    fun addChannel(chanelName: CharSequence?) {
        channel = NotificationChannel(channelId, chanelName, NotificationManager.IMPORTANCE_DEFAULT)
    }

    fun show(idAlert: Int) {
        notificationManager.createNotificationChannel(channel!!)
        notificationManager.notify(idAlert, notificationBuilder.build())
    }

    companion object {
        const val CHANNEL_ID_NOTIFICATIONS = "channel_id_notifications"
        const val CHANNEL_GROUP_GENERAL = "channel_group_general"
        const val NOTIFICATION_ID = 1
    }
}