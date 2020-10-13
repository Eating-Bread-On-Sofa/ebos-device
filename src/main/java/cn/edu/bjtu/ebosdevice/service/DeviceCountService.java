package cn.edu.bjtu.ebosdevice.service;

import cn.edu.bjtu.ebosdevice.entity.DeviceCount;
import cn.edu.bjtu.ebosdevice.entity.Gateway;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public interface DeviceCountService {

    void saveGateway(String gateway, Date date, int count);
    void saveTotal( Date date, int count);
    List<DeviceCount> findAll();
    List<Gateway> findGateway();
    List<DeviceCount> findRecent();
    void addUpdate(Date date,String gateway);
    Boolean judgeTotal(Date date);
    void delUpdate(Date date,String gateway);
    void delByDate(Date date);
}
