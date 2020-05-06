package cn.edu.bjtu.ebosdevice.service.impl;

import cn.edu.bjtu.ebosdevice.dao.DeviceRepository;
import cn.edu.bjtu.ebosdevice.entity.Device;
import cn.edu.bjtu.ebosdevice.service.DeviceService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;


@Service
public class DeviceServiceImpl implements DeviceService {

    //private final DateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    DeviceRepository deviceRepository;
    @Autowired
    RestTemplate restTemplate;


    @Override
    public Page<Device> findAllDevice(Pageable pageable) {
        Page<Device> devices = deviceRepository.findAll(pageable);
        return devices;
    }

    @Override
    public List<Device> findAllDevice() {
        return deviceRepository.findAll();
    }

    @Override
    public String addDevice(Device device) {
        Device findDevice = deviceRepository.findByDeviceName(device.getDeviceName());
        if (findDevice == null) {
            device.setDeviceCreateTime(new Date());
            deviceRepository.save(device);
            return "添加成功";
        } else {
            return "名称重复";
        }
    }

    @Override
    public String plusDevice(Device device){
        try {
            Device findDevice = deviceRepository.findByDeviceName(device.getDeviceName());
            if (findDevice != null) {
                deviceRepository.deleteByDeviceId(findDevice.getDeviceId());
            }
            Device device1 = deviceRepository.save(device);
            ObjectId objectId = new ObjectId(device1.getDeviceId());
            device1.setDeviceCreateTime(objectId.getDate());
            deviceRepository.save(device1);
            return "成功";
        }catch (Exception e){return e.toString();}
    }

    @Override
    public String deleteDevice(String deviceId) {
        Device device = deviceRepository.findDeviceByDeviceId(deviceId);
        if (device == null) {
            return "不存在该产品";
        } else {
            deviceRepository.deleteById(deviceId);
            return "删除成功";
        }
    }

    @Override
    public Device findDeviceByDeviceId(String deviceId) {
        return deviceRepository.findDeviceByDeviceId(deviceId);
    }

    @Override
    public Page<Device> findDeviceByDeviceType(String deviceType, Pageable pageable) {
        Page<Device> devicesByType = deviceRepository.findDeviceByDeviceType(deviceType,pageable);
        return devicesByType;
    }

    @Override
    public void changeDeviceStatus(Device dev, int status){
        System.out.println("DeviceService currentstatus:"+status);
        dev.setDeviceStatus(status);
        deviceRepository.save(dev);
    }

    @Override
    public Device findByName(String name){
        return deviceRepository.findDeviceByDeviceName(name);
    }

    @Override
    public boolean deleteByEdgexId(String id){
        Device device = deviceRepository.findByEdgexId(id);
        if (device == null) {
            return false;
        } else {
            deviceRepository.deleteByEdgexId(id);
            return true;
        }
    }

    @Override
    public boolean deleteByName(String name){
        Device device = deviceRepository.findByDeviceName(name);
        if(device == null ){
            return false;
        }else{
            deviceRepository.deleteByDeviceName(name);
            return true;
        }
    }

    @Override
    public JSONObject addInfo2JsonObject(JSONObject jo, Device device){
        Date date = device.getDeviceCreateTime();
        String gateway = device.getGateway();
        String description = device.getDescription();
        jo.put("createdTime", date);
        jo.put("gateway",gateway);
        jo.put("description",description);
        return jo;
    }

    @Override
    public List<Device> findByCreatedAfter(Date date){
        return deviceRepository.findByDeviceCreateTimeAfter(date);
    }

    @Override
    public List<Device> findByCreatedBetween(Date start, Date end){
        return deviceRepository.findByDeviceCreateTimeBetween(start, end);
    }

    @Override
    public void simplifyAdd2JSONArray(JSONArray output, JSONObject input){
        String profileName = input.getJSONObject("profile").getString("name");
        input.remove("profile");
        input.put("profile",profileName);
        String serviceName = input.getJSONObject("service").getString("name");
        input.remove("service");
        input.put("service",serviceName);
        output.add(input);
    }

    @Override
    public JSONArray getOnlineDevices(String ip){
        String allUrl = "http://"+ip+":48082/api/v1/device";
        JSONArray all = new JSONArray(restTemplate.getForObject(allUrl,JSONArray.class));
        JSONArray idArr = new JSONArray();
        for(int i = 0; i<all.size();i++) {
            JSONObject deviceObj = all.getJSONObject(i);
            String deviceId = deviceObj.getString("id");
            String deviceName = deviceObj.getString("name");
            JSONArray commandsArr = deviceObj.getJSONArray("commands");
            String commandsId= commandsArr.getJSONObject(0).getString("id");
            try {
                String url = "http://"+ip+":48082/api/v1/device/"+deviceId+"/command/"+commandsId;
                JSONObject get = new JSONObject(restTemplate.getForObject(url, JSONObject.class));
                JSONObject id = new JSONObject();
                id.put("id",deviceId);
                id.put("name",deviceName);
                idArr.add(id);
            } catch (Exception e) {
            }
        }
        return idArr;
    }

    @Override
    public JSONObject getDeviceDetail(String ip, String id){
        if(id.equals("0")){return new JSONObject();}else {
            String deviceId = id;
            String deviceUrl = "http://"+ip+":48082/api/v1/device/" + deviceId;
            JSONObject deviceObj = new JSONObject(restTemplate.getForObject(deviceUrl, JSONObject.class));
            JSONArray commandsArr = deviceObj.getJSONArray("commands");
            JSONObject result = new JSONObject();
            for (int i = 0; i < commandsArr.size(); i++) {
                String commandsId = commandsArr.getJSONObject(i).getString("id");
                String url = "http://"+ip+":48082/api/v1/device/" + deviceId + "/command/" + commandsId;
                try {
                    JSONObject commandObj = new JSONObject(restTemplate.getForObject(url, JSONObject.class));
                    JSONObject reading = commandObj.getJSONArray("readings").getJSONObject(0);
                    result.put(reading.getString("name"), reading.getString("value"));
                } catch (Exception e) {
                }
            }
            return result;
        }
    }
}
