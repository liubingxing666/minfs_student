import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.UriSpec;

import java.net.InetAddress;

public class TestZookeeper {

    private static final String ZK_CONNECT_STRING = "localhost:2181"; // ZooKeeper连接字符串，例如 "localhost:2181"
    private static final String ZK_BASE_PATH = "/dataServers"; // ZooKeeper中用于存储数据服务器信息的基本路径

    private static void saveAdditionalNodeInfo(CuratorFramework curatorClient, ServiceInstance<Void> instance,int capacity, String rack, String zone) throws Exception {
        String nodePath = ZK_BASE_PATH + "/" + instance.getId(); // 每个数据服务器在ZooKeeper中的节点路径

        // 在节点中存储额外信息
        curatorClient.create().creatingParentsIfNeeded().forPath(nodePath + "/capacity", String.valueOf(capacity).getBytes());
        curatorClient.create().creatingParentsIfNeeded().forPath(nodePath + "/rack", rack.getBytes());
        curatorClient.create().creatingParentsIfNeeded().forPath(nodePath + "/zone", zone.getBytes());

        // 打印保存成功信息
        System.out.println("Additional node info saved in ZooKeeper successfully!");
    }

    public static void registerDataServer(String ip, int port, int capacity, String rack, String zone) throws Exception {
        // 创建 CuratorFramework 客户端
        CuratorFramework curatorClient = CuratorFrameworkFactory.newClient(ZK_CONNECT_STRING, new ExponentialBackoffRetry(1000, 3));
        curatorClient.start();

        try {
            // 创建 ServiceDiscovery 用于注册服务
            ServiceDiscovery<Void> serviceDiscovery = ServiceDiscoveryBuilder.builder(Void.class)
                    .basePath(ZK_BASE_PATH)
                    .client(curatorClient)
                    .build();

            // 创建 ServiceInstance 表示数据服务器的实例信息
            ServiceInstance<Void> dataServerInstance = ServiceInstance.<Void>builder()
                    .name("dataServer")
                    .address(ip)
                    .port(port)
                    .payload(null)
                    .uriSpec(new UriSpec("{scheme}://{address}:{port}"))
                    .build();

            // 注册服务实例到 ZooKeeper
            serviceDiscovery.registerService(dataServerInstance);

            // 打印注册成功信息
            System.out.println("DataServer registered in ZooKeeper successfully!");

            // 模拟一些逻辑，例如保存额外的节点信息
            saveAdditionalNodeInfo(curatorClient, dataServerInstance, capacity, rack, zone);
        } finally {
            // 关闭 CuratorFramework 客户端
            curatorClient.close();
        }
    }


   // public static void main(String[] args) throws Exception {

        // 创建 ZooKeeper 客户端
//        CuratorFramework client = CuratorFrameworkFactory.newClient("localhost:2181", new ExponentialBackoffRetry(1000, 3));
//        client.start();
//
//        try {
//            // 创建节点并设置数据
//            String path = "/myNode";
//            String data = "Hello, ZooKeeper!";
//            client.create().forPath(path, data.getBytes());
//
//            System.out.println("Node created in ZooKeeper: " + path);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            // 关闭 ZooKeeper 客户端
//            client.close();
//        }


        public static void main(String[] args) {
            try {
                // 获取本地IP地址
                String ip = InetAddress.getLocalHost().getHostAddress();
                int port = 8000; // 假设端口号为8000
                int capacity = 1000; // 假设容量为1000
                String rack = "rack-1"; // 假设所在机架为rack-1
                String zone = "zone-1"; // 假设所在区域为zone-1

                // 注册数据服务器信息到ZooKeeper
                registerDataServer(ip, port, capacity, rack, zone);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }






}
