package cn.edu.bjtu.ebosdevice.service;

import com.alibaba.fastjson.JSONArray;
import org.springframework.stereotype.Service;

@Service
public interface LogService {
    void debug(String message);

    void info(String message);

    void warn(String message);

    void error(String message);

    String getTop();

    JSONArray findLogByCategory(String category);

    JSONArray findAll();

    JSONArray findLogBySource(String source);

    JSONArray findLogBySourceAndCategory(String source, String category);
}
