package cn.edu.bjtu.ebosdevice.service.impl;

import cn.edu.bjtu.ebosdevice.service.MqProducer;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.jms.Destination;

@Service("activemq")
public class ActiveMqProducerImpl implements MqProducer {
    private JmsMessagingTemplate jmsMessagingTemplate;

    @Autowired
    public ActiveMqProducerImpl(JmsMessagingTemplate jmsMessagingTemplate){
        this.jmsMessagingTemplate = jmsMessagingTemplate;
    }

    @Override
    public void publish(String topic, String message) {
        Destination destination = new ActiveMQTopic(topic);
        System.out.println("activemq发布"+topic+"消息 " + message);
        jmsMessagingTemplate.convertAndSend(destination, message);
    }
}
