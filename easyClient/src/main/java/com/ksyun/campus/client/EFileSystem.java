package com.ksyun.campus.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksyun.campus.client.domain.ClusterInfo;
import com.ksyun.campus.client.domain.StatInfo;
import com.ksyun.campus.client.util.OkHttpUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class EFileSystem extends FileSystem {

    private String fileName = "default";
    StatInfo statInfo=null;
    public EFileSystem() {
    }

    public EFileSystem(String fileName) {
        this.fileName = fileName;
    }

    //此函数的功能是打开一个文件，并返回一个输入流（FSInputStream）
    public FSInputStream open(String path) {
        //TODO 主备节点还需要完成
        //FSInputStream fsInputStream =new FSInputStream();
        return null;
    }

    //此函数的功能是创建一个新的文件，并返回一个输出流（FSOutputStream），以便向文件中写入数据。
    public FSOutputStream create(String path) {
//        //不能带test,sb问题
//        String url3="http://localhost:8000/create?path=/test1/dd.c";
//        OkHttpClient client = new OkHttpClient();
//
//
//        Request request = new Request.Builder()
//                .url(url3)
//                .build();
//
//        try (Response response = client.newCall(request).execute()) {
//            if (response.isSuccessful()) {
//                String responseBody = response.body().string();
//                System.out.println("Response Body:");
//                System.out.println(responseBody);
//            } else {
//                System.out.println("Request failed with code: " + response.code());
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        FSOutputStream outputStream = new FSOutputStream();
        String url = "http://localhost:8000/create";
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("path", path);
        try {
            outputStream.zhuanfaMap = OkHttpUtils.sendGetRequesttoJson(url, queryParams);
            for (Map.Entry<String, String> entry : outputStream.zhuanfaMap.entrySet()) {
                System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
            }
    } catch(
    IOException e)

    {
        e.printStackTrace();
    }
        return outputStream;
}

    //创建文件夹
    public boolean mkdir(String path) {
        String url = "http://127.0.0.1:8000/mkdir";
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("path", path);
        try {
            String responseBody = OkHttpUtils.sendGetRequestMkdir(url, queryParams);
            System.out.println("Response: " + responseBody);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    //删除文件或者文件夹
    public boolean delete(String path) {
        return false;
    }

    //获取当个文件属性
    public StatInfo getFileStats(String path) {

        fileName = new File(path).getName();
        System.out.println(fileName);
        OkHttpClient client = new OkHttpClient();
        String url="http://"+"localhost:8000"+"/stats?filename="+fileName;
        System.out.println("url："+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                System.out.println("Response body: " + responseBody);
                ObjectMapper objectMapper = new ObjectMapper();
                statInfo = objectMapper.readValue(responseBody, StatInfo.class);
                System.out.println("statinfo:"+statInfo.toString());
            } else {
                System.err.println("Request failed with code: " + response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    //获取所有文件属性
    public List<StatInfo> listFileStats(String path) {
        return null;
    }

    //主备meta节点。四个data节点
    public ClusterInfo getClusterInfo() {
        return null;
    }


}
