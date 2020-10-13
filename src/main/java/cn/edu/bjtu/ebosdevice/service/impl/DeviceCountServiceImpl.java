package cn.edu.bjtu.ebosdevice.service.impl;

import cn.edu.bjtu.ebosdevice.entity.DeviceCount;
import cn.edu.bjtu.ebosdevice.entity.Gateway;
import cn.edu.bjtu.ebosdevice.service.DeviceCountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DeviceCountServiceImpl implements DeviceCountService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void saveGateway(String gateway, Date date, int count) {

        DeviceCount deviceCount = new DeviceCount();
        deviceCount.setGateway(gateway);
        deviceCount.setDate(date);
        deviceCount.setCount(count);
        mongoTemplate.save(deviceCount);

    }

    @Override
    public void saveTotal(Date date, int count) {
        DeviceCount deviceCount = new DeviceCount();
        deviceCount.setGateway("总计");
        deviceCount.setDate(date);
        deviceCount.setCount(count);
        mongoTemplate.save(deviceCount);
    }

    @Override
    public List<DeviceCount> findAll() {
        return mongoTemplate.findAll(DeviceCount.class,"deviceCount");
    }

    @Override
    public List<Gateway> findGateway() {
        return mongoTemplate.findAll(Gateway.class,"Gateway");
    }

    @Override
    public List<DeviceCount> findRecent() {
        Query query = new Query();
        query.with(Sort.by(Sort.Order.desc("date"))).limit(1);
        return mongoTemplate.find(query,DeviceCount.class,"deviceCount");
    }

    @Override
    public void addUpdate(Date date,String gateway) {
        Query query = Query.query(Criteria.where("gateway").is(gateway).and("date").is(date));
        List<DeviceCount> datas = mongoTemplate.find(query,DeviceCount.class,"deviceCount");
        for (DeviceCount data : datas){
            Update update = new Update();
            update.set("count",data.getCount() + 1);
            mongoTemplate.upsert(query,update,DeviceCount.class);
        }
        Query query1 = Query.query(Criteria.where("gateway").is("总计").and("date").is(date));
        List<DeviceCount> totals = mongoTemplate.find(query1,DeviceCount.class,"deviceCount");
        for (DeviceCount total : totals){
            Update update1 = new Update();
            update1.set("count",total.getCount() + 1);
            mongoTemplate.upsert(query1,update1,DeviceCount.class);
        }
    }

    @Override
    public Boolean judgeTotal(Date date) {
        Boolean bool = false;
        Query query = Query.query(Criteria.where("gateway").is("总计").and("date").is(date));
        List<DeviceCount> datas = mongoTemplate.find(query,DeviceCount.class,"deviceCount");
        for(DeviceCount data : datas){
           if (data.getCount() == 1){
               bool = true;
               break;
           }
        }
        return bool;
    }

    @Override
    public void delUpdate(Date date,String gateway) {
        Query query = Query.query(Criteria.where("gateway").is(gateway).and("date").is(date));
        List<DeviceCount> datas = mongoTemplate.find(query,DeviceCount.class,"deviceCount");
        for (DeviceCount data : datas){
            Update update = new Update();
            update.set("count",data.getCount() - 1);
            mongoTemplate.upsert(query,update,DeviceCount.class);
        }
        Query query1 = Query.query(Criteria.where("gateway").is("总计").and("date").is(date));
        List<DeviceCount> totals = mongoTemplate.find(query1,DeviceCount.class,"deviceCount");
        for (DeviceCount total : totals){
            Update update1 = new Update();
            update1.set("count",total.getCount() - 1);
            mongoTemplate.upsert(query1,update1,DeviceCount.class);
        }
    }

    @Override
    public void delByDate(Date date) {
        Query query = Query.query(Criteria.where("date").is(date));
        mongoTemplate.remove(query,DeviceCount.class,"deviceCount");
    }
}
