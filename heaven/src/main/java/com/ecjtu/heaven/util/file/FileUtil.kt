package com.ecjtu.heaven.util.file

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
}