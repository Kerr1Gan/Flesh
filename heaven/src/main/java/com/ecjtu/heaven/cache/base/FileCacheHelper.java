package com.ecjtu.heaven.cache.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.channels.FileLock;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by Ethan_Xiang on 2017/7/21.
 * 该类可以跨进程使用
 */
public class FileCacheHelper {

    private ReentrantReadWriteLock mReentrantLock;

    private String mPath;

    private Lock mReadLock;

    private Lock mWriteLock;

    public FileCacheHelper(String path) {
        mPath = path;
        mReentrantLock = new ReentrantReadWriteLock();
        mReadLock = mReentrantLock.readLock();
        mWriteLock = mReentrantLock.writeLock();
    }

    public boolean put(String key, Object object) {
        boolean ret = false;
        try {
            mWriteLock.lockInterruptibly();
            ret = persistObject(key, object);
        } catch (InterruptedException e) {
            e.printStackTrace();
            ret = false;
        } finally {
            mWriteLock.unlock();
        }
        return ret;
    }

    public <T> T get(String key) {
        T ret = null;
        try {
            mReadLock.lockInterruptibly();
            ret = (T) readObject(key);
        } catch (InterruptedException e) {
            e.printStackTrace();
            ret = null;
        } finally {
            mReadLock.unlock();
        }
        return ret;
    }

    protected <T> boolean persistObject(String key, T object) {
        File file = new File(mPath, key + "@@@@");
        if (file.exists()) file.delete();
        FileOutputStream fos = null;
        boolean ret = false;
        FileLock fileLock = null;
        try {
            fos = new FileOutputStream(file);
            fileLock = fos.getChannel().lock();
            writeObjectFromStream(fos, object);
            ret = true;
            file.renameTo(new File(mPath, key));
        } catch (Exception e) {
            e.printStackTrace();
            ret = false;
        } finally {
            if (fileLock != null) {
                try {
                    fileLock.release();
                } catch (IOException e) {
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
        }
        return ret;
    }

    protected <T> T readObject(String key) {
        File file = new File(mPath, key);
        if (!file.exists()) return null;

        T ret = null;
        FileLock fileLock = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            fileLock = fis.getChannel().lock(0L, Long.MAX_VALUE, true);
            ret = readObjectFromStream(fis);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileLock != null) {
                try {
                    fileLock.release();
                } catch (IOException e) {
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                }
            }
        }
        return ret;
    }

    protected <T> T readObjectFromStream(FileInputStream is) throws IOException, ClassNotFoundException {
        ObjectInputStream fis = new ObjectInputStream(is);
        return (T) fis.readObject();
    }

    protected <T> void writeObjectFromStream(FileOutputStream os, T object) throws IOException, ClassNotFoundException {
        ObjectOutputStream fos = new ObjectOutputStream(os);
        fos.writeObject(object);
    }
}
