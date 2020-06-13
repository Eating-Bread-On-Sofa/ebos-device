package cn.edu.bjtu.ebosdevice.service;

import com.alibaba.fastjson.JSONArray;
import org.springframework.stereotype.Service;

@Service
public interface LogService {
    void debug(String message);

    void info(String message);

    void warn(String message);

    void error(String message);

    void create(String message);

    void delete(String message);

    void update(String message);

    void retrieve(String message);

    String getTop();

    JSONArray findAll();

    JSONArray findLogByCategory(String category);

    JSONArray findLogBySource(String source);

    JSONArray findLogBySourceAndCategory(String source, String category);

}
