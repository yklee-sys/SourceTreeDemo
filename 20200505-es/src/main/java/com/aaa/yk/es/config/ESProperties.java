package com.aaa.yk.es.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Param
 * @ClassName ESProperties
 * @Description
 * @Author yk
 * @Date 2020/5/5 0005 16:13
 * @Return
 **/
@Component
@ConfigurationProperties(prefix = "spring.elasticsearch")
public class ESProperties {
    private String ip;
    private String port;
    private String clusterName;
    private String nodeName;
    private String pool;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getPool() {
        return pool;
    }

    public void setPool(String pool) {
        this.pool = pool;
    }
}
