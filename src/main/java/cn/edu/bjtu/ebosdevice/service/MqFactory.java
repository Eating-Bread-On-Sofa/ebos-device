package cn.edu.bjtu.ebosdevice.service;

public interface MqFactory {
    MqProducer createProducer();
    MqConsumer createConsumer(String topic);
}
