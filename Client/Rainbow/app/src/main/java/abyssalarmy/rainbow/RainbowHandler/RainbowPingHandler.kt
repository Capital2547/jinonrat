package abyssalarmy.rainbow.RainbowHandler

import abyssalarmy.rainbow.RainbowNetwork.RainbowSocket
import android.util.Log

class RainbowPingHandler(private val socket: RainbowSocket) {
    fun handle(){
        socket.addEventListener("ping"){
            Log.i("PING","PING")
            socket.sendPong()
        }
    }
}