package cn.edu.bjtu.ebosdevice.service;

import org.springframework.stereotype.Service;

@Service
public interface LogService {
    void debug(String message);

    void info(String message);

    void warn(String message);

    void error(String message);

    String getTop();

    String findLogByCategory(String category);

    String findAll();
}
