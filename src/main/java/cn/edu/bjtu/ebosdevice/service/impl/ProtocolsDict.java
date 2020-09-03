package cn.edu.bjtu.ebosdevice.service.impl;

import cn.edu.bjtu.ebosdevice.entity.Protocol;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProtocolsDict {
    @Autowired
    MongoTemplate mongoTemplate;

    public JSONObject findOption(String name) {
        JSONObject protocol = new JSONObject();
        List<Protocol> prostrings = mongoTemplate.findAll(Protocol.class,"protocol");
        for( Protocol prostring : prostrings) {
            if(prostring.getName().equals(name)){
                if( prostring.getOption1() != null ){
                    protocol.put(prostring.getOption1(),null);
                }
                if( prostring.getOption2() != null ){
                    protocol.put(prostring.getOption2(),null);
                }
                if( prostring.getOption3() != null ){
                    protocol.put(prostring.getOption3(),null);
                }
                if( prostring.getOption4() != null ){
                    protocol.put(prostring.getOption4(),null);
                }
                if( prostring.getOption5() != null ){
                    protocol.put(prostring.getOption5(),null);
                }
                if( prostring.getOption6() != null ){
                    protocol.put(prostring.getOption6(),null);
                }
                if( prostring.getOption7() != null ){
                    protocol.put(prostring.getOption7(),null);
                }
                if( prostring.getOption8() != null ){
                    protocol.put(prostring.getOption8(),null);
                }
                if( prostring.getOption9() != null ){
                    protocol.put(prostring.getOption9(),null);
                }
                if( prostring.getOption10() != null ){
                    protocol.put(prostring.getOption10(),null);
                }
            }
        }
        return protocol;
    }

    public Set<String> getKeys(){
        Set<String> protocolName = new HashSet<>();
        List<Protocol> prostrings = mongoTemplate.findAll(Protocol.class,"protocol");
        for( Protocol prostring : prostrings) {
            protocolName.add(prostring.getName());
        }
        return protocolName;
    }
}
