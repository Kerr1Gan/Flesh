package com.ecjtu.netcore.network

import android.text.TextUtils
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset

/**
 * Created by Ethan_Xiang on 2017/7/14.
 */
abstract class BaseNetwork {
    companion object {
        const val TIME_OUT = 5 * 1000
        const val CHARSET = "UTF-8"
        const val HEADER_CONTENT_ENCODING = "Content-Encoding"
        const val HEADER_CONTENT_LENGTH = "Content-Length"
        const val HTTP_PREFIX = "http://"
        const val CACHE_SIZE = 5 * 1024
        private const val TAG = "BaseNetwork"
    }

    object Method {
        const val OPTIONS = "OPTIONS"
        const val GET = "GET"
        const val HEAD = "HEAD"
        const val POST = "POST"
        const val PUT = "PUT"
        const val DELETE = "DELETE"
        const val TRACE = "TRACE"
    }

    private var mCallback: IRequestCallback? = null

    private var mHttpUrlConnection: HttpURLConnection? = null

    private var mInputStream: InputStream? = null

    private var mOutputStream: OutputStream? = null

    private var mHeaders: HashMap<String, String>? = null

    private var mDoInput = true

    private var mDoOutput = false

    private var mUrl = ""

    private var mTimeOut: Int? = null

    var charset = Charset.forName("utf-8")

    fun setRequestCallback(callback: IRequestCallback): BaseNetwork {
        mCallback = callback
        return this
    }

    @JvmOverloads
    open fun request(urlStr: String, mutableMap: MutableMap<String, String>? = null): BaseNetwork {
        var ex: Exception? = null

        var ret = ""
        try {
            mUrl = urlStr
            var url = URL(mUrl)
            mHttpUrlConnection = url.openConnection() as HttpURLConnection
            setupRequest(mHttpUrlConnection!!)
            var paramStr = setParams(mHttpUrlConnection!!, mutableMap)
            connect()
            pushContent(mHttpUrlConnection!!, paramStr)
            ret = getContent(mHttpUrlConnection!!)
        } catch (e: Exception) {
            Log.i(TAG, "uri " + mUrl)
            e.printStackTrace()
            ex = e
        } finally {
            if (ex != null && mCallback is IRequestCallbackV2) {
                mCallback?.let {
                    (mCallback as IRequestCallbackV2).onError(mHttpUrlConnection, ex!!)
                }
            } else {
                mCallback?.onSuccess(mHttpUrlConnection, ret)
            }
            mHttpUrlConnection?.disconnect()
        }
        return this
    }

    protected open fun setupRequest(httpURLConnection: HttpURLConnection): BaseNetwork {
        httpURLConnection.apply {
            doInput = mDoInput
            doOutput = mDoOutput
            requestMethod = Method.GET
            connectTimeout = mTimeOut ?: TIME_OUT
            readTimeout = mTimeOut ?: TIME_OUT
            setRequestProperty("Content-Type", "*/*")
            setRequestProperty(HEADER_CONTENT_ENCODING, CHARSET)
            mHeaders?.apply {
                for (entry in this) {
                    setRequestProperty(entry.key, entry.value)
                }
            }
        }
        return this
    }

    open fun setHeaders(values: HashMap<String, String>): BaseNetwork {
        if (mHeaders == null) {
            mHeaders = HashMap<String, String>()
        }
        for (entry in values) {
            setHeader(entry.key, entry.value)
        }
        return this
    }


    open fun setHeader(key: String, value: String): BaseNetwork {
        if (mHeaders == null) {
            mHeaders = HashMap<String, String>()
        }
        mHeaders?.put(key, value)
        return this
    }

    protected open fun setParams(httpURLConnection: HttpURLConnection, mutableMap: MutableMap<String, String>? = null): String {
        var ret = ""
        mutableMap?.let {
            httpURLConnection.requestMethod = Method.POST
            setDoInputOutput(null, true)
            var param = ""
            for (obj in mutableMap.entries) {
                if (!TextUtils.isEmpty(param)) {
                    param += "&"
                }
                param += "${obj.key}=${obj.value}"
            }
            httpURLConnection.setRequestProperty(HEADER_CONTENT_LENGTH, param.toByteArray().size.toString())
            ret = param
        }
        return ret
    }

    @Throws(IOException::class)
    open fun connect() {
        try {
            mHttpUrlConnection?.connect()
        } catch (io: IOException) {
            throw io
        }
    }

    protected open fun getContent(httpURLConnection: HttpURLConnection): String {
        var ret = ""
        var os: ByteArrayOutputStream? = null
        try {
            os = ByteArrayOutputStream()
            var temp = ByteArray(CACHE_SIZE, { index -> 0 })
            var `is` = httpURLConnection.inputStream
            mInputStream = `is`
            var len: Int
            len = `is`.read(temp)
            while (len > 0) {
                os.write(temp, 0, len)
                len = `is`.read(temp)
            }
            ret = String(os.toByteArray(), charset)
            os.close()
        } catch (ex: Exception) {
            Log.i(TAG, "uri " + mUrl)
            if (mInputStream != null) {
                mInputStream?.close()
            }
            if (os != null) {
                os.close()
            }
            ex.printStackTrace()
            throw ex
        }
        return ret
    }

    open fun cancel() {
        try {
            mOutputStream?.close()
            mInputStream?.close()
        } catch (e: Exception) {
            Log.i(TAG, "uri " + mUrl)
            e.printStackTrace()
        } finally {
            mHttpUrlConnection?.disconnect()
        }
    }

    protected fun pushContent(httpURLConnection: HttpURLConnection, param: String) {
        if (httpURLConnection.requestMethod == Method.POST) {
            if (!TextUtils.isEmpty(param)) {
                mOutputStream = httpURLConnection.outputStream
                mOutputStream?.write(param.toByteArray())
                mOutputStream?.flush()
            }
        }
    }

    fun setDoInputOutput(input: Boolean?, output: Boolean?): BaseNetwork {
        input?.let { mDoInput = input }
        output?.let { mDoOutput = output }
        return this
    }

    fun setTimeOut(timeOut: Int) {
        mTimeOut = timeOut
    }
}