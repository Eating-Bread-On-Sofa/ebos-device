package cn.edu.bjtu.ebosdevice.service;

public interface LogFind {
    String read(String key, String value);
    String readAll();
}
