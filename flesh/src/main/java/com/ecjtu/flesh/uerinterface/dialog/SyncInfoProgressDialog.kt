package com.ecjtu.flesh.uerinterface.dialog

import android.content.Context
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.widget.Toast
import com.amazonaws.AbortedException
import com.amazonaws.ClientConfiguration
import com.amazonaws.Protocol
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.GetObjectRequest
import com.amazonaws.services.s3.model.S3Object
import com.ecjtu.flesh.Constants
import com.ecjtu.flesh.R
import com.ecjtu.flesh.db.DatabaseManager
import com.ecjtu.flesh.util.encrypt.SecretKeyUtils
import com.ecjtu.flesh.util.file.FileUtil
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import kotlin.concurrent.thread

//// Initializes TransferUtility
//val transferUtility = TransferUtility(s3, getApplicationContext())
//// Starts a download
//val observer = transferUtility.download("bucket_name", "key", file)
//observer.setTransferListener(object : TransferListener() {
//    fun onStateChanged(id: Int, newState: String) {
//        // Do something in the callback.
//    }
//
//    fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
//        // Do something in the callback.
//    }
//
//    fun onError(id: Int, e: Exception) {
//        // Do something in the callback.
//    }
//})
/**
 * Created by Ethan_Xiang on 2018/3/22.
 */
class SyncInfoProgressDialog(context: Context, val deviceId: String) : BaseDialogHelper(context) {

    private var mThread: Thread? = null

    override fun onCreateDialog(): AlertDialog? {
        return AlertDialog.Builder(getContext())
                .setTitle(R.string.please_wait_for_sync)
                .setView(R.layout.dialog_sync_info_progress)
                .setNegativeButton(R.string.cancel, { _, _ ->
                    mThread?.interrupt()
                })
                .setCancelable(false)
                .create()
    }

    override fun onDialogShow(dialog: AlertDialog) {
        super.onDialogShow(dialog)
        mThread = thread {
            var index = 0
            while (!Thread.interrupted()) {
                var outputStream: OutputStream? = null
                var s3Object: S3Object? = null
                try {
                    val secretKey = SecretKeyUtils.getKeyFromServer()
                    val content = SecretKeyUtils.getS3InfoFromServer(secretKey!!.key)
                    val params = content.split(",")
                    val provider = BasicAWSCredentials(params[0], params[1])
                    val config = ClientConfiguration()
                    config.protocol = Protocol.HTTP
                    if (index >= 1) {
                        config.protocol = Protocol.HTTPS
                    }
                    val mS3 = AmazonS3Client(provider, config)
                    val region = Region.getRegion(Regions.CN_NORTH_1)
                    mS3.setRegion(region)
                    mS3.setEndpoint(Constants.S3_URL)
                    val dbPath = getContext().getDatabasePath(DatabaseManager.DB_NAME)
                    val request = GetObjectRequest("firststorage0001", "databases/$deviceId")
                    var bytesCount = 0L
                    var lengthCount = -1L

                    request.setGeneralProgressListener { event ->
                        if (event.bytesTransferred >= 0) {
                            bytesCount += event.getBytesTransferred();
                        }
                        val percent = "" + ((bytesCount * 1.0f) / (lengthCount * 1.0f) * 100).toInt() + "%"
                        System.out.println("percent " + percent +
                                " transferredBytes " + event.getBytesTransferred() +
                                " eventType " + event.eventCode)
                        getHandler().post {
                            getDialog()?.setTitle(getContext().getString(R.string.please_wait_for_sync) + percent)
                        }
                    }
                    s3Object = mS3.getObject(request)
                    lengthCount = s3Object?.objectMetadata?.getRawMetadataValue("Content-Length") as Long

                    if (s3Object != null) {
                        val time = s3Object.objectMetadata?.userMetadata?.get("update_time")
                                ?: "-1"
                        val parent = dbPath.parent
                        val tempFile = File(parent, "db_temp")
                        outputStream = FileOutputStream(tempFile)
                        FileUtil.copyFile(s3Object.objectContent, outputStream)
                        getHandler().post {
                            if (time != "-1") {
                                PreferenceManager.getDefaultSharedPreferences(getContext()).edit()
                                        .putLong(Constants.PREF_SYNC_DATA_TIME, time.toLong()).apply()
                            }
                            FileUtil.moveFile2Path(tempFile, dbPath)
                            Toast.makeText(getContext(), R.string.sync_success, Toast.LENGTH_SHORT).show()
                            getDialog()?.cancel()
                        }
                    }
                    break
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    if (ex is InterruptedException || ex is AbortedException) {
                        getHandler().post {
                            Toast.makeText(getContext(), R.string.sync_failure, Toast.LENGTH_SHORT).show()
                        }
                        break
                    } else {
                        if (index >= 1) {
                            break
                        }
                        index++
                    }
                } finally {
                    try {
                        outputStream?.close()
                    } catch (ex: Exception) {
                    }
                    try {
                        s3Object?.close()
                    } catch (ex: Exception) {
                    }
                }
            }
        }
    }

    override fun onDialogCancel(dialog: AlertDialog) {
        super.onDialogCancel(dialog)
    }
}