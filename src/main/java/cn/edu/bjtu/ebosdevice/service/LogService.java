package cn.edu.bjtu.ebosdevice.service;

import cn.edu.bjtu.ebosdevice.entity.Log;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public interface LogService {
    void debug(String operation, String message);

    void info(String operation, String message);

    void warn(String operation, String message);

    void error(String operation, String message);

    void write(String category, String operation, String message);

    List<Log> findAll();

    List<Log> find(Date startDate, Date endDate, String source, String category, String operation);

    List<Log> findLogByCategory(String category);

    List<Log> findLogBySource(String source);

    List<Log> findLogByOperation(String operation);

    List<Log> findLogByDate(Date startDate, Date endDate);

    List<Log> findLogBySourceAndDate(Date startDate, Date endDate, String source);

    List<Log> findLogBySourceAndCategory(String source, String category);

    List<Log> findLogBySourceAndOperation(String source, String operation);
}
