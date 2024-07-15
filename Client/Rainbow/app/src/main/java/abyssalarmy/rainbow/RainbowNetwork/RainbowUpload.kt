package abyssalarmy.rainbow.RainbowNetwork

import abyssalarmy.rainbow.RainbowUtils.RainbowScope
import abyssalarmy.rainbow.RainbowUtils.RainbowTools
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import okhttp3.*
import java.io.File
import java.io.IOException

class RainbowUpload(val context: Context) {
    private val host = RainbowTools.rainbowConnectionData(context)
    private val client = OkHttpClient()
    fun upload(file: File, onComplete: (() -> Unit)? = null) {
        RainbowScope.runAsync {
            val formBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.name, RequestBody.create(null, file))
                .build()
            val request = Request.Builder()
                .url(host + "upload/")
                .post(formBody)
                .addHeader("model", RainbowTools.getDeviceName())
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    onComplete?.invoke()
                }
                override fun onResponse(call: Call, response: Response) {
                    response.close()
                    onComplete?.invoke()
                }
            })
        }
    }
}