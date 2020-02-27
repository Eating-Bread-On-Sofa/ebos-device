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
    private static String top = "";
    private static String str = "";
    @Autowired
    private MongoTemplate mongoTemplate;
    @Override
    public void debug(String massage){
        Log log = new Log();
        log.setData(new Date());
        log.setCategory("debug");
        log.setMassage(massage);
        getTop();
        log.setSource(top);
        mongoTemplate.save(log);
    }
    @Override
    public void info(String massage){
        Log log = new Log();
        log.setData(new Date());
        log.setCategory("info");
        log.setMassage(massage);
        getTop();
        log.setSource(top);
        mongoTemplate.save(log);
    }
    @Override
    public void warn(String massage){
        Log log = new Log();
        log.setData(new Date());
        log.setCategory("warn");
        log.setMassage(massage);
        getTop();
        log.setSource(top);
        mongoTemplate.save(log);
    }
    @Override
    public void error(String massage){
        Log log = new Log();
        log.setData(new Date());
        log.setCategory("debug");
        log.setMassage(massage);
        getTop();
        log.setSource(top);
        mongoTemplate.save(log);
    }
    @Override
    public String findAll(){
        String str1 = "";
        str = "";
        List<Log> list = mongoTemplate.findAll(Log.class);
        for(Log log:list){
            str1 = log.getData() + "   [" + log.getCategory() +"]   " + log.getSource() + "    " + log.getMassage() + "\n";
            str += str1;
        }
        str = str.substring(0,str.length()-1);
        return str;
    }
    @Override
    public String findLogByCategory(String category){
        String str2 = "";
        str = "";
        Query query = Query.query(Criteria.where("category").is(category));
        List<Log> list = mongoTemplate.find(query , Log.class);
        for(Log log:list){
            str2 = log.getData() + "   [" + log.getCategory() +"]   " + log.getSource() + " - " + log.getMassage() + "\n";
            str += str2;
        }
        str = str.substring(0,str.length()-1);
        return str;
    }
    @Override
    public void getTop() {
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
                    isEachLogClass = false;
                    caller = s;
                    break;
                }
            }
        }
        if(caller != null) {
            top = caller.toString();
        }else{
            top = "";
        }
    }
}
