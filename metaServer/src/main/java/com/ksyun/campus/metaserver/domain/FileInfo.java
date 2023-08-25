package com.ksyun.campus.metaserver.domain;

import lombok.Data;
import org.checkerframework.checker.units.qual.C;
import org.springframework.stereotype.Component;

@Data
@Component
public class FileInfo {
   public String name;
   public StatInfo statInfo;

}
