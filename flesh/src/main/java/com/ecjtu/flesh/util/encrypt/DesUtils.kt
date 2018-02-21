package com.ecjtu.flesh.util.encrypt

import java.io.*
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
    fun encryptFile(file: String, destFile: String, key: Key) {
        val fileInput = FileInputStream(file)
        val fileOutput = FileOutputStream(destFile)
        try {
            encrypt(fileInput, fileOutput, key)
        } catch (ex: Exception) {
            ex.printStackTrace()
            throw ex
        } finally {
            try {
                fileInput.close()
            } catch (ex: Exception) {
            }
            try {
                fileOutput.close()
            } catch (ex: Exception) {
            }
        }
    }

    /**
     * 文件采用DES算法解密文件
     *
     * @param file 已加密的文件 如c:/加密后文件.txt
     * * @param destFile
     * 解密后存放的文件名 如c:/ test/解密后文件.txt
     */
    @Throws(Exception::class)
    fun decryptFile(file: String, dest: String, key: Key) {
        val fileInput = FileInputStream(file)
        val fileOutput = FileOutputStream(dest)
        try {
            decrypt(fileInput, fileOutput, key)
        } catch (ex: Exception) {
            ex.printStackTrace()
            throw ex
        } finally {
            try {
                fileInput.close()
            } catch (ex: Exception) {
            }
            try {
                fileOutput.close()
            } catch (ex: Exception) {
            }
        }
    }

    fun encryptBytes(origin: ByteArray, key: Key): ByteArray? {
        val byteInput = ByteArrayInputStream(origin)
        val byteOutput = ByteArrayOutputStream()
        try {
            encrypt(byteInput, byteOutput, key)
        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            val ret = byteOutput.toByteArray()
            try {
                byteInput.close()
            } catch (e: Exception) {
            }
            try {
                byteOutput.close()
            } catch (e: Exception) {
            }
            return ret
        }
    }

    fun decryptBytes(origin: ByteArray, key: Key): ByteArray? {
        val byteInput = ByteArrayInputStream(origin)
        val byteOutput = ByteArrayOutputStream()
        try {
            decrypt(byteInput, byteOutput, key)
        } catch (ex: Exception) {
            ex.printStackTrace()
        } finally {
            val ret = byteOutput.toByteArray()
            try {
                byteInput.close()
            } catch (e: Exception) {
            }
            try {
                byteOutput.close()
            } catch (e: Exception) {
            }
            return ret
        }
    }

    /**
     * 文件file进行加密并保存目标文件destFile中
     *
     * @param file     要加密的文件 如c:/test/srcFile.txt
     * @param destFile 加密后存放的文件名 如c:/加密后文件.txt
     */
    @Throws(Exception::class)
    fun encrypt(origin: InputStream, dest: OutputStream, key: Key) {
        val cipher = Cipher.getInstance("DES")
        // cipher.init(Cipher.ENCRYPT_MODE, getKey());
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val `is` = origin
        val out = dest
        val cis = CipherInputStream(`is`, cipher)
        try {
            val buffer = ByteArray(1024)
            do {
                val len = cis.read(buffer)
                if (len <= 0) break
                out.write(buffer, 0, len)
            } while (len > 0)
        } catch (ex: Exception) {
            ex.printStackTrace()
            throw ex
        } finally {
            try {
                cis.close()
            } catch (ex: Exception) {
            }
        }
    }

    /**
     * 文件采用DES算法解密文件
     *
     * @param file 已加密的文件 如c:/加密后文件.txt
     * * @param destFile
     * 解密后存放的文件名 如c:/ test/解密后文件.txt
     */
    @Throws(Exception::class)
    fun decrypt(origin: InputStream, dest: OutputStream, key: Key) {
        val cipher = Cipher.getInstance("DES")
        cipher.init(Cipher.DECRYPT_MODE, key)
        val `is` = origin
        val out = dest
        val cos = CipherOutputStream(out, cipher)
        try {
            val buffer = ByteArray(1024)
            do {
                val len = `is`.read(buffer)
                if (len <= 0) break
                cos.write(buffer, 0, len)
            } while (len > 0)
        } catch (ex: Exception) {
            ex.printStackTrace()
            throw ex
        } finally {
            try {
                cos.close()
            } catch (ex: Exception) {
            }
        }
    }
}