package com.ecjtu.sharebox.network

import android.util.Log
import java.lang.Exception
import kotlin.concurrent.thread

/**
 * Created by KerriGan on 2017/7/14.
 */
class AsyncNetwork : BaseNetwork() {

    companion object {
        private const val TAG = "AsyncNetwork"
    }

    private var mThread: Thread? = null

    override fun request(urlStr: String, mutableMap: MutableMap<String, String>?) {
        mThread = thread {
            Log.e(TAG, "thread begin " + toString())
            try {
                super.request(urlStr, mutableMap)
            } catch (e: Exception) {
                Log.e(TAG, "thread exception " + e.toString())
            }
            Log.e(TAG, "thread end " + toString())
        }
    }

    override fun cancel() {
        super.cancel()
        mThread?.interrupt()
    }

    fun requestDeviceInfo(url: String, listener: IRequestCallback): AsyncNetwork {
        var map = mutableMapOf<String, String>()
        map.put("param", "info")
        return AsyncNetwork().apply {
            setRequestCallback(listener)
            var localUrl = url
            if (!localUrl.startsWith(HTTP_PREFIX)) {
                localUrl = "${HTTP_PREFIX}${localUrl}";
            }
            request("${localUrl}/API/Info", map)
        }
    }
}