package com.ksyun.campus.metaserver.services;

import ch.qos.logback.core.util.TimeUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksyun.campus.metaserver.domain.FileInfo;
import com.ksyun.campus.metaserver.domain.FileType;
import com.ksyun.campus.metaserver.domain.ReplicaData;
import com.ksyun.campus.metaserver.domain.StatInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Service
@Slf4j
public class MetaService {

    StatInfo statInfo;
    public Map<String, StatInfo> infoMap = new LinkedHashMap<>();

    @Autowired
    public MetaService(StatInfo statInfo) {
        this.statInfo = statInfo;
    }

    @Value("${zookeeper.addr}")
    String connectionString;

    //    public String create(String path) throws JsonProcessingException {
//        File file = new File(path);
//        //获取文件创建时间
//        LocalDateTime.now();
//        LocalDateTime now = LocalDateTime.now();
//        Instant instant = now.toInstant(ZoneOffset.UTC);
//        long ctime= instant.toEpochMilli();
//        statInfo.setMtime(ctime);
//        statInfo.setSize(20000);
//        statInfo.setType(FileType.get(2));
//        log.info("文件创建时间为："+ctime);
//
////        log.info("statInfo:"+statInfo.toString());
//        try {
//            if (file.createNewFile()) {
//                String absolutePath = file.getAbsolutePath();
//                System.out.println("文件创建成功：" + absolutePath);
//                String fileName=getFileName(absolutePath);
//                statInfo.setPath(absolutePath);
//                ObjectMapper objectMapper=new ObjectMapper();
//                String json = objectMapper.writeValueAsString(statInfo);
//                System.out.println("statinfo:"+json);
//                return absolutePath;
//            } else {
//                System.out.println("文件已存在：" + file.getAbsolutePath());
//                return null;
//            }
//        } catch (IOException e) {
//            System.err.println("创建文件时出现异常：" + e.getMessage());
//            e.printStackTrace();
//            return null;
//        }
//    }
    public Object create(String path) throws IOException {
        String[] ss = path.split("/");
        String[] filtered = Arrays.stream(ss).filter(s -> !s.isEmpty()).toArray(String[]::new);
        String pingjiepath = "";
        for (String s : filtered) {
            System.out.println("s：" + s);
            pingjiepath = pingjiepath + File.separator + s;
        }
        // 获取zk的节点信息
        List<String> arrlist = (List<String>) getDataserverInfo();
        //文件存储位置
        List<String> fileSumStore=new ArrayList<>();
        //TODO 现在是随机获取三个node，后面要考虑容量
        Map<String, String> nodeMap = getNodeInfoMap(arrlist);

        //absoluteNodeMap存的是三副本存储路径以及对应的端口
        Map<String,String>absoluteNodeMap=new LinkedHashMap<>();
        System.out.println("nodeMap:" + nodeMap);
        List<ReplicaData> replicaData = new ArrayList<>();
        StatInfo statInfo1 = new StatInfo();
        String fileName = null;
        //将文件信息存在InfoMap中，并创建三副本文件
        for (Map.Entry<String, String> entry : nodeMap.entrySet()) {
            //拼接路径
            System.out.println("pingjie:" + pingjiepath);
            String sumPath = entry.getValue() + pingjiepath;
            log.info("sumPath:" + sumPath);
            fileSumStore.add(sumPath);
            absoluteNodeMap.put(entry.getKey(),sumPath);
            File file = new File(sumPath);
            String absolutePath = file.getAbsolutePath();

            fileName = file.getName();
            if (file.exists() && file.isFile()) {
                log.info("File '" + fileName + "' already exists in MetaServer.");
                log.info("{},{}",file.exists(),file.getAbsolutePath());
                if (file.delete()) {
                    log.info("File '" + fileName + "' deleted successfully.");
                    infoMap.remove(fileName);
                } else {
                    log.error("Failed to delete existing file: " + fileName);
                }
            }
            //创建文件及文件夹
            file.getParentFile().mkdirs(); // 创建父目录结构
            file.createNewFile();
           // if (!infoMap.containsKey(fileName)) {

                ReplicaData data = new ReplicaData();
                data.dsNode = entry.getKey();
                data.path = sumPath;
                data.id = entry.getKey();
                replicaData.add(data);

                statInfo1.setPath(absolutePath);
                statInfo1.setSize(file.length());
                statInfo1.setMtime(getTime());
                statInfo1.setType(FileType.get(2));


                log.info("File '" + fileName + "' created successfully in MetaServer.");
                printInfoMap();
            //} else {
             //   log.info("File '" + fileName + "' aleady created  in MetaServer.");
             //   printInfoMap();
           // }

        }
        statInfo1.setReplicaData(replicaData);
        infoMap.put(fileName, statInfo1);
        //
        log.info("absoluteNodeMap:{}",absoluteNodeMap);

        return absoluteNodeMap;
    }

        public Object mkdir(String path) throws IOException {
            System.out.println("receive path:"+path);
            String[] ss = path.split("/");
            String[] filtered = Arrays.stream(ss).filter(s -> !s.isEmpty()).toArray(String[]::new);
            String pingjiepath = "";
            for (String s : filtered) {
                System.out.println("s：" + s);
                pingjiepath = pingjiepath + File.separator + s;
            }
            // 获取zk的节点信息
            List<String> arrlist = (List<String>) getDataserverInfo();
            //文件存储位置
            List<String> fileSumStore=new ArrayList<>();
            //TODO 现在是随机获取三个node，后面要考虑容量
            Map<String, String> nodeMap = getNodeInfoMap(arrlist);
            Map<String,String>absoluteNodeMap=new LinkedHashMap<>();
            System.out.println("nodeMap:" + nodeMap);
            List<ReplicaData> replicaData = new ArrayList<>();
            StatInfo statInfo1 = new StatInfo();
            String fileName = null;
            //将文件信息存在InfoMap中，并创建三副本文件
            for (Map.Entry<String, String> entry : nodeMap.entrySet()) {
                //拼接路径
                System.out.println("pingjie:" + pingjiepath);
                String sumPath = entry.getValue() + pingjiepath;
                log.info("sumPath:" + sumPath);
                fileSumStore.add(sumPath);
                absoluteNodeMap.put(entry.getKey(),sumPath);
                File file = new File(sumPath);
                String absolutePath = file.getAbsolutePath();

                fileName = file.getName();
                if (file.exists() && file.isFile()) {
                    log.info("File '" + fileName + "' already exists in MetaServer.");
                    log.info("{},{}",file.exists(),file.getAbsolutePath());
                    if (file.delete()) {
                        log.info("File '" + fileName + "' deleted successfully.");
                        infoMap.remove(fileName);
                    } else {
                        log.error("Failed to delete existing file: " + fileName);
                    }
                }
                //创建文件及文件夹
                file.getParentFile().mkdirs(); // 创建父目录结构
                file.mkdir();
                // if (!infoMap.containsKey(fileName)) {

                ReplicaData data = new ReplicaData();
                data.dsNode = entry.getKey();
                data.path = sumPath;
                data.id = entry.getKey();
                replicaData.add(data);

                statInfo1.setPath(absolutePath);
                statInfo1.setSize(file.length());
                statInfo1.setMtime(getTime());
                statInfo1.setType(FileType.get(3));


                log.info("File '" + fileName + "' created successfully in MetaServer.");
                printInfoMap();
                //} else {
                //   log.info("File '" + fileName + "' aleady created  in MetaServer.");
                //   printInfoMap();
                // }

            }
            statInfo1.setReplicaData(replicaData);
            infoMap.put(fileName, statInfo1);
            //
            log.info("absoluteNodeMap:{}",absoluteNodeMap);
//        File file = new File(path);
//        String absolutePath = file.getAbsolutePath();
//        log.info("创建文件夹的绝对路径为：{}",absolutePath);
//        String fileName = file.getName();
//        if (file.exists() && file.isFile()) {
//            log.info("File '" + fileName + "' already exists in MetaServer.");
//            if (file.delete()) {
//                log.info("File '" + fileName + "' deleted successfully.");
//            } else {
//                log.error("Failed to delete existing file: " + fileName);
//            }
//        }
//        file.mkdirs();
//        if (!infoMap.containsKey(fileName)) {
//            infoMap.put(fileName, statInfo);
//            statInfo.setPath(absolutePath);
//            statInfo.setSize(102400);
//            statInfo.setMtime(getTime());
//            statInfo.setType(FileType.get(3));
//            log.info("File '" + fileName + "' created successfully in MetaServer.");
//            printInfoMap();
//        } else {
//            log.info("File '" + fileName + "' created successfully in MetaServer.");
//            printInfoMap();
//        }

        return absoluteNodeMap;
    }

//    public Object mkdir(String path) throws IOException {
//
//        File file = new File(path);
//        String absolutePath = file.getAbsolutePath();
//        log.info("创建文件夹的绝对路径为：{}",absolutePath);
//        String fileName = file.getName();
//        if (file.exists() && file.isFile()) {
//            log.info("File '" + fileName + "' already exists in MetaServer.");
//            if (file.delete()) {
//                log.info("File '" + fileName + "' deleted successfully.");
//            } else {
//                log.error("Failed to delete existing file: " + fileName);
//            }
//        }
//        file.mkdirs();
//        if (!infoMap.containsKey(fileName)) {
//            infoMap.put(fileName, statInfo);
//            statInfo.setPath(absolutePath);
//            statInfo.setSize(102400);
//            statInfo.setMtime(getTime());
//            statInfo.setType(FileType.get(3));
//            log.info("File '" + fileName + "' created successfully in MetaServer.");
//            printInfoMap();
//        } else {
//            log.info("File '" + fileName + "' created successfully in MetaServer.");
//            printInfoMap();
//        }
//
//        return null;
//    }

    public Object delete(String path) throws IOException {
        File file = new File(path);
        if (file.exists()) {
            if (!file.delete()) {
                System.out.println("无法删除：" + path);
            }
        } else {
            System.out.println("文件或文件夹不存在：" + path);
        }
        return null;
    }

    public Object pickDataServer() {


        // 需要考虑选择ds的策略？负载
        return null;
    }


    public long getTime() {
        LocalDateTime now = LocalDateTime.now();
        Instant instant = now.toInstant(ZoneOffset.UTC);
        long ctime = instant.toEpochMilli();
        return ctime;
    }

    public String getFileName(String path) {
        File file = new File(path);
        String fileName = file.getName();
        return fileName;
    }

    public List<String> printInfoMap() throws JsonProcessingException {
        List<String> keyList = new ArrayList<>();
        for (Map.Entry<String, StatInfo> entry : infoMap.entrySet()) {
            String key = entry.getKey();
            StatInfo value = entry.getValue();
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(value);
            System.out.println("文件名: " + key);
            keyList.add(key);
            System.out.println("文件详情信息: " + json);
        }
        return keyList;
    }


    //从zookeeper获取多副本信息。。
    public Object getDataserverInfo() {
        CuratorFramework client = null;
        List<String> children = null;
        try {
            // 创建Curator Framework客户端
            client = CuratorFrameworkFactory.newClient(connectionString, new ExponentialBackoffRetry(1000, 3));
            client.start();

            String path = "/dataServers/dataServer";
            children = client.getChildren().forPath(path);

            // 获取节点数据
            byte[] data = client.getData().forPath("/dataServers/dataServer/localhost:9000");

            // 解析节点数据
            String metadata = new String(data); // 这里假设数据是字符串，根据实际情况进行解析

            System.out.println("Metadata: " + metadata);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭客户端连接
            CloseableUtils.closeQuietly(client);
        }
        return children;
    }


    //获取daserver每个节点的数据,存储文件的地址
    public Map<String, String> getNodeInfoMap(List<String> nodeList) {

        //1.选取三个node,先随机选取3个
        List<String> randomNodes = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 3; i++) {
            int randomIndex = random.nextInt(nodeList.size());
            randomNodes.add(nodeList.get(randomIndex));
            // 避免重复选择同一个节点
            nodeList.remove(randomIndex);
        }
        log.info("选取的三个节点为：" + randomNodes.toString());

        //2.连接zk，获取存储的地址
        CuratorFramework client = null;
        Map<String, String> map = new LinkedHashMap<>();
        try {
            // 创建Curator Framework客户端
            client = CuratorFrameworkFactory.newClient(connectionString, new ExponentialBackoffRetry(1000, 3));
            client.start();

            String path = "/dataServers/dataServer";
            for (String str : randomNodes) {
                byte[] data = client.getData().forPath("/dataServers/dataServer/" + str);// 获取节点数据
                String metadata = new String(data); // 解析节点数据
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(metadata);
                System.out.println("rootNode:" + rootNode);
                JsonNode paylord = rootNode.get("payload");
                String rootPath = paylord.get("currentPath").asText();
                map.put(str, rootPath);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭客户端连接
            CloseableUtils.closeQuietly(client);
        }
        return map;
    }

}
