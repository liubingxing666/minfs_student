package com.ksyun.campus.dataserver.services;


import org.springframework.stereotype.Service;

import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Paths;

@Service
public class DataService {

    //private String dataPath="C:/Users/liubingxin/Desktop/Desktop Mess";
    public void write(byte[] data){
        //todo 写本地

        //todo 调用远程ds服务写接口，同步副本，已达到多副本数量要求
        //todo 选择策略，按照 az rack->zone 的方式选取，将三副本均分到不同的az下
        //todo 支持重试机制
        //todo 返回三副本位置
    }

    public void writeFile(String fileSystem, String path, int offset, int length, String data) {
       // String fullPath = fileSystem + File.separator + path;
        try (FileOutputStream fos = new FileOutputStream(path, false)) {
            byte[] decodedData = URLDecoder.decode(data, "UTF-8").getBytes();
            fos.write(decodedData, offset, length);
        } catch (IOException e) {
            System.out.println("error");
        }
    }


//    public byte[] read(String path,int offset,int length){
//        //todo 根据path读取指定大小的内容
//        return null;
//    }

    public byte[] read(String path, int offset, int length) throws IOException {
        Path filePath = Paths.get(path);
        RandomAccessFile raf = new RandomAccessFile(filePath.toFile(), "r");
        raf.seek(offset);
        byte[] data = new byte[length];
        int n = raf.read(data);
        raf.close();
        return data;
    }


}
