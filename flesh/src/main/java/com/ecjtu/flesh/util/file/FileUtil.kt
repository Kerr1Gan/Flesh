package com.ecjtu.flesh.util.file

import android.content.Context
import java.io.*
import java.lang.Exception

/**
 * Created by Ethan_Xiang on 2017/9/14.
 */
object FileUtil {
    fun getFilesByFolder(root: File, out: MutableList<File>? = null): MutableList<File> {
        var list = out
        if (list == null) {
            list = mutableListOf<File>()
        }

        if (!root.exists()) return list
        if (root.isDirectory) {
            var childList = root.listFiles()
            for (child in childList) {
                if (child.isDirectory) {
                    list = getFilesByFolder(child, list)
                } else {
                    list?.add(child)
                }
            }
        } else {
            list.add(root)
        }
        return list!!
    }

    fun copyFile2Path(src: File, dest: File): Boolean {
        var fis: FileInputStream? = null
        var buf: BufferedOutputStream? = null

        try {
            fis = FileInputStream(src)
            buf = BufferedOutputStream(FileOutputStream(dest))
            copyFile(fis, buf)
        } catch (e: Exception) {
            return false
        } finally {
            fis?.close()
            buf?.close()
        }
        return true
    }

    @Throws(IOException::class)
    fun copyFile(inputStream: InputStream, outputStream: BufferedOutputStream) {
        val arr = ByteArray(1024 * 5)
        var len = inputStream.read(arr)
        while (len > 0) {
            outputStream.write(arr)
            len = inputStream.read(arr)
        }
    }

    fun getGlideCacheSize(context: Context): Long {
        var cacheFile = File(context.cacheDir.absolutePath + "/image_manager_disk_cache")
        var size = 0L
        var list = FileUtil.getFilesByFolder(cacheFile)
        var ret = 0L
        for (child in list) {
            ret += child.length()
        }
        return ret
    }

    fun readFileContent(file: File): ByteArray? {
        var fis: FileInputStream? = null
        var buf: ByteArrayOutputStream? = null
        var ret: ByteArray? = null
        try {
            fis = FileInputStream(file)
            buf = ByteArrayOutputStream()
            var byteArr = ByteArray(1024 * 2)
            var len = fis.read(byteArr)
            while (len > 0) {
                buf.write(byteArr, 0, len)
                len = fis.read(byteArr)
            }
            ret = buf.toByteArray()
        } catch (ex: Exception) {
            ex.printStackTrace()
            ret = null
        } finally {
            fis?.close()
            buf?.close()
        }
        return ret
    }
}