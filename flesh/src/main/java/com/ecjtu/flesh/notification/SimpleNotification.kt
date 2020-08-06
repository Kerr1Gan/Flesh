package com.ecjtu.flesh.notification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.ecjtu.flesh.R

/**
 * Created by KerriGan on 2017/9/3.
 */
abstract class SimpleNotification(val context: Context) {

    private var mBuilder: NotificationCompat.Builder? = null

    open fun buildNotification(id: Int, title: String, contentText: String, ticker: String): NotificationCompat.Builder {
        val builder = NotificationCompat.Builder(context)
        builder.setContentTitle(title)
        builder.setContentText(contentText)
        builder.setSmallIcon(R.mipmap.ic_launcher)
        builder.setWhen(System.currentTimeMillis())
        builder.setTicker(ticker)
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        builder.setAutoCancel(true)

        mBuilder = builder
        return builder
    }

    open fun fullScreenIntent(builder: NotificationCompat.Builder?, requestCode: Int, intent: Intent?, highPriority: Boolean = false) {
        if (intent == null) {
            builder?.setFullScreenIntent(null, highPriority)
        } else {
            val pending = PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            builder?.setFullScreenIntent(pending, highPriority)
        }
    }

    open fun sendNotification(id: Int, builder: NotificationCompat.Builder?, tag: String? = null) {
        NotificationManagerCompat.from(context).notify(tag, id, builder!!.build())
    }

    open fun cancelNotification(id: Int, tag: String? = null) {
        NotificationManagerCompat.from(context).cancel(tag, id)
    }

    open fun cancelAllNotification() {
        NotificationManagerCompat.from(context).cancelAll()
    }

    abstract fun send(tag: String? = null)

    abstract fun cancel(tag: String? = null)

    open fun getCurrentBuilder(): NotificationCompat.Builder? {
        return mBuilder
    }
}