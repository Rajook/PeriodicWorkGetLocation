package com.krunal.locationexample.Service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import com.krunal.locationexample.Activity.MainActivity
import com.krunal.locationexample.R
import com.krunal.locationexample.Utility.Constants
import com.krunal.locationexample.Utility.Constants.Companion.foregroundServiceNotificationTitle
import android.os.Looper
import android.support.v4.content.LocalBroadcastManager
import com.krunal.locationexample.Utility.ProgressResponseBody


class MyForegroundService : Service(), ProgressResponseBody.OnAttachmentDownloadListener {

    private var count: Int = 0
    private var isDownLoading: Boolean = true
    private lateinit var notification: NotificationCompat.Builder
    private lateinit var notificationManager: NotificationManager
    private lateinit var url: String
    private lateinit var name: String
    private lateinit var id: String

    private lateinit var progressResponseBody: ProgressResponseBody

    override fun onCreate() {
        super.onCreate()
        val context: Context = this.applicationContext
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.run {
            when (action) {
                START_SERVICE -> {
                    url = intent.getStringExtra(EXTRA_URL) ?: ""
                    name = intent.getStringExtra(EXTRA_NAME) ?: ""
                    id = intent.getStringExtra(EXTRA_ID) ?: ""

                    startForeground(NOTIFICATION_ID, prepareNotification(intent,"Uploading Data..."))

                    val task = SyncData(this@MyForegroundService,this@MyForegroundService)
                    task.execute(10)
                    return START_STICKY
                }
                STOP_SERVICE -> {
                    closeService()
                    return START_NOT_STICKY
                }
                else -> {
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun closeService(isSuccess: Boolean = false) {
        sendBroadcast(if (isSuccess) BROADCAST_DOWNLOAD_EVENT_SUCCESS else BROADCAST_DOWNLOAD_EVENT_FAILED)
        stopForeground(true)
        stopSelf()
    }

    private fun sendBroadcast(name: String, progress: Int = 0) = Intent().run {
        val intent = Intent(BROADCAST_DOWNLOAD_EVENT)
        intent.putExtra(BROADCAST_DOWNLOAD_EVENT_NAME, name)
        intent.putExtra(BROADCAST_DOWNLOAD_EVENT_PROGRESS, progress)
        intent.putExtra(BROADCAST_DOWNLOAD_PRODUCT_NAME, this@MyForegroundService.name)
        intent.putExtra(BROADCAST_DOWNLOAD_PRODUCT_ID, this@MyForegroundService.id)
        intent.putExtra(BROADCAST_DOWNLOAD_PRODUCT_URL, this@MyForegroundService.url)
        LocalBroadcastManager.getInstance(this@MyForegroundService).sendBroadcast(intent)
    }

    private fun prepareNotification(intent: Intent?, s: String) : Notification?  {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        notificationIntent.action = "OPEN_ACTIVITY_DOWNLOAD"
// 1

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
            notificationManager.getNotificationChannel(CHANNEL_ID) == null
        ) {
            val name: CharSequence = getString(R.string.app_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.enableVibration(false)
            notificationManager.createNotificationChannel(channel)
        }


        notification =  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder(this, CHANNEL_ID)
        } else {
            NotificationCompat.Builder(this)
        }

        notification
            .setContentTitle(foregroundServiceNotificationTitle)
            .setContentText(s)
            .setProgress(100, count,count == 0)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentIntent(pendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notification.setVisibility(Notification.VISIBILITY_PUBLIC)
        }
        return notification.build()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        private const val NOTIFICATION_ID: Int = 9999
        private const val EXTRA_URL: String = "url"
        private const val EXTRA_NAME: String = "name"
        private const val EXTRA_ID: String = "id"
        private const val START_SERVICE: String = "START_SERVICE"
        private const val BROADCAST_DOWNLOAD_EVENT: String = "BROADCAST_DOWNLOAD_EVENT"
        private const val BROADCAST_DOWNLOAD_EVENT_NAME: String = "BROADCAST_DOWNLOAD_EVENT_NAME"
        private const val BROADCAST_DOWNLOAD_EVENT_START: String = "download-start"
        private const val BROADCAST_DOWNLOAD_EVENT_SUCCESS: String = "download-success"
        private const val BROADCAST_DOWNLOAD_EVENT_FAILED: String = "download-failed"
        private const val BROADCAST_DOWNLOAD_EVENT_PROGRESS: String = "download-progress"
        private const val BROADCAST_DOWNLOAD_PRODUCT_NAME: String = "product-name"
        private const val BROADCAST_DOWNLOAD_PRODUCT_ID: String = "product-id"
        private const val BROADCAST_DOWNLOAD_PRODUCT_URL: String = "product-url"
        private const val CHANNEL_ID: String = "CHANNEL_ID"
        private const val STOP_SERVICE: String = "STOP_SERVICE"

        fun startService(context: Context) =
            Intent(context, MyForegroundService::class.java).run {
                action = START_SERVICE
                context.startService(this)
            }

        fun stopService(context: Context) =
            Intent(context, MyForegroundService::class.java).run {
                action = STOP_SERVICE
                context.startService(this)
            }

    }

    override fun onAttachmentDownloadedSuccess() {
        TODO("Not yet implemented")
    }

    override fun onAttachmentDownloadedError() {
        TODO("Not yet implemented")
    }

    override fun onAttachmentDownloadedFinished() {
        TODO("Not yet implemented")
    }

    override fun onAttachmentDownloadUpdate(percent: Int) {
        notification.setProgress(
            100,
            percent,
            false
        )
        notificationManager.notify(NOTIFICATION_ID, notification.build())

        if (percent==100){
            closeService(true)
        }
    }
}