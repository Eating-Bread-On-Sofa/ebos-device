package cn.edu.bjtu.ebosdevice.service.impl;

import com.alibaba.fastjson.JSONObject;
import cn.edu.bjtu.ebosdevice.service.MqService;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTopic;

import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.config.SimpleJmsListenerContainerFactory;
import org.springframework.stereotype.Service;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.jms.core.JmsMessagingTemplate;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;

@Service
public class MqServiceImpl implements MqService {
    @Resource
    private JmsMessagingTemplate jmsMessagingTemplate;

    @Bean
    JmsListenerContainerFactory<?> topicContainerFactory(ConnectionFactory connectionFactory){
        SimpleJmsListenerContainerFactory factory = new SimpleJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setPubSubDomain(true);
        return factory;
    }

    @Override
    public void send(String destinationName, JSONObject message) {
        System.out.println("发送"+destinationName+"消息： " + message);
        Destination destination = new ActiveMQQueue(destinationName);
        jmsMessagingTemplate.convertAndSend(destination, message);
    }

    @Override
    public void publish(String destinationName, JSONObject message) {
        Destination destination = new ActiveMQTopic(destinationName);
        System.out.println("发布"+destinationName+"消息 " + message);
        jmsMessagingTemplate.convertAndSend(destination, message);
    }

    @JmsListener(destination = "test.topic", containerFactory = "topicContainerFactory")
    public void subscribeTest(JSONObject msg) {
        System.out.println("收到订阅的消息:" + msg);
    }

    @JmsListener(destination = "test.queue")
    public void receiveTest(String msg) {
        System.out.println("收到消息： " + msg);
    }
}
