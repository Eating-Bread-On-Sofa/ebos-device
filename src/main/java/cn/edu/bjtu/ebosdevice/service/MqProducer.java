package cn.edu.bjtu.ebosdevice.service;

public interface MqProducer {
    void publish(String topic, String message);
}
