package tv.danmaku.ijk.media.player;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import tv.danmaku.ijk.media.player.misc.IMediaDataSource;

/**
 * Created by Ethan_Xiang on 2018/2/22.
 */

public class InputStreamSourceProvider implements IMediaDataSource {
    private InputStream mDescriptor;

    private byte[] mMediaBytes;

    private int mSize;

    public InputStreamSourceProvider(InputStream descriptor) {
        this.mDescriptor = descriptor;
    }

    @Override
    public int readAt(long position, byte[] buffer, int offset, int size) throws IOException {
        if (position + 1 >= mMediaBytes.length) {
            return -1;
        }

        int length;
        if (position + size < mMediaBytes.length) {
            length = size;
        } else {
            length = (int) (mMediaBytes.length - position);
            if (length > buffer.length)
                length = buffer.length;

//            length--;
        }
        System.arraycopy(mMediaBytes, (int) position, buffer, offset, length);

        return length;
    }

    @Override
    public long getSize() throws IOException {
        long length = 0;
//        if (mMediaBytes == null) {
//            InputStream inputStream = mDescriptor.createInputStream();
//            mMediaBytes = readBytes(inputStream);
//        }
        return length;
    }

    @Override
    public void close() throws IOException {
        if (mDescriptor != null)
            mDescriptor.close();

        mDescriptor = null;
        mMediaBytes = null;
    }

    private byte[] readBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        return byteBuffer.toByteArray();
    }

}
