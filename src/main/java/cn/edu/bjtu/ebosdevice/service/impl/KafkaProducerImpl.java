package cn.edu.bjtu.ebosdevice.service.impl;

import cn.edu.bjtu.ebosdevice.service.MqProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerImpl implements MqProducer {
    private KafkaTemplate kafkaTemplate;

    @Autowired
    public KafkaProducerImpl(KafkaTemplate kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(String topic, String message){
        System.out.println("kafka发布"+topic+"消息 " + message);
        kafkaTemplate.send(topic,message);
    }

}
