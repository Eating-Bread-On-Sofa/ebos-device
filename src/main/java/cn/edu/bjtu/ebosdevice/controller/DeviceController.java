package cn.edu.bjtu.ebosdevice.controller;

import cn.edu.bjtu.ebosdevice.service.LogService;
import cn.edu.bjtu.ebosdevice.service.impl.LogServiceImpl;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import cn.edu.bjtu.ebosdevice.entity.Device;

import cn.edu.bjtu.ebosdevice.service.DeviceService;
import cn.edu.bjtu.ebosdevice.service.ProfileService;
import cn.edu.bjtu.ebosdevice.util.LayuiTableResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Date;


@RequestMapping("/api/device")
@RestController
public class DeviceController {
    @Autowired
    DeviceService deviceService;
    @Autowired
    ProfileService profileService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    LogService logService;
    @Value("${server.edgex}")
    private String ip;

    @CrossOrigin
    @GetMapping("/list")
    public LayuiTableResultUtil<JSONArray> getEdgeXDevices(){
        String url = "http://"+ip+":48081/api/v1/device";
        JSONArray devices = new JSONArray(restTemplate.getForObject(url,JSONArray.class));
        JSONArray arr = new JSONArray();
        for(int i=0; i<devices.size();i++){
            JSONObject jo = devices.getJSONObject(i);
            try {
                Device device = deviceService.findByName(jo.getString("name"));
                Date date = device.getDeviceCreateTime();
                jo.put("createdTime", date);
            }catch (Exception ignored){}
            String profileName = jo.getJSONObject("profile").getString("name");
            jo.remove("profile");
            jo.put("profile",profileName);
            String serviceName = jo.getJSONObject("service").getString("name");
            jo.remove("service");
            jo.put("service",serviceName);
            arr.add(jo);
        }
        return new LayuiTableResultUtil<>("",arr,0,devices.size());
    }

    //控制设备激活
    @PutMapping("/changeStatus")
    public int changeStatus(@RequestBody JSONObject deviceID){
        JSONObject obj = new JSONObject(deviceID);//将json字符串转换为json对象

        System.out.println("deviceid:"+deviceID);
        System.out.println("deviceid:"+deviceID.getString("deviceId"));
        Device dev = deviceService.findDeviceByDeviceId(deviceID.getString("deviceId"));
        int currentStatus;
        if(dev.getDeviceStatus() == 1){
            currentStatus = 0;
        }else {
            currentStatus = 1;
        }
        System.out.println("DeviceController currentstatus:"+currentStatus);
        deviceService.changeDeviceStatus(dev, currentStatus);
        return currentStatus;
    }



//    @PostMapping("/addDevice")
//    @ResponseBody
//    public Boolean addDevice(@RequestBody Device device) {
//        if (device != null) {
//            if (deviceService.addDevice(device)) {
//                return true;
//            }
//        }
//        return false;
//    }
    @CrossOrigin
    @GetMapping("/{id}")
    public JSONObject getThisDevice(@PathVariable String id){
        String url = "http://"+ip+":48081/api/v1/device/"+id;
        JSONObject jo = restTemplate.getForObject(url,JSONObject.class);
        try {
            Device device = deviceService.findByName(jo.getString("name"));
            Date date = device.getDeviceCreateTime();
            jo.put("createdTime", date);
        }catch (Exception ignored){}
        return jo;
    }

    @CrossOrigin
    @PostMapping("/json")
    public String addDevice(@RequestBody JSONObject jsonObject) {
        if(deviceService.findByName(jsonObject.getString("name")) == null) {
            try {
                String url = "http://" + ip + ":48081/api/v1/device";
                String result = restTemplate.postForObject(url, jsonObject, String.class);
                System.out.println("添加设备成功 id=" + result);
                Device device = new Device();
                device.setDeviceName(jsonObject.getString("name"));
                device.setEdgexId(result);
                deviceService.addDevice(device);
                return result;
            } catch (HttpClientErrorException e) {
                return "失败";
            }
        }else{
            return "名称重复";
        }

    }

    @CrossOrigin
    @DeleteMapping()
    public void deleteDevice(@RequestBody String name){
        String url = "http://"+ip+":48081/api/v1/device/name/"+name;
        if (deviceService.deleteByName(name)){
        restTemplate.delete(url);
        }
    }

    @CrossOrigin
    @GetMapping("/ping")
    public String ping(){
        return "pong";
    }

    @CrossOrigin
    @RequestMapping ("/logtest")
    public String logtest1(){
        logService.info("device");
        return "成功";
    }
    @CrossOrigin
    @GetMapping("/logtest")
    public String logtest2(){
        return logService.findLogByCategory("info");
    }
}
