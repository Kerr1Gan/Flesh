package com.ecjtu.flesh.util.encrypt

import com.ecjtu.flesh.Constants
import java.io.InputStream
import java.io.ObjectInputStream
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

/**
 * Created by KerriGan on 2018/2/18.
 */
object SecretKeyUtils {
    fun readSecretKey(inputStream: InputStream): SecretKey? {
        var ret: SecretKey? = null
        try {
            val input = ObjectInputStream(inputStream)
            ret = input.readObject() as SecretKey?
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return ret
    }

    fun getKeyFromServer(): SecretKey? {
        try {
            val url = URL(Constants.SECRETE_KEY)
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 3000
            val `is` = connection.inputStream
            return SecretKeyUtils.readSecretKey(`is`)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    // 可逆的加密算法
    fun encode(inStr: String): String {
        return encode(inStr, 's')
    }

    fun encode(inStr: String, key: Char): String {
        // String s = new String(inStr);
        val a = inStr.toCharArray()
        for (i in a.indices) {
            a[i] = (a[i].toInt() xor key.toInt()).toChar()
        }
        return String(a)
    }
}