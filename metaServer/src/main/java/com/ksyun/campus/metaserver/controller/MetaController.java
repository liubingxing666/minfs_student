package com.ksyun.campus.metaserver.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ksyun.campus.metaserver.domain.FileInfo;
import com.ksyun.campus.metaserver.domain.FileType;
import com.ksyun.campus.metaserver.domain.StatInfo;
import com.ksyun.campus.metaserver.services.MetaService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.zookeeper.data.Stat;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController("/")
public class MetaController {
    @Autowired
    FileInfo fileInfo;
    StatInfo statInfo;
    MetaService metaService;
   // Map<String,StatInfo>infoMap=new LinkedHashMap<>();
    @Autowired
    public MetaController(StatInfo statInfo,MetaService metaService) {
        this.statInfo = statInfo;
        this.metaService=metaService;
    }

    @RequestMapping("infomaplist")
    public Map<String,StatInfo> infomaplist(){
        return metaService.infoMap;
    }
    //查看单个文件状态
    @RequestMapping("stats")
    public Object stats(@RequestHeader(name = "fileSystem", required = false)String fileSystem,@RequestParam String filename){
        System.out.println("hahahhaha");
        if(!metaService.infoMap.containsKey(filename)){
            return "没有这个文件";
        }else{
            System.out.println("wkajhd:"+metaService.infoMap.toString());
            return metaService.infoMap.get(filename);
        }

    }
    //lbx：创建文件索引
    @RequestMapping(value = "create",produces = "application/json")
    public Map<String, String> createFile(@RequestHeader(name = "fileSystem", required = false) String fileSystem, @RequestParam String path) throws IOException {
        System.out.println("path:"+path);
        Map<String,String> map=(Map<String,String>) metaService.create(path);
        return map;
      //return new ResponseEntity(HttpStatus.OK);
    }
    @RequestMapping("mkdir")
    public Map<String,StatInfo> mkdir(@RequestHeader(name = "fileSystem", required = false) String fileSystem,@RequestParam String path) throws IOException {
        System.out.println("mkdirpath:"+path);
        metaService.mkdir(path);

        return metaService.infoMap;
    }

    @RequestMapping("listdir")
    public List<String> listdir(@RequestHeader(name = "fileSystem", required = false) String fileSystem, @RequestParam(name = "path", required = false) String path) throws JsonProcessingException {
        System.out.println("hello");
        System.out.println(metaService.printInfoMap());
        return metaService.printInfoMap();
    }
    @RequestMapping("delete")
    public ResponseEntity delete(@RequestHeader(name = "fileSystem", required = false) String fileSystem, @RequestParam String path){

        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * 保存文件写入成功后的元数据信息，包括文件path、size、三副本信息等
     * @param fileSystem
     * @param path
     * @param offset
     * @param length
     * @return
     */
    @RequestMapping("write")
    public Object  commitWrite(@RequestHeader(name = "fileSystem", required = false) String fileSystem,
                                @RequestParam String path, @RequestParam(name = "offset", required = false) Integer offset,
                                @RequestParam(name = "length", required = false) Integer length,
                                @RequestParam String whichhost){
        log.info("path为：{},whichhost：{}",path,whichhost);
        OkHttpClient client = new OkHttpClient();
        String url="http://"+whichhost+"/updateSize?"+"path="+path;
        System.out.println("url："+url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        long fileSize=0;
        String fileName=(new File(path).getName());
        log.info("fileName:{}",fileName);
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                try {
                    fileSize = Long.parseLong(responseBody);
                    System.out.println("File size: " + fileSize);
                } catch (NumberFormatException e) {
                    System.err.println("Failed to parse response body as long: " + responseBody);
                }
            } else {
                System.err.println("Request failed with code: " + response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

         StatInfo statInfo = metaService.infoMap.get(fileName);
         statInfo.setSize(fileSize);
         metaService.infoMap.put(fileName,statInfo);

        return "hello";
    }

    /**
     * 根据文件path查询三副本的位置，返回客户端具体ds、文件分块信息
     * @param fileSystem
     * @param path
     * @return
     */
    @RequestMapping("open")
    public String open(@RequestHeader(name = "fileSystem", required = false) String fileSystem,@RequestParam String path){
        String fileName = metaService.getFileName(path);
        System.out.println("filename:"+fileName);

        String storePath=metaService.infoMap.get(fileName).getPath();
        System.out.println("storePath:"+storePath);
        System.out.println(metaService.getDataserverInfo());
        //其实此时要返回的是一个dataserver的信息，应该主要包括ip+端口就行了
        return storePath;
    }

    /**
     * 关闭退出进程
     */
    @RequestMapping("shutdown")
    public void shutdownServer(){
        System.exit(-1);
    }

}
