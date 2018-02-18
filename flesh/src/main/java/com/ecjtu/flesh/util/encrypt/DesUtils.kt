package com.ecjtu.flesh.util.encrypt

import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.Key
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.KeyGenerator


/**
 * Created by KerriGan on 2018/2/18.
 */
object DesUtils {
    /**
     * 根据参数生成KEY
     */
    fun generateKey(strKey: String): Key? {
        try {
            var generator = KeyGenerator.getInstance("DES")
            generator!!.init(SecureRandom(strKey.toByteArray()))
            return generator.generateKey()
        } catch (e: Exception) {
            throw RuntimeException("Error initializing SqlMap class. Cause: " + e)
        }
        return null
    }

    /**
     * 文件file进行加密并保存目标文件destFile中
     *
     * @param file     要加密的文件 如c:/test/srcFile.txt
     * @param destFile 加密后存放的文件名 如c:/加密后文件.txt
     */
    @Throws(Exception::class)
    fun encrypt(file: String, destFile: String, key: Key) {
        val cipher = Cipher.getInstance("DES")
        // cipher.init(Cipher.ENCRYPT_MODE, getKey());
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val `is` = FileInputStream(file)
        val out = FileOutputStream(destFile)
        val cis = CipherInputStream(`is`, cipher)
        val buffer = ByteArray(1024)
        do {
            val len = cis.read(buffer)
            if (len <= 0) break
            out.write(buffer, 0, len)
        } while (len > 0)
        cis.close()
        `is`.close()
        out.close()
    }

    /**
     * 文件采用DES算法解密文件
     *
     * @param file 已加密的文件 如c:/加密后文件.txt
     * * @param destFile
     * 解密后存放的文件名 如c:/ test/解密后文件.txt
     */
    @Throws(Exception::class)
    fun decrypt(file: String, dest: String, key: Key) {
        val cipher = Cipher.getInstance("DES")
        cipher.init(Cipher.DECRYPT_MODE, key)
        val `is` = FileInputStream(file)
        val out = FileOutputStream(dest)
        val cos = CipherOutputStream(out, cipher)
        val buffer = ByteArray(1024)
        do {
            val len = `is`.read(buffer)
            if (len <= 0) break
            cos.write(buffer, 0, len)
        } while (len > 0)
        cos.close()
        out.close()
        `is`.close()
    }
}