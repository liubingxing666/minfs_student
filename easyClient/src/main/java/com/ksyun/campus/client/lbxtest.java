package com.ksyun.campus.client;

import com.ksyun.campus.client.util.OkHttpUtils;
import com.ksyun.campus.client.util.ZkUtil;
import org.slf4j.MDC;

import java.io.IOException;
import java.net.FileNameMap;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class lbxtest {
    public static void main(String[] args) throws IOException {
        lbxtest lbxtest=new lbxtest();
        lbxtest.testZKCOnnect();
        System.out.println();
    }
    public void test_mkdir_and_file() throws IOException {
        EFileSystem eFileSystem=new EFileSystem();
        //eFileSystem.mkdir("/ead/sss");
        // eFileSystem.create("/test/ss.txt");
        //eFileSystem.create("/lbx/dashuaibi.txt");
        FSOutputStream fsOutputStream= eFileSystem.create("/aqqq/hahha.txt");
        fsOutputStream.write("真厉害asdadda呀".getBytes(StandardCharsets.UTF_8));

        eFileSystem.getFileStats("/test/hahha.txt");
    }
    public void testZKCOnnect(){
        EFileSystem eFileSystem=new EFileSystem();
        ZkUtil zkUtil =new ZkUtil();
        System.out.println(zkUtil.id);
    }
}
