package com.ksyun.campus.client.util;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OkHttpUtils {

    private static final OkHttpClient client = new OkHttpClient();

    public static String sendGetRequest(String url, Map<String, String> queryParams) throws IOException {
        System.out.println(url);
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        if (queryParams != null) {
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }
        String finalUrl="http://localhost:9001/write?path=E:\\Workspace_ALL\\ksyun\\minfs_student\\dataServer\\target\\localhost_9001\\lyx\\hahha.txt&byte=sdas";
       // String finalUrl = urlBuilder.build().toString();
        String encodedPath = URLEncoder.encode(finalUrl, "UTF-8");

        System.out.println("finalUrl:"+finalUrl);
        Request request = new Request.Builder()
                .url(finalUrl.replace("\\","/"))
                .build();
        System.out.println(finalUrl.replace("\\","/"));
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return response.body().string();
            } else {
                throw new IOException("Request failed with code: " + response.code());
            }
        }
    }

    //创建文件夹用到的

    public static String sendGetRequestMkdir(String url, Map<String, String> queryParams) throws IOException {
        System.out.println(url);
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        if (queryParams != null) {
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }
         String finalUrl = urlBuilder.build().toString();
        //String encodedPath = URLEncoder.encode(finalUrl, "UTF-8");

        System.out.println("finalUrl:"+finalUrl);
        Request request = new Request.Builder()
                .url(finalUrl.replace("\\","/"))
                .build();
        System.out.println(finalUrl.replace("\\","/"));
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return response.body().string();
            } else {
                throw new IOException("Request failed with code: " + response.code());
            }
        }
    }


    //创建文件时用到的
    public static Map<String, String> sendGetRequesttoJson(String url, Map<String, String> queryParams) throws IOException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        if (queryParams != null) {
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }
        String finalUrl = urlBuilder.build().toString();
        System.out.println("finalUrl:"+finalUrl);
        Request request = new Request.Builder()
                .url(finalUrl)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                Gson gson = new Gson();
                Type type = new TypeToken<Map<String, String>>() {}.getType();
                Map<String, String> responseMap = gson.fromJson(responseBody, type);
                return responseMap;
            } else {
                throw new IOException("Request failed with code: " + response.code());
            }
        }
    }
}
