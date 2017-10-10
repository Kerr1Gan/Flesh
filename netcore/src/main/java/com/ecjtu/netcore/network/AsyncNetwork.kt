package com.ecjtu.netcore.network

import android.util.Log
import java.lang.Exception
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread

/**
 * Created by KerriGan on 2017/7/14.
 */
class AsyncNetwork : BaseNetwork() {

    companion object {
        private const val TAG = "AsyncNetwork"
        private var sThreadsCount: AtomicInteger = AtomicInteger(0)
    }

    private var mThread: Thread? = null

    override fun request(urlStr: String, mutableMap: MutableMap<String, String>?) {
        mThread = thread {
            Log.e(TAG, "thread begin " + toString() + " threads count:" + sThreadsCount.incrementAndGet())
            try {
                super.request(urlStr, mutableMap)
            } catch (e: Exception) {
                Log.e(TAG, "thread exception " + e.toString())
            }
            Log.e(TAG, "thread end " + toString() + " threads count:" + sThreadsCount.decrementAndGet())
        }
    }

    override fun cancel() {
        super.cancel()
        mThread?.interrupt()
    }
}