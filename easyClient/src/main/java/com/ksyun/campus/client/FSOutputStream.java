package com.ksyun.campus.client;

import com.ksyun.campus.client.util.OkHttpUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

public class FSOutputStream extends OutputStream {


    public List<String> pathList=new ArrayList<>();
    public Map<String,String>zhuanfaMap=new HashMap<>();
    @Override
    public void write(int b) throws IOException {

    }

    @Override
    public void write(byte[] b) throws IOException {
        FileOutputStream fileOutputStream=null;

        String commitPath=null;
        //提交给哪台dataserver
        String commmitWhichdataServer=null;
        for (Map.Entry<String, String> entry : zhuanfaMap.entrySet()) {

//            String url = "http://"+entry.getKey()+"/write";
//            Map<String, String> queryParams = new HashMap<>();
//            queryParams.put("path", entry.getValue());
//            queryParams.put("byte",b.toString());
//            String xxx = OkHttpUtils.sendGetRequest(url, queryParams);
            OkHttpClient client = new OkHttpClient();
            String data=new String(b, "UTF-8");
            System.out.println("b:"+ data);
            commitPath=entry.getValue().replace("\\","/");
            commmitWhichdataServer=entry.getKey();
            String url = "http://"+entry.getKey()+"/write?"+"path="+commitPath+"&data="+data; // 替换为你的请求 URL
            System.out.println("finalurl:"+url);
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    System.out.println("Response body: " + responseBody);
                } else {
                    System.err.println("Request failed with code: " + response.code());
                    //如果三个副本里面有一个没写成功，直接退掉
                    return ;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("Key1: " + entry.getKey() + ", Value1: " + entry.getValue());
        }

        //提交commit更新信息,url为
        System.out.println("commitPath:"+commitPath);

        OkHttpClient client = new OkHttpClient();
        String url="http://"+"localhost:8000"+"/write?path="+commitPath+"&whichhost="+commmitWhichdataServer;
        System.out.println("url："+url);
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                System.out.println("Response body: " + responseBody);
            } else {
                System.err.println("Request failed with code: " + response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }



        //super.write(b);
    }



    @Override
    public void write(byte[] b, int off, int len) throws IOException {


        super.write(b, off, len);
    }

    @Override
    public void close() throws IOException {
        super.close();
    }
}
