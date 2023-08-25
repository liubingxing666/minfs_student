package com.ksyun.campus.metaserver.services;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
public class RegistService implements ApplicationRunner {
    @Value("${zookeeper.addr}")
    String ipAddressPort;
    @Value("${server.port}")
    Integer port;
    public void registToCenter() throws Exception {
        // todo 将本实例信息注册至zk中心，包含信息 ip、port
        // 创建 CuratorFramework 客户端
        CuratorFramework curatorClient = CuratorFrameworkFactory.newClient(ipAddressPort, new ExponentialBackoffRetry(1000, 3));
        curatorClient.start();
        try {
            // 创建 ServiceDiscovery 用于注册服务
            ServiceDiscovery<Void> serviceDiscovery = ServiceDiscoveryBuilder.builder(Void.class)
                    .basePath("/metaServers") // 指定存储数据服务器信息的基本路径
                    .client(curatorClient)
                    .build();
            String ip = "127.0.0.1";
            // 创建 ServiceInstance 表示数据服务器的实例信息
            ServiceInstance<Void> metaServerInstance = ServiceInstance.<Void>builder().name("metaServer").address(ip).id("localhost"+":"+port).port(port).build();
            // 注册服务实例到 ZooKeeper
            serviceDiscovery.registerService(metaServerInstance);

            // 打印注册成功信息
            System.out.println("metaServer registered in ZooKeeper successfully!");
        } finally {
//             curatorClient.close();
        }


    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        registToCenter();
    }
}
