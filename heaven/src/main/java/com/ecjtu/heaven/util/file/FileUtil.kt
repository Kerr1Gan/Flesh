package com.ecjtu.heaven.util.file

import java.io.File

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
}