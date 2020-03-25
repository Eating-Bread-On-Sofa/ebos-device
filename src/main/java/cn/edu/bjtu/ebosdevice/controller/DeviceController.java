package cn.edu.bjtu.ebosdevice.controller;

import cn.edu.bjtu.ebosdevice.service.LogService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import cn.edu.bjtu.ebosdevice.entity.Device;
import cn.edu.bjtu.ebosdevice.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@RequestMapping("/api/device")
@RestController
public class DeviceController {
    @Autowired
    DeviceService deviceService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    LogService logService;

    @CrossOrigin
    @GetMapping("/{ip}")
    public JSONArray getEdgeXDevices(@PathVariable String ip){
        String url = "http://"+ip+":48081/api/v1/device";
        JSONArray devices = new JSONArray(restTemplate.getForObject(url,JSONArray.class));
        JSONArray arr = new JSONArray();
        for(int i=0; i<devices.size();i++){
            JSONObject jo = devices.getJSONObject(i);
            try {
                Device device = deviceService.findByName(jo.getString("name"));
                jo = deviceService.addInfo2Json(jo,device);
            }catch (Exception ignored){}
            String profileName = jo.getJSONObject("profile").getString("name");
            jo.remove("profile");
            jo.put("profile",profileName);
            String serviceName = jo.getJSONObject("service").getString("name");
            jo.remove("service");
            jo.put("service",serviceName);
            arr.add(jo);
        }
        return arr;
    }

    @CrossOrigin
    @GetMapping("/{ip}/{id}")
    public JSONObject getThisDevice(@PathVariable String ip, @PathVariable String id){
        String url = "http://"+ip+":48081/api/v1/device/"+id;
        JSONObject jo = restTemplate.getForObject(url,JSONObject.class);
        try {
            Device device = deviceService.findByName(jo.getString("name"));
            jo = deviceService.addInfo2Json(jo,device);
        }catch (Exception ignored){}
        return jo;
    }

    @CrossOrigin
    @PostMapping("/{ip}")
    public String addDevice(@PathVariable String ip, @RequestBody JSONObject jsonObject) {
        String url = "http://" + ip + ":48081/api/v1/device";
        System.out.println(url);
        if(deviceService.findByName(jsonObject.getString("name")) == null) {
            try {
                String result = restTemplate.postForObject(url, jsonObject, String.class);
                System.out.println("添加设备成功 Edgex id=" + result);
                Device device = new Device();
                device.setDeviceName(jsonObject.getString("name"));
                device.setGateway(ip);
                deviceService.addDevice(device);
                return "添加成功";
            } catch (HttpClientErrorException e) {
                return "失败"+e.toString();
            }
        }else{
            return "名称重复";
        }
    }

    @CrossOrigin
    @PostMapping("/recover/{ip}")
    public JSONObject plusDevice(@PathVariable String ip, @RequestBody JSONArray jsonArray) {
        String url = "http://" + ip + ":48081/api/v1/device";
        JSONObject result = new JSONObject();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            try {
                restTemplate.put(url, jsonObject);
                Device device = new Device();
                device.setDeviceName(jsonObject.getString("name"));
                device.setGateway(ip);
                String r = deviceService.plusDevice(device);
                result.put(jsonObject.getString("name"), r);
            } catch (Exception e) {
                result.put(jsonObject.getString("name"), e.toString());
            }
        }
        return result;
    }

    @CrossOrigin
    @DeleteMapping("/{ip}")
    public void deleteDevice(@PathVariable String ip, @RequestBody String name){
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
