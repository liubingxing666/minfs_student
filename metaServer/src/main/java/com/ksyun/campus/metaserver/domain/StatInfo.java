package com.ksyun.campus.metaserver.domain;

import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
public class StatInfo
{
    public String path;
    public long size;
    public long mtime;
    public FileType type;
    private List<ReplicaData> replicaData;
    public StatInfo() {}

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getMtime() {
        return mtime;
    }

    public void setMtime(long mtime) {
        this.mtime = mtime;
    }

    public FileType getType() {
        return type;
    }

    public void setType(FileType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "StatInfo{" +
                "path='" + path + '\'' +
                ", size=" + size +
                ", mtime=" + mtime +
                ", type=" + type +
                ", replicaData=" + replicaData +
                '}';
    }

}
