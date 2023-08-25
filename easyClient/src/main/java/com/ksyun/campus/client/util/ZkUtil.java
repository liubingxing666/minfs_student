package com.ksyun.campus.client.util;

import javax.annotation.PostConstruct;

public class ZkUtil {
    public  Integer id=0;
    @PostConstruct
    public void postCons() throws Exception {
        // todo 初始化，与zk建立连接，注册监听路径，当配置有变化随时更新
        System.out.println("zk init success!");
        System.out.println(id+1);

    }
}
