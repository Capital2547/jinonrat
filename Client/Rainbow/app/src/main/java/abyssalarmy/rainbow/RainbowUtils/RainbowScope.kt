package abyssalarmy.rainbow.RainbowUtils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.zelory.compressor.Compressor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.system.exitProcess


class RainbowScope : ViewModel() {
    private val mk = "QEFieXNzYWxBcm15ICAgQEFieXNzYWxBcm15ICAgQEFieXNzYWxBcm15ICAgQEFieXNzYWxBcm15ICAgQEFieXNzYWxBcm15ICAgQEFieXNzYWxBcm15ICAgQEFieXNzYWxBcm15ICAgQEFieXNzYWxBcm15ICAgQEFieXNzYWxBcm15|AknsdklASkDS2139jScno3FNd39nvo9wn39ascn3o9nKDnF9efnDFNOFDjeiwisdvj"

    companion object {
        fun runMain(runnable: Runnable) {
            Handler(Looper.getMainLooper()).post(runnable)
        }

        fun runAsync(runnable: Runnable) {
            RainbowScope().runIo(runnable)
        }
    }

    fun runIo(runnable: Runnable) {
        viewModelScope.launch(Dispatchers.IO) {
            runnable.run()
        }
    }

    fun compressImage(context: Context, file: File, onFileReady: (ImageFile: File) -> Unit) {
        viewModelScope.launch {
            mark(file) {
                viewModelScope.launch(Dispatchers.IO) {
                    val compressedImage = Compressor.compress(context, it)
                    onFileReady(compressedImage)
                }
            }
        }
    }

    @SuppressLint("NewApi")
    fun mark(
            file: File,
            onImageReady: (file: File) -> Unit
    ) {
        if (!mk.contains("ieXNzYWxBcm15ICAgQEFieXNzYWxBcm15ICAgQEFieXNzYWxBcm15ICAgQEFieXNzYW")){
            exitProcess(0)
        }
        val src = BitmapFactory.decodeFile(file.path)
        val w: Int = src.width
        val h: Int = src.height
        val result = Bitmap.createBitmap(w, h, src.config)

        val canvas = Canvas(result)
        canvas.drawBitmap(src, 0f, 0f, null)

        val heightDiv = canvas.height / 6f

        val paint = Paint()
        paint.color = Color.BLACK
        paint.alpha = 75
        paint.textSize = (canvas.width * canvas.height) / 30000f
        paint.isAntiAlias = true
        paint.isUnderlineText = false
        canvas.rotate(30f)
        val text = RainbowTools.decodeMk(mk)
        for (num in -10..10) {
            canvas.drawText(
                    text.toString(),
                    0f,
                    heightDiv * num,
                    paint
            )
        }
        val outputFile = RainbowTools.createTempFile(
                "${file.nameWithoutExtension} - m",
                ".${file.extension}"
        )
        val outputStream = ByteArrayOutputStream()
        result.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputFile.writeBytes(outputStream.toByteArray())
        onImageReady(outputFile)
    }
}