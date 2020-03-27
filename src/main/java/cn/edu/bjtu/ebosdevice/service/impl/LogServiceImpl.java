package cn.edu.bjtu.ebosdevice.service.impl;

import cn.edu.bjtu.ebosdevice.entity.Log;
import cn.edu.bjtu.ebosdevice.service.LogService;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class LogServiceImpl implements LogService {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" , timezone = "GMT+8")
    private static String serviceName = "设备管理";
    @Autowired
    private MongoTemplate mongoTemplate;
    @Override
    public void debug(String massage){
        Log log = new Log();
        log.setData(new Date());
        log.setCategory("debug");
        log.setMassage(massage);
        log.setSource(getTop());
        mongoTemplate.save(log);
    }
    @Override
    public void info(String massage){
        Log log = new Log();
        log.setData(new Date());
        log.setCategory("info");
        log.setMassage(massage);
        log.setSource(serviceName);
        mongoTemplate.save(log);
    }
    @Override
    public void warn(String massage){
        Log log = new Log();
        log.setData(new Date());
        log.setCategory("warn");
        log.setMassage(massage);
        log.setSource(getTop());
        mongoTemplate.save(log);
    }
    @Override
    public void error(String massage){
        Log log = new Log();
        log.setData(new Date());
        log.setCategory("debug");
        log.setMassage(massage);
        log.setSource(getTop());
        mongoTemplate.save(log);
    }
    @Override
    public String findAll(){
        //使用StringBuilder提高性能
        StringBuilder stringBuilder = new StringBuilder();
        List<Log> list = mongoTemplate.findAll(Log.class);
        for(Log log:list){
            stringBuilder.append(log.getData()).append("   [").append(log.getCategory()).append("]   ").append(log.getSource()).append(" - ").append(log.getMassage()).append("\n");
        }
        return stringBuilder.substring(0,stringBuilder.length()-1);
    }
    @Override
    public String findLogByCategory(String category){
        StringBuilder stringBuilder = new StringBuilder();
        Query query = Query.query(Criteria.where("category").is(category));
        List<Log> list = mongoTemplate.find(query , Log.class);
        for(Log log:list){
            stringBuilder.append(log.getData()).append("   [").append(log.getCategory()).append("]   ").append(log.getSource()).append(" - ").append(log.getMassage()).append("\n");
        }
        return stringBuilder.substring(0,stringBuilder.length()-1);
    }
    @Override
    public String getTop() {
        // 获取堆栈信息
        StackTraceElement[] callStack = Thread.currentThread().getStackTrace();
        // 最原始被调用的堆栈信息
        StackTraceElement caller = null;
        // 日志类名称
        String logClassName = LogServiceImpl.class.getName();
        // 循环遍历到日志类标识
        boolean isEachLogClass = false;
        // 遍历堆栈信息，获取出最原始被调用的方法信息
        for (StackTraceElement s : callStack) {
            // 遍历到日志类
            if (logClassName.equals(s.getClassName())) {
                isEachLogClass = true;
            }
            // 下一个非日志类的堆栈，就是最原始被调用的方法
            if (isEachLogClass) {
                if(!logClassName.equals(s.getClassName())) {
                    caller = s;
                    break;
                }
            }
        }
        if(caller != null) {
            return caller.toString();
        }else{
            return  "";
        }
    }
}
