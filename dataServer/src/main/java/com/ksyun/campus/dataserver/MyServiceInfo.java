package com.ksyun.campus.dataserver;

import lombok.Data;
import lombok.NoArgsConstructor;
import sun.security.timestamp.TSRequest;

@Data
@NoArgsConstructor
public class MyServiceInfo {
    private String capacity;
    private String usedCapacity;
    private String rack;
    private String zone;
    private String currentPath;

    public MyServiceInfo(String capacity,String usedCapacity,String rack,String zone,String currentPath){
        this.capacity=capacity;
        this.usedCapacity=usedCapacity;
        this.rack=rack;
        this.zone=zone;
        this.currentPath=currentPath;
    }
}
