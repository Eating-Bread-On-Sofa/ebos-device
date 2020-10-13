package cn.edu.bjtu.ebosdevice.entity;

import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

@Document(collection = "deviceCount")
public class DeviceCount implements Serializable {

    // 用于统计记录某天哪个网关添加过设备
    private String gateway;
    private Date date;
    private int count;

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}
