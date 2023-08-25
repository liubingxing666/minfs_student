package com.ksyun.campus.dataserver.controller;

import com.ksyun.campus.dataserver.services.DataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController("/")
public class DataController {
    final DataService dataService;
    public DataController(DataService dataService) {
        this.dataService = dataService;
    }

    /**
     * 1、读取request content内容并保存在本地磁盘下的文件内
     * 2、同步调用其他ds服务的write，完成另外2副本的写入
     * 3、返回写成功的结果及三副本的位置
     * @param fileSystem
     * @param path
     * @param offset
     * @param length
     * @return
     */
    @RequestMapping("write")
    public String writeFile(@RequestHeader(name = "fileSystem", required = false) String fileSystem,
                                    @RequestParam String path,
                                    @RequestParam(name = "offset", required = false) Integer offset,
                                    @RequestParam (name = "length", required = false)Integer length,
                                    @RequestParam String data
    ) throws IOException {
        //FileOutputStream fileOutputStream=null;
        if(offset==null&&length==null){

            try (FileOutputStream fileOutputStream = new FileOutputStream(path)) {
                System.out.println("path:" + path);
                fileOutputStream.write(data.getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                // 处理异常
            }
//            try {
//                System.out.println("success");
//                fileOutputStream=new FileOutputStream(path);
//                System.out.println("path:"+path);
//                fileOutputStream.write(data.getBytes(StandardCharsets.UTF_8));
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }

        }

        return path+"写入完成";

    }

    @GetMapping("updateSize")
    public long updatesize(@RequestParam String path){
        File file = new File(path);
        //只是创建了一个File对象来查询文件大小。由于没有在文件上打开活动流或读取器，不需要关闭任何东西。
        long fileSize=0;
        if (file.exists() && file.isFile()) {
            fileSize = file.length();
            log.info("File size of {} is {} bytes",path,fileSize);
        } else {
            System.out.println("File not found or not a regular file: " + path);
        }
        return fileSize;
    }
//    @RequestMapping(value = "/write", method = RequestMethod.GET)
//    public ResponseEntity<String> writeFile(@RequestHeader String fileSystem,
//                                            @RequestHeader String path,
//                                            @RequestHeader int offset,
//                                            @RequestHeader int length,
//                                            @RequestHeader String data) {
//        dataService.writeFile(fileSystem,path,offset,length,data);
//        return new ResponseEntity(HttpStatus.OK);
//
//    }

    /**
     * 在指定本地磁盘路径下，读取指定大小的内容后返回
     * @param fileSystem
     * @param path
     * @param offset
     * @param length
     * @return
     */
    @RequestMapping("read")
    public ResponseEntity readFile(@RequestHeader String fileSystem, @RequestParam String path, @RequestParam int offset, @RequestParam int length) throws IOException {

        byte[] data= dataService.read(path, offset, length);
        String str = new String(data);
        log.info("str:"+str);

        return new ResponseEntity(str,HttpStatus.OK);
    }
    /**
     * 关闭退出进程
     */
    @RequestMapping("shutdown")
    public void shutdownServer(){
        System.exit(-1);
    }
}
