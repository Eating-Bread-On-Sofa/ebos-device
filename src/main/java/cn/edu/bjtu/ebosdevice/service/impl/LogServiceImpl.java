package cn.edu.bjtu.ebosdevice.service.impl;

import cn.edu.bjtu.ebosdevice.entity.Log;
import cn.edu.bjtu.ebosdevice.service.LogService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class LogServiceImpl implements LogService {

    private static String serviceName = "设备管理";

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void debug(String operation,String message) {
        Log log = new Log();
        log.setDate(new Date());
        log.setSource(serviceName);
        log.setCategory("debug");
        log.setOperation(operation);
        log.setMessage(message);
        mongoTemplate.save(log);
    }

    @Override
    public void info(String operation,String message) {
        Log log = new Log();
        log.setDate(new Date());
        log.setSource(serviceName);
        log.setCategory("info");
        log.setOperation(operation);
        log.setMessage(message);
        mongoTemplate.save(log);
    }

    @Override
    public void warn(String operation,String message) {
        Log log = new Log();
        log.setDate(new Date());
        log.setSource(serviceName);
        log.setCategory("warn");
        log.setOperation(operation);
        log.setMessage(message);
        mongoTemplate.save(log);
    }

    @Override
    public void error(String operation,String message) {
        Log log = new Log();
        log.setDate(new Date());
        log.setSource(serviceName);
        log.setCategory("error");
        log.setOperation(operation);
        log.setMessage(message);
        mongoTemplate.save(log);
    }

    @Override
    public void write(String category,String operation,String message) {
        Log log = new Log();
        log.setDate(new Date());
        log.setSource(serviceName);
        log.setCategory(category);
        log.setOperation(operation);
        log.setMessage(message);
        mongoTemplate.save(log);
    }

    @Override
    public JSONArray findAll() {
        List<Log> list = mongoTemplate.findAll(Log.class,"log");
        return list2json(list);
    }

    @Override
    public JSONArray find(Date startDate, Date endDate, String source, String category, String operation) {
        if (source.equals("全部")) {
            if (category.equals("all")) {
                if (operation.equals("all")) {
                    Query query = Query.query(Criteria.where("date").gte(startDate).lte(endDate));
                    List<Log> list = mongoTemplate.find(query, Log.class, "log");
                    return list2json(list);
                } else if (operation.equals("null")) {
                    Query query = Query.query(Criteria.where("date").gte(startDate).lte(endDate).and("operation").is(null));
                    List<Log> list = mongoTemplate.find(query, Log.class, "log");
                    return list2json(list);
                } else {
                    Query query1 = Query.query(Criteria.where("date").gte(startDate).lte(endDate).and("operation").is(operation));
                    List<Log> list1 = mongoTemplate.find(query1, Log.class, "log");
                    Query query2 = Query.query(Criteria.where("date").gte(startDate).lte(endDate).and("operation").is(null));
                    List<Log> list = mongoTemplate.find(query2, Log.class, "log");
                    list.addAll(list1);
                    return list2json(list);
                }
            } else if (category.equals("null")) {
                if (operation.equals("all")) {
                    Query query = Query.query(Criteria.where("date").gte(startDate).lte(endDate).and("category").is(null));
                    List<Log> list = mongoTemplate.find(query, Log.class, "log");
                    return list2json(list);
                } else if (operation.equals("null")) {
                    Query query = Query.query(Criteria.where("date").gte(startDate).lte(endDate).and("category").is(null).and("operation").is(null));
                    List<Log> list = mongoTemplate.find(query, Log.class, "log");
                    return list2json(list);
                } else {
                    Query query = Query.query(Criteria.where("date").gte(startDate).lte(endDate).and("operation").is(operation));
                    List<Log> list = mongoTemplate.find(query, Log.class, "log");
                    return list2json(list);
                }
            }else{
                if (operation.equals("all")) {
                    Query query1 = Query.query(Criteria.where("date").gte(startDate).lte(endDate).and("category").is(category));
                    List<Log> list1 = mongoTemplate.find(query1, Log.class, "log");
                    Query query2 = Query.query(Criteria.where("date").gte(startDate).lte(endDate).and("category").is(null));
                    List<Log> list = mongoTemplate.find(query2, Log.class, "log");
                    list.addAll(list1);
                    return list2json(list);
                } else if (operation.equals("null")) {
                    Query query = Query.query(Criteria.where("date").gte(startDate).lte(endDate).and("category").is(category));
                    List<Log> list = mongoTemplate.find(query, Log.class, "log");
                    return list2json(list);
                } else {
                    Query query1 = Query.query(Criteria.where("date").gte(startDate).lte(endDate).and("operation").is(operation));
                    List<Log> list1 = mongoTemplate.find(query1, Log.class, "log");
                    Query query2 = Query.query(Criteria.where("date").gte(startDate).lte(endDate).and("category").is(category));
                    List<Log> list = mongoTemplate.find(query2, Log.class, "log");
                    list.addAll(list1);
                    return list2json(list);
                }
            }
        } else {
            if (category.equals("all")) {
                if (operation.equals("all")) {
                    Query query = Query.query(Criteria.where("date").gte(startDate).lte(endDate).and("source").is(source));
                    List<Log> list = mongoTemplate.find(query, Log.class, "log");
                    return list2json(list);
                } else if (operation.equals("null")) {
                    Query query = Query.query(Criteria.where("date").gte(startDate).lte(endDate).and("source").is(source).and("operation").is(null));
                    List<Log> list = mongoTemplate.find(query, Log.class, "log");
                    return list2json(list);
                } else {
                    Query query1 = Query.query(Criteria.where("date").gte(startDate).lte(endDate).and("source").is(source).and("operation").is(operation));
                    List<Log> list1 = mongoTemplate.find(query1, Log.class, "log");
                    Query query2 = Query.query(Criteria.where("date").gte(startDate).lte(endDate).and("source").is(source).and("operation").is(null));
                    List<Log> list = mongoTemplate.find(query2, Log.class, "log");
                    list.addAll(list1);
                    return list2json(list);
                }
            } else if (category.equals("null")) {
                if (operation.equals("all")) {
                    Query query = Query.query(Criteria.where("date").gte(startDate).lte(endDate).and("source").is(source).and("category").is(null));
                    List<Log> list = mongoTemplate.find(query, Log.class, "log");
                    return list2json(list);
                } else if (operation.equals("null")) {
                    Query query = Query.query(Criteria.where("date").gte(startDate).lte(endDate).and("source").is(source).and("category").is(null).and("operation").is(null));
                    List<Log> list = mongoTemplate.find(query, Log.class, "log");
                    return list2json(list);
                } else {
                    Query query = Query.query(Criteria.where("date").gte(startDate).lte(endDate).and("source").is(source).and("operation").is(operation));
                    List<Log> list = mongoTemplate.find(query, Log.class, "log");
                    return list2json(list);
                }
            }else{
                if (operation.equals("all")) {
                    Query query1 = Query.query(Criteria.where("date").gte(startDate).lte(endDate).and("source").is(source).and("category").is(category));
                    List<Log> list1 = mongoTemplate.find(query1, Log.class, "log");
                    Query query2 = Query.query(Criteria.where("date").gte(startDate).lte(endDate).and("source").is(source).and("category").is(null));
                    List<Log> list = mongoTemplate.find(query2, Log.class, "log");
                    list.addAll(list1);
                    return list2json(list);
                } else if (operation.equals("null")) {
                    Query query = Query.query(Criteria.where("date").gte(startDate).lte(endDate).and("source").is(source).and("category").is(category));
                    List<Log> list = mongoTemplate.find(query, Log.class, "log");
                    return list2json(list);
                } else {
                    Query query1 = Query.query(Criteria.where("date").gte(startDate).lte(endDate).and("source").is(source).and("operation").is(operation));
                    List<Log> list1 = mongoTemplate.find(query1, Log.class, "log");
                    Query query2 = Query.query(Criteria.where("date").gte(startDate).lte(endDate).and("source").is(source).and("category").is(category));
                    List<Log> list = mongoTemplate.find(query2, Log.class, "log");
                    list.addAll(list1);
                    return list2json(list);
                }
            }
        }
    }

    @Override
    public JSONArray findLogByCategory(String category){
        Query query = Query.query(Criteria.where("category").is(category));
        List<Log> list = mongoTemplate.find(query , Log.class);
        return list2json(list);
    }

    @Override
    public JSONArray findLogBySource(String source){
        Query query = Query.query(Criteria.where("source").is(source));
        List<Log> list = mongoTemplate.find(query,Log.class,"log");
        return list2json(list);
    }

    @Override
    public JSONArray findLogByOperation(String operation) {
        Query query = Query.query(Criteria.where("operation").is(operation));
        List<Log> list = mongoTemplate.find(query , Log.class,"log");
        return list2json(list);
    }

    @Override
    public JSONArray findLogByDate(Date startDate, Date endDate) {
        Query query = Query.query(Criteria.where("date").gte(startDate).lte(endDate));
        List<Log> list = mongoTemplate.find(query , Log.class,"log");
        return list2json(list);
    }

    @Override
    public JSONArray findLogBySourceAndDate(Date startDate, Date endDate, String source) {
        Query query = Query.query(Criteria.where("date").gte(startDate).lte(endDate).and("source").is(source));
        List<Log> list = mongoTemplate.find(query , Log.class,"log");
        return list2json(list);
    }

    @Override
    public JSONArray findLogBySourceAndCategory(String source, String category){
        Query query = Query.query(Criteria.where("source").is(source).and("category").is(category));
        List<Log> list = mongoTemplate.find(query , Log.class,"log");
        return list2json(list);
    }

    @Override
    public JSONArray findLogBySourceAndOperation(String source, String operation) {
        Query query = Query.query(Criteria.where("source").is(source).and("operation").is(operation));
        List<Log> list = mongoTemplate.find(query , Log.class,"log");
        return list2json(list);
    }

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

    private JSONArray list2json(List<Log> list){
        JSONArray jsonArray = new JSONArray();
        for(Log log:list){
            JSONObject jsonObject = (JSONObject) JSONObject.toJSON(log);
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }
}
