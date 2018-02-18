package com.ecjtu.flesh.util.encrypt

import java.security.MessageDigest
import kotlin.experimental.and

/**
 * Created by KerriGan on 2018/2/18.
 */
object MD5Utils {
    fun MD5(s: String): String {
        try {
            val md = MessageDigest.getInstance("MD5")
            val bytes = md.digest(s.toByteArray(charset("utf-8")))
            return toHex(bytes)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    }

    fun toHex(bytes: ByteArray): String {
        val HEX_DIGITS = "0123456789ABCDEF".toCharArray()
        val ret = StringBuilder(bytes.size * 2)
        for (i in bytes.indices) {
            ret.append(HEX_DIGITS[bytes[i].toInt() shr 4 and 0x0f])
            ret.append(HEX_DIGITS[(bytes[i] and 0x0f).toInt()])
        }
        return ret.toString()
    }
}