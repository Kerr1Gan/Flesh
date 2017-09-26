package com.ecjtu.flesh.cache.base;

import android.os.Parcel;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Ethan_Xiang on 2017/8/21.
 */
public abstract class ParcelableFileCacheHelper extends FileCacheHelper {

    private ByteArrayOutputStream mByteArrayOutputStream;

    public ParcelableFileCacheHelper(String path) {
        super(path);
    }

    @Override
    protected <T> T readObjectFromStream(FileInputStream is) throws IOException, ClassNotFoundException {
        mByteArrayOutputStream = new ByteArrayOutputStream();
        int len;
        byte[] buf = new byte[1024 * 1024];
        while ((len = is.read(buf)) > 0) {
            mByteArrayOutputStream.write(buf, 0, len);
        }
        Parcel parcel = Parcel.obtain();
        buf = mByteArrayOutputStream.toByteArray();
        parcel.unmarshall(buf, 0, buf.length);
        parcel.setDataPosition(0);
        int version = parcel.readInt();
        if (version != getVersion()) {
            if (getVersion() <= 0) {
                throw new IllegalStateException("getVersion() must be >0");
            }
            return null;
        }
        //read parcel
        T ret = readParcel(parcel);

        parcel.recycle();
        gc();
        mByteArrayOutputStream.close();
        return ret;
    }

    @Override
    protected <T> void writeObjectFromStream(FileOutputStream os, T object) throws IOException, ClassNotFoundException {
        Parcel parcel = Parcel.obtain();
        parcel.writeInt(getVersion());
        parcel = writeParcel(parcel, object);
        if (parcel != null) {
            os.write(parcel.marshall());
            parcel.recycle();
        }
        gc();
    }

    @Override
    protected <T> T readObject(String key) {
        T ret = super.readObject(key);
        try {
            if (mByteArrayOutputStream != null) {
                mByteArrayOutputStream.close();
                mByteArrayOutputStream = null;
            }
        } catch (IOException e) {
        }
        return ret;
    }

    @Override
    protected <T> boolean persistObject(String key, T object) {
        return super.persistObject(key, object);
    }

    private void gc() {
        System.gc();
        System.runFinalization();
        System.gc();
    }

    protected abstract <T> T readParcel(Parcel parcel);

    protected abstract <T> Parcel writeParcel(Parcel parcel, T object);

    public int getVersion() {
        return 1;
    }
}
