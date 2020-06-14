package cn.edu.bjtu.ebosdevice.service;

import com.alibaba.fastjson.JSONArray;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public interface LogService {
    void debug(String operation,String message);

    void info(String operation,String message);

    void warn(String operation,String message);

    void error(String operation,String message);

    void write(String category,String operation,String message);

    JSONArray findAll();

    JSONArray find(Date startDate, Date endDate, String source, String category, String operation);

    JSONArray findLogByCategory(String category);

    JSONArray findLogBySource(String source);

    JSONArray findLogByOperation(String operation);

    JSONArray findLogByDate(Date startDate, Date endDate);

    JSONArray findLogBySourceAndDate(Date startDate, Date endDate,String source);

    JSONArray findLogBySourceAndCategory(String source, String category);

    JSONArray findLogBySourceAndOperation(String source, String operation);

}
