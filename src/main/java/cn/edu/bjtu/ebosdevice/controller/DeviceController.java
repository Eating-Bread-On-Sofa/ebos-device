package cn.edu.bjtu.ebosdevice.controller;

import cn.edu.bjtu.ebosdevice.model.PostedDevice;
import cn.edu.bjtu.ebosdevice.service.LogService;
import cn.edu.bjtu.ebosdevice.service.impl.ProtocolsDict;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import cn.edu.bjtu.ebosdevice.entity.Device;
import cn.edu.bjtu.ebosdevice.service.DeviceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

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
    @ApiImplicitParam(name = "ip",value = "指定网关的ip",required = true,paramType = "path",dataTypeClass = String.class)
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
                jo = deviceService.addInfo2JsonObject(jo,device);
            }catch (Exception ignored){}
            deviceService.simplifyAdd2JSONArray(arr, jo);
        }
        logService.info(null,"查看了位于网关"+ip+" 下的设备列表");
        return arr;
    }

    @ApiOperation(value = "查看指定创建时间范围的网关设备",notes = "范围 天数 int days")
    @ApiImplicitParam(name = "days",value = "查询天数范围,int类型",required = true, paramType = "query")
    @CrossOrigin
    @GetMapping("/days")
    public JSONArray getEdgeXDevices(@RequestParam int days){
        JSONArray jsonArray = new JSONArray();
        Date end = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(end);
        for (int i = 0; i < days; i++) {
            calendar.add(Calendar.DATE, -1);
            Date start = calendar.getTime();
            List<Device> devices = deviceService.findByCreatedBetween(start,end);
            JSONArray details = new JSONArray();
            for (Device device : devices) {
                String url = "http://" + device.getGateway() + ":48081/api/v1/device/name/" + device.getDeviceName();
                try {
                    JSONObject jo = restTemplate.getForObject(url, JSONObject.class);
                    jo = deviceService.addInfo2JsonObject(jo, device);
                    deviceService.simplifyAdd2JSONArray(details, jo);
                }catch (HttpClientErrorException.NotFound ignored){}
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("startDate",start);
            jsonObject.put("endDate",end);
            jsonObject.put("details",details);
            jsonObject.put("count",details.size());
            jsonArray.add(jsonObject);
            end = start;
        }
        return jsonArray;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "ip",value = "指定网关的ip",required = true,paramType = "path",dataTypeClass = String.class),
            @ApiImplicitParam(name = "keywords",value = "关键字",required = true,paramType = "path",dataTypeClass = String.class)
    })
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
                    jo = deviceService.addInfo2JsonObject(jo, device);
                } else {
                    continue;
                }
            }catch (Exception ignored){}
            deviceService.simplifyAdd2JSONArray(res, jo);
        }
        logService.info(null,"查看了位于网关"+ip+" 下的名字类似为"+keywords+"的设备列表");
        return res;
    }

    @CrossOrigin
    @GetMapping("/ip/{ip}/id/{id}")
    public JSONObject getThisDevice(@PathVariable String ip, @PathVariable String id){
        String url = "http://"+ip+":48081/api/v1/device/"+id;
        JSONObject jo = restTemplate.getForObject(url,JSONObject.class);
        try {
            Device device = deviceService.findByName(jo.getString("name"));
            jo = deviceService.addInfo2JsonObject(jo,device);
        }catch (Exception ignored){}
        logService.info(null,"查看了位于网关"+ip+" 下id="+id+" 的设备信息");
        return jo;
    }

    @CrossOrigin
    @PostMapping("/ip/{ip}")
    public String addDevice(@PathVariable String ip, @RequestBody PostedDevice postedDevice) {
        String url = "http://" + ip + ":48081/api/v1/device";
        String name = postedDevice.getName();
        if(deviceService.findByName(name) == null) {
            try {
                return getRes(ip, postedDevice, url, name);
            }catch (HttpClientErrorException e){
                System.out.println(e.getMessage());
                if (Objects.equals(e.getMessage(), "400 Bad Request: [Invalid object ID: A device must be associated with a device profile\n" +
                        "]")){
                    try{
                        String profileUrl = "http://localhost:8091/api/profile/gateway/"+ip+"/"+postedDevice.getProfile().getName();
                        String profileRes = restTemplate.postForObject(profileUrl,null,String.class);
                        if(profileRes.equals("模板库中无此模板")){return "添加失败 模板库及网关中均无此设备所使用的模板";}
                        return getRes(ip, postedDevice, url, name);
                    }catch (Exception e1){
                        logService.error(null,"尝试向"+ip+"添加"+name+"设备失败:"+e1.toString());
                        e1.printStackTrace();
                        return "添加失败! "+e1.toString();
                    }
                }else {
                    logService.error(null,"尝试向"+ip+"添加"+name+"设备失败:"+e.toString());
                    System.out.println("添加失败 ");
                    return "添加失败 "+e.toString();
                }
            }
        }else{
            logService.warn(null,"尝试向"+ip+"添加"+name+"设备失败:名称重复");
            return "名称重复";
        }
    }

    private String getRes(@PathVariable String ip, @RequestBody PostedDevice postedDevice, String url, String name) {
        String result = restTemplate.postForObject(url, postedDevice, String.class);
        Device device = new Device();
        device.setDeviceName(name);
        device.setGateway(ip);
        device.setDescription(postedDevice.getDescription());
        deviceService.addDevice(device);
        logService.info(null,"向"+ip+"添加"+name+"设备成功 Edgex id=" + result);
        return "添加成功";
    }

    @CrossOrigin
    @PostMapping("/recover/{ip}")
    public JSONObject plusDevice(@PathVariable String ip, @RequestBody List<PostedDevice> postedDeviceList) {
        logService.info(null, "开始向" + ip + "恢复设备配置");
        String url = "http://" + ip + ":48081/api/v1/device";
        JSONObject result = new JSONObject();
        for (PostedDevice postedDevice : postedDeviceList) {
            String res = "已更新";
            try {
                try {
                    restTemplate.put(url, postedDevice);
                } catch (HttpClientErrorException.NotFound e) {
                    res = addDevice(ip, postedDevice);
                }
                Device device = new Device();
                device.setDeviceName(postedDevice.getName());
                device.setGateway(ip);
                deviceService.plusDevice(device);
                result.put(postedDevice.getName(), res);
            } catch (Exception e) {
                result.put(postedDevice.getName(), e.toString());
                e.printStackTrace();
            }
        }
        logService.info(null, "恢复结果" + result.toJSONString());
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
            logService.info(null,"删除网关"+ip+"中的"+name+"设备");
            return "done";
        } catch (Exception e) {
            logService.error(null,"删除网关"+ip+"中的"+name+"设备失败"+e.toString());
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
    @GetMapping("/service/{ip}")
    public JSONArray getDeviceService(@PathVariable String ip){
        JSONArray result = new JSONArray();
        JSONArray list = restTemplate.getForObject("http://"+ip+":48081/api/v1/deviceservice",JSONArray.class);
        for (int i = 0; i < list.size(); i++) {
            JSONObject service = list.getJSONObject(i);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", service.getString("name"));
            result.add(jsonObject);
        }
        return result;
    }

    @CrossOrigin
    @GetMapping("/details/{ip}/{id}")
    public JSONObject getDetails(@PathVariable String ip, @PathVariable String id){return deviceService.getDeviceDetail(ip, id);}

    @CrossOrigin
    @GetMapping("/online/{ip}")
    public JSONArray getOnline(@PathVariable String ip){return deviceService.getOnlineDevices(ip);}

    @CrossOrigin
    @GetMapping("/ping")
    public String ping(){
        return "pong";
    }
}
