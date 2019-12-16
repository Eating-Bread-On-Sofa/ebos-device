package cn.edu.bjtu.ebosdevice.service;

import com.alibaba.fastjson.JSONObject;

public interface MqService {
    void send(String destinationName, JSONObject message);
    void publish(String destinationName, JSONObject message);
}
