package cn.edu.bjtu.ebosdevice.service.impl;

import cn.edu.bjtu.ebosdevice.service.MqConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Arrays;
import java.util.Iterator;


public class KafkaConsumerImpl implements MqConsumer {
    private KafkaConsumer kafkaConsumer;

    public KafkaConsumerImpl(String topic){
        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(KafkaConfig.getProperties());
        kafkaConsumer.subscribe(Arrays.asList(topic));
        this.kafkaConsumer=kafkaConsumer;
    }

    @Override
    public String subscribe(){
        ConsumerRecords<String, String> records = kafkaConsumer.poll(100);
        for (ConsumerRecord<String, String> record : records) {
            System.out.println("-----------------");
            System.out.printf("offset = %d, value = %s", record.offset(), record.value());
            System.out.println();
        }
        Iterator<ConsumerRecord<String,String>> iterator=records.iterator();
        ConsumerRecord record=iterator.next();
        return record.value().toString();
    }
}
