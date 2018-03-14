package com.ecjtu.flesh.util

import java.io.Closeable

/**
 * Created by Ethan_Xiang on 2018/3/14.
 */
object CloseableUtil {

    fun closeQuitely(closeable: AutoCloseable?): Boolean {
        try {
            closeable?.close()
            return true
        } catch (ex: Exception) {
            ex.printStackTrace()
            return false
        }
    }

    fun closeQuitely(closeable: Closeable?): Boolean {
        try {
            closeable?.close()
            return true
        } catch (ex: Exception) {
            ex.printStackTrace()
            return false
        }
    }

}