package com.ksyun.campus.dataserver.services;

import com.ksyun.campus.dataserver.MyServiceInfo;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.UriSpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Component
public class RegistService implements ApplicationRunner {
    @Value("${server.port}")
    Integer port;
    @Value("${az.rack}")
    String rack;
    @Value("${az.zone}")
    String zone;
    @Value("${zookeeper.addr}")
    String ipAddressPort;
    @Override
    public void run(ApplicationArguments args) throws Exception {

        registToCenter();


    }

    public void registToCenter() throws Exception {

        // 获取当前工程路径
        String projectPath = System.getProperty("user.dir");
        System.out.println("当前工程路径：" + projectPath);

        // 创建上一级目录的路径
        String parentDirectoryPath = new File(projectPath).getParent();
        System.out.println("上一级目录路径：" + parentDirectoryPath);

        // 创建文件夹名
        String directoryName = "myFolder";

        // 创建上一级目录的文件夹路径
//        String folderPath = parentDirectoryPath + File.separator + "localhost_"+port;
        String folderPath =projectPath+File.separator+ "localhost_"+port;

        // 创建文件夹
        File folder = new File(folderPath);
        String dataServerRootPath=folder.getAbsolutePath();
        if (folder.mkdir()) {
            System.out.println("文件夹创建成功：" + folderPath);
        } else {
            System.out.println("文件夹已存在或创建失败：" + folderPath);
        }

        // todo 将本实例信息注册至zk中心，包含信息 ip、port、capacity、rack、zone
        // 创建 CuratorFramework 客户端
        CuratorFramework curatorClient = CuratorFrameworkFactory.newClient(ipAddressPort, new ExponentialBackoffRetry(1000, 3));
        curatorClient.start();
        MyServiceInfo myServiceInfo=new MyServiceInfo("1024000","10240",rack,zone,dataServerRootPath);
        try {
//            // 创建 ServiceDiscovery 用于注册服务
            ServiceDiscovery<MyServiceInfo> serviceDiscovery = ServiceDiscoveryBuilder.builder(MyServiceInfo.class)
                    .basePath("/dataServers") // 指定存储数据服务器信息的基本路径
                    .client(curatorClient)
                    .build();
          //  curatorClient.create().creatingParentsIfNeeded().forPath("/dataServers/dataserver/"+"127.0.0.1:" +port, "dataserver".getBytes(StandardCharsets.UTF_8));
//
//            String secondPath="/dataServers/dataserver/"+"127.0.0.1:" +port;
//            String postPath = secondPath +"/"+"port";
//            byte[] portdata = port.toString().getBytes(StandardCharsets.UTF_8);
//
//            String rackPath = secondPath + "/"+ "rack";
//            byte[] rackdata = rack.getBytes(StandardCharsets.UTF_8);
//            String zonePath = secondPath + "/"+"zone";
//            byte[] zonedata = zone.getBytes(StandardCharsets.UTF_8);
//            String capacityPath = secondPath + "/" +"capacity";
//            byte[] capacitydata = "102400".getBytes(StandardCharsets.UTF_8);
//
//            if (curatorClient.checkExists().forPath(postPath) == null
//                    && curatorClient.checkExists().forPath(rackPath) == null
//                    && curatorClient.checkExists().forPath(zonePath) == null
//                    && curatorClient.checkExists().forPath(capacityPath) == null
//
//            ) {
//                curatorClient.create().creatingParentsIfNeeded().forPath(postPath, portdata);
//                curatorClient.create().creatingParentsIfNeeded().forPath(rackPath, zonedata);
//                curatorClient.create().creatingParentsIfNeeded().forPath(zonePath, rackdata);
//                curatorClient.create().creatingParentsIfNeeded().forPath(capacityPath, capacitydata);
//
//            }


             //获取本地IP地址
            String ip = InetAddress.getLocalHost().getHostAddress();

            // 创建 ServiceInstance 表示数据服务器的实例信息
            ServiceInstance<MyServiceInfo> dataServerInstance = ServiceInstance.<MyServiceInfo>builder()
                    .name("dataServer") // 服务名称
                    .address(ip) // 服务器IP地址
                    .id("localhost"+":"+port)
                    .payload(myServiceInfo)
                    .port(port) // 服务器端口号
                    .build();
                    //.uriSpec(new UriSpec("{scheme}://{address}:{port}")) // URI 规范


            // 注册服务实例到 ZooKeeper
            serviceDiscovery.registerService(dataServerInstance);
            // 打印注册成功信息
            System.out.println("DataServer registered in ZooKeeper successfully!");




        } finally {
           // curatorClient.close();
        }
    }

    public List<Map<String, Integer>> getDslist() {
        return null;
    }


}
