package cn.edu.bjtu.ebosdevice.controller;

import cn.edu.bjtu.ebosdevice.service.LogService;
import cn.edu.bjtu.ebosdevice.service.impl.ProtocolsDict;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import cn.edu.bjtu.ebosdevice.entity.Device;
import cn.edu.bjtu.ebosdevice.service.DeviceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

@Api(tags = "设备管理")
@RequestMapping("/api/device")
@RestController
public class DeviceController {
    @Autowired
    DeviceService deviceService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    LogService logService;
    @Autowired
    ProtocolsDict protocolsDict;

    @ApiOperation(value = "查看指定网关设备",notes = "需要网关ip")
    @ApiImplicitParam(name = "ip",value = "指定网关的ip",required = true,dataTypeClass = String.class)
    @CrossOrigin
    @GetMapping("/ip/{ip}")
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
            complete(arr, jo);
        }
        logService.info("查看了位于网关"+ip+" 下的设备列表");
        return arr;
    }

    @CrossOrigin
    @GetMapping("/ip/{ip}/{keywords}")
    public JSONArray getLikeEdgeXDevices(@PathVariable String ip,@PathVariable String keywords){
        String url = "http://"+ip+":48081/api/v1/device";
        JSONArray devices = new JSONArray(restTemplate.getForObject(url,JSONArray.class));
        JSONArray res = new JSONArray();
        for(int i=0; i<devices.size();i++){
            JSONObject jo = devices.getJSONObject(i);
            try {
                String deviceName = jo.getString("name").toLowerCase();
                if (deviceName.contains(keywords.toLowerCase())) {
                    Device device = deviceService.findByName(jo.getString("name"));
                    jo = deviceService.addInfo2Json(jo, device);
                } else {
                    continue;
                }
            }catch (Exception ignored){}
            complete(res, jo);
        }
        logService.info("查看了位于网关"+ip+" 下的名字类似为"+keywords+"的设备列表");
        return res;
    }

    private void complete(JSONArray res, JSONObject jo) {
        String profileName = jo.getJSONObject("profile").getString("name");
        jo.remove("profile");
        jo.put("profile",profileName);
        String serviceName = jo.getJSONObject("service").getString("name");
        jo.remove("service");
        jo.put("service",serviceName);
        res.add(jo);
    }

    @CrossOrigin
    @GetMapping("/ip/{ip}/id/{id}")
    public JSONObject getThisDevice(@PathVariable String ip, @PathVariable String id){
        String url = "http://"+ip+":48081/api/v1/device/"+id;
        JSONObject jo = restTemplate.getForObject(url,JSONObject.class);
        try {
            Device device = deviceService.findByName(jo.getString("name"));
            jo = deviceService.addInfo2Json(jo,device);
        }catch (Exception ignored){}
        logService.info("查看了位于网关"+ip+" 下id="+id+" 的设备信息");
        return jo;
    }

    @CrossOrigin
    @PostMapping("/ip/{ip}")
    public String addDevice(@PathVariable String ip, @RequestBody JSONObject jsonObject) {
        String url = "http://" + ip + ":48081/api/v1/device";
        String name = jsonObject.getString("name");
        if(deviceService.findByName(name) == null) {
            try {
                String result = restTemplate.postForObject(url, jsonObject, String.class);
                Device device = new Device();
                device.setDeviceName(name);
                device.setGateway(ip);
                deviceService.addDevice(device);
                logService.info("向"+ip+"添加"+name+"设备成功 Edgex id=" + result);
                return "添加成功";
            } catch (HttpClientErrorException e) {
                logService.error("尝试向"+ip+"添加"+name+"设备失败:"+e.toString());
                return "添加失败 "+e.toString();
            }
        }else{
            logService.warn("尝试向"+ip+"添加"+name+"设备失败:名称重复");
            return "名称重复";
        }
    }

    @CrossOrigin
    @PostMapping("/recover/{ip}")
    public JSONObject plusDevice(@PathVariable String ip, @RequestBody JSONArray jsonArray) {
        logService.info("开始向"+ip+"恢复设备配置");
        String url = "http://" + ip + ":48081/api/v1/device";
        JSONObject result = new JSONObject();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            try {
                try {
                    restTemplate.put(url, jsonObject);
                } catch (HttpClientErrorException.NotFound e) {
                    restTemplate.postForObject(url, jsonObject, String.class);
                }
                Device device = new Device();
                device.setDeviceName(jsonObject.getString("name"));
                device.setGateway(ip);
                String r = deviceService.plusDevice(device);
                result.put(jsonObject.getString("name"), r);
            } catch (Exception e) {
                result.put(jsonObject.getString("name"), e.toString());
            }
        }
        logService.info("恢复结果"+result.toJSONString());
        return result;
    }

    @CrossOrigin
    @DeleteMapping("/ip/{ip}/name/{name}")
    public String deleteDevice(@PathVariable String ip, @PathVariable String name) {
        String url = "http://" + ip + ":48081/api/v1/device/name/" + name;
        try {
            if (deviceService.deleteByName(name)) {
                restTemplate.delete(url);
            }
            logService.info("删除网关"+ip+"中的"+name+"设备");
            return "done";
        } catch (Exception e) {
            logService.error("删除网关"+ip+"中的"+name+"设备失败"+e.toString());
            return e.toString();
        }
    }

    @CrossOrigin
    @GetMapping("/protocol/{name}")
    public JSONObject getProtocol(@PathVariable String name) {
        Map map = protocolsDict.getProtocol();
        JSONObject result = new JSONObject();
        JSONObject content = new JSONObject();
        Set keys = map.keySet();
        Iterator it = keys.iterator();
        while (it.hasNext()) {
            String key = it.next().toString();
            String protocalName = key.substring(0, key.length() - 2);
            if (protocalName.equals(name)) {
                content.put(map.get(key).toString(), null);
            }
        }
        result.put(name, content);
        return result;
    }

    @CrossOrigin
    @GetMapping("/protocol")
    public Set<String> getProtocolKeys(){
        Map map = protocolsDict.getProtocol();
        Set keys = map.keySet();
        Iterator it = keys.iterator();
        Set<String> set = new HashSet<>();
        while (it.hasNext()) {
            String key = it.next().toString();
            String protocalName = key.substring(0, key.length() - 2);
            set.add(protocalName);
        }
        return set;
    }

    @CrossOrigin
    @GetMapping("/ping")
    public String ping(){
        return "pong";
    }
}
