package cn.edu.bjtu.ebosdevice.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class KafkaConfig {
    public static String servers;

    @Value("${spring.kafka.consumer.bootstrap-servers}")
    public void setServers(String s){
        servers=s;
    }

    public static Properties getProperties(){
        Properties properties = new Properties();
        properties.put("bootstrap.servers", servers);//xxx是服务器集群的ip
        properties.put("group.id", "mqrouter");
        properties.put("enable.auto.commit", "true");
        properties.put("auto.commit.interval.ms", "1000");
        properties.put("auto.offset.reset", "latest");
        properties.put("session.timeout.ms", "30000");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        return properties;
    }
}
