package com.ecjtu.netcore.network

import android.util.Log
import java.lang.Exception
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by KerriGan on 2017/7/14.
 */
class AsyncNetwork : BaseNetwork() {

    companion object {
        private const val TAG = "AsyncNetwork"

        private var sThreadsCount: AtomicInteger = AtomicInteger(0)

        @JvmStatic
        private val sFixedThreadPool = ThreadPoolExecutor(1, Runtime.getRuntime().availableProcessors() + 1,
                30L, TimeUnit.SECONDS,
                LinkedBlockingQueue()) // 阻塞队列防止Rejection异常
    }

    private var mFuture: Future<*>? = null

    override fun request(urlStr: String, mutableMap: MutableMap<String, String>?): BaseNetwork {
        mFuture = sFixedThreadPool.submit {
            Log.e(TAG, "task begin " + toString() + " task count:" + sThreadsCount.incrementAndGet())
            try {
                super.request(urlStr, mutableMap)
            } catch (e: Exception) {
                Log.e(TAG, "task exception " + e.toString())
            }
            Log.e(TAG, "task end " + toString() + " task count:" + sThreadsCount.decrementAndGet())
        }
        return this
    }

    override fun cancel() {
        super.cancel()
        mFuture?.cancel(true)
    }
}