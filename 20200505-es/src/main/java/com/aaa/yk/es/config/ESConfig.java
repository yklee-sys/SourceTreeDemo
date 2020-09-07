package com.aaa.yk.es.config;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @Param
 * @ClassName ESConfig
 * @Description ES的配置类
 *         @Configuration/@SpringbootApplication 注解作用就是把该类标识为配置类，
 *         这个类就相当于之前spring的xml文件
 *
 *         TransportClient:通过TransportClient对象对es进行增删改查
 * @Author yk
 * @Date 2020/5/5 0005 16:21
 * @Return
 **/
@SpringBootApplication
public class ESConfig {
    /*1.把ESProperties类导入进来*/

    @Autowired
    private ESProperties esProperties;

    @Bean
    public TransportClient getTransportClient(){
        /*1.创建TransportClient对象，不用初始化*/
        TransportClient transportClient = null;
        try{
            /*2.配置es的集群信息
             * client.transport.sniff:
             *                 当es集群中有新的节点加入，项目会自动发现这个节点，不需要在进行手动添加
             * thread_pool.search.size:
             *                 es的线程池
             * */
            Settings settings = Settings.builder()
                    .put("cluster.name", esProperties.getClusterName())
                    .put("node.name", esProperties.getNodeName())
                    .put("client.transport.sniff", true)
                    .put("thread_pool.search.size", esProperties.getPool()).build();
            /*3.对TransportClient进行初始化*/
            transportClient = new PreBuiltTransportClient(settings);
            /*4.配置es的连接信息(需要知道es的ip地址和es的端口号才可进行连接)*/
            TransportAddress transportAddress = new TransportAddress(InetAddress.getByName(esProperties.getIp()),Integer.parseInt(esProperties.getPort()));
            /*5.把es的连接信息数放入TransportClient对象中*/
            transportClient.addTransportAddress(transportAddress);
        }catch (UnknownHostException e){
            e.printStackTrace();
        }

        /*6.返回TransportClient对象*/
        return transportClient;
    }

}
