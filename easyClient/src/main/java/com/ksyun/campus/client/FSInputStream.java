package com.ksyun.campus.client;

import java.io.IOException;
import java.io.InputStream;

public class FSInputStream extends InputStream {
//    @Override
//    public int read() throws IOException {
//        return 0;
//    }
//
//    @Override
//    public int read(byte[] b) throws IOException {
//        return super.read(b);
//    }
//
//    @Override
//    public int read(byte[] b, int off, int len) throws IOException {
//        return super.read(b, off, len);
//    }
//
//    @Override
//    public void close() throws IOException {
//        super.close();
//    }
    private byte[] data; // 假设数据源为一个字节数组
    private int position; // 当前读取位置

    public FSInputStream(byte[] data) {
        this.data = data;
        this.position = 0;
    }

    @Override
    public int read() throws IOException {
        if (position >= data.length) {
            return -1; // 已经读取到数据末尾，返回 -1 表示没有更多数据可读
        }
        return data[position++];
    }

    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (position >= data.length) {
            return -1; // 已经读取到数据末尾，返回 -1 表示没有更多数据可读
        }
        int bytesRead = Math.min(len, data.length - position);
        System.arraycopy(data, position, b, off, bytesRead);
        position += bytesRead;
        return bytesRead;
    }

    @Override
    public void close() throws IOException {
        // 可以在这里进行资源释放操作
        super.close();
    }


}
