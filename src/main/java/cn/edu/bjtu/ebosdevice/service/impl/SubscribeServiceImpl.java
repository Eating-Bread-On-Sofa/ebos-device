package cn.edu.bjtu.ebosdevice.service.impl;

import cn.edu.bjtu.ebosdevice.entity.Subscribe;
import cn.edu.bjtu.ebosdevice.service.SubscribeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubscribeServiceImpl implements SubscribeService {

    @Autowired
    private MongoTemplate mongoTemplate;

    private static String serviceName = "设备管理";

    @Override
    public void save( String subTopic) {
        Subscribe subscribe = new Subscribe();
        subscribe.setServiceName(serviceName);
        subscribe.setSubTopic(subTopic);
        subscribe.setCreated(new Date());
        mongoTemplate.save(subscribe);
    }

    @Override
    public void delete(String subTopic) {
        Query query = Query.query(Criteria.where("subTopic").is(subTopic));
        mongoTemplate.remove(query,Subscribe.class,"subscribe");
    }

    @Override
    public List<Subscribe> findAll() {
        return mongoTemplate.findAll(Subscribe.class,"subscribe");
    }

    @Override
    public List<Subscribe> findByServiceName() {
        Query query = Query.query(Criteria.where("serviceName").is(serviceName));
        return mongoTemplate.find(query,Subscribe.class,"subscribe");
    }
}
