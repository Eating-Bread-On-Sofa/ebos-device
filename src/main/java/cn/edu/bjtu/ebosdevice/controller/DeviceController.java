package cn.edu.bjtu.ebosdevice.controller;

import cn.edu.bjtu.ebosdevice.entity.DeviceCount;
import cn.edu.bjtu.ebosdevice.entity.Gateway;
import cn.edu.bjtu.ebosdevice.model.PostedDevice;
import cn.edu.bjtu.ebosdevice.service.*;
import cn.edu.bjtu.ebosdevice.service.impl.DeviceCountServiceImpl;
import cn.edu.bjtu.ebosdevice.service.impl.ProtocolsDict;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import cn.edu.bjtu.ebosdevice.entity.Device;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
    SubscribeService subscribeService;
    @Autowired
    MqFactory mqFactory;
    @Autowired
    ProtocolsDict protocolsDict;
    @Autowired
    DeviceCountService deviceCountService;

    public static final List<RawSubscribe> status = new LinkedList<>();
    private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 50,3, TimeUnit.SECONDS,new SynchronousQueue<>());

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
        logService.info("create","查看了位于网关"+ip+" 下的设备列表");
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

    @ApiOperation(value = "按关键字搜索指定网关下的设备")
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
        logService.info("retrieve","查看了位于网关"+ip+" 下的名字类似为"+keywords+"的设备列表");
        return res;
    }

    @ApiOperation(value = "查看指定网关的指定设备")
    @CrossOrigin
    @GetMapping("/ip/{ip}/id/{id}")
    public JSONObject getThisDevice(@PathVariable String ip, @PathVariable String id){
        String url = "http://"+ip+":48081/api/v1/device/"+id;
        JSONObject jo = restTemplate.getForObject(url,JSONObject.class);
        try {
            Device device = deviceService.findByName(jo.getString("name"));
            jo = deviceService.addInfo2JsonObject(jo,device);
        }catch (Exception ignored){}
        logService.info("retrieve","查看了位于网关"+ip+" 下id="+id+" 的设备信息");
        return jo;
    }

    @ApiOperation(value = "向指定网关添加设备",notes = "需要网关ip")
    @CrossOrigin
    @PostMapping("/ip/{ip}")
    public String addDevice(@PathVariable String ip, @RequestBody PostedDevice postedDevice) {
        String url = "http://" + ip + ":48081/api/v1/device";
        String name = postedDevice.getName();
        if(deviceService.findByName(name) == null) {
            try {
                return getRes(ip, postedDevice, url, name);
            }catch (HttpClientErrorException | ParseException e){
                System.out.println(e.getMessage());
                if (Objects.equals(e.getMessage(), "400 Bad Request: [Invalid object ID: A device must be associated with a device profile\n" +
                        "]")){
                    try{
                        String profileUrl = "http://localhost:8091/api/profile/gateway/"+ip+"/"+postedDevice.getProfile().getName();
                        String profileRes = restTemplate.postForObject(profileUrl,null,String.class);
                        if(profileRes.equals("模板库中无此模板")){return "添加失败 模板库及网关中均无此设备所使用的模板";}
                        return getRes(ip, postedDevice, url, name);
                    }catch (Exception e1){
                        logService.error("create","尝试向"+ip+"添加"+name+"设备失败:"+e1.toString());
                        e1.printStackTrace();
                        return "添加失败! "+e1.toString();
                    }
                }else {
                    logService.error("create","尝试向"+ip+"添加"+name+"设备失败:"+e.toString());
                    System.out.println("添加失败 ");
                    return "添加失败 "+e.toString();
                }
            }
        }else{
            logService.warn("create","尝试向"+ip+"添加"+name+"设备失败:名称重复");
            return "名称重复";
        }
    }

    private String getRes(@PathVariable String ip, @RequestBody PostedDevice postedDevice, String url, String name) throws ParseException {
        String result = restTemplate.postForObject(url, postedDevice, String.class);
        Device device = new Device();
        device.setDeviceName(name);
        device.setGateway(ip);
        device.setDescription(postedDevice.getDescription());
        deviceService.addDevice(device);
        logService.info("create","向"+ip+"添加"+name+"设备成功 Edgex id=" + result);
        Date date = new Date();
        SimpleDateFormat ds =  new SimpleDateFormat("yyyy-MM-dd");
        Date now = ds.parse(ds.format(date));
        addCount(now,ip);
        return "添加成功";
    }

    private void addCount (Date date , String ip) throws ParseException {
        List<DeviceCount> counts = deviceCountService.findRecent();
        SimpleDateFormat ds =  new SimpleDateFormat("yyyy-MM-dd");
        if ( counts.size() == 0 ){
            List<Gateway> gateways = deviceCountService.findGateway();
            for (Gateway gateway : gateways){
                if(gateway.getIp().equals(ip)){
                    deviceCountService.saveGateway(gateway.getName(),date,1);
                    deviceCountService.saveTotal(date,1);
                }else{
                    deviceCountService.saveGateway(gateway.getName(),date,0);
                }
            }
        } else {
            for(DeviceCount count : counts){
                if (ds.format(count.getDate()).equals(ds.format(date))){
                    List<Gateway> gateways = deviceCountService.findGateway();
                    for (Gateway gateway : gateways){
                        if(gateway.getIp().equals(ip)){
                            deviceCountService.addUpdate(date,gateway.getName());
                        }
                    }
                }else{
                    List<Gateway> gateways = deviceCountService.findGateway();
                    for (Gateway gateway : gateways){
                        if(gateway.getIp().equals(ip)){
                            deviceCountService.saveGateway(gateway.getName(),date,1);
                            deviceCountService.saveTotal(date,1);
                        }else{
                            deviceCountService.saveGateway(gateway.getName(),date,0);
                        }
                    }
                }
            }
        }
    }

    @ApiOperation(value = "设备恢复",notes = "需要指定网关ip")
    @CrossOrigin
    @PostMapping("/recover/{ip}")
    public JSONObject plusDevice(@PathVariable String ip, @RequestBody List<PostedDevice> postedDeviceList) {
        logService.info("update", "开始向" + ip + "恢复设备配置");
        String url = "http://" + ip + ":48081/api/v1/device";
        JSONObject result = new JSONObject();
        for (PostedDevice postedDevice : postedDeviceList) {
            String res = "已覆盖更新";
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
                logService.error("update","尝试恢复设备失败："+e.toString());
                result.put(postedDevice.getName(), "失败");
                e.printStackTrace();
            }
        }
        logService.info("update", "恢复结果" + result.toJSONString());
        return result;
    }

    @ApiOperation(value = "删除指定网关下的设备",notes = "需要网关ip")
    @CrossOrigin
    @DeleteMapping("/ip/{ip}/name/{name}")
    public String deleteDevice(@PathVariable String ip, @PathVariable String name) {
        String url = "http://" + ip + ":48081/api/v1/device/name/" + name;
        try {
            if (deviceService.deleteByName(name)) {
                restTemplate.delete(url);
            }
            logService.info("delete","删除网关"+ip+"中的"+name+"设备");
            Date date = new Date();
            SimpleDateFormat ds =  new SimpleDateFormat("yyyy-MM-dd");
            Date now = ds.parse(ds.format(date));
            delCount(now,ip);
            return "done";
        } catch (Exception e) {
            logService.error("delete","删除网关"+ip+"中的"+name+"设备失败"+e.toString());
            return e.toString();
        }
    }

    private void delCount (Date date, String ip) {
        Boolean bool = deviceCountService.judgeTotal(date);
        if (bool){
            deviceCountService.delByDate(date);
        }else {
            List<Gateway> gateways = deviceCountService.findGateway();
            for (Gateway gateway : gateways) {
                if (gateway.getIp().equals(ip)) {
                    deviceCountService.delUpdate(date,gateway.getName());
                    break;
                }
            }
        }
    }

    @ApiOperation(value = "返回每天各个网关增减设备的情况")
    @CrossOrigin
    @GetMapping("/ip/count")
    public JSONObject count() {
        JSONObject result = new JSONObject();
        List<DeviceCount> test = deviceCountService.findRecent();
        if (test.size() == 0){
            return result;
        }else {
            List<DeviceCount> counts = deviceCountService.findAll();
            List<Gateway> gateways = deviceCountService.findGateway();
            SimpleDateFormat dss =  new SimpleDateFormat("yyyy-MM-dd");
            for (Gateway gateway : gateways) {
                JSONObject tem = new JSONObject();
                for (DeviceCount count : counts){
                    if (count.getGateway().equals(gateway.getName())){
                        tem.put(dss.format(count.getDate()),count.getCount());
                    }
                }
                result.put(gateway.getName(),tem);
            }
            for (DeviceCount count : counts){
                JSONObject temp = new JSONObject();
                if (count.getGateway().equals("total")){
                    temp.put(dss.format(count.getDate()),count.getCount());
                }
                result.put("total",temp);
            }
            return result;
        }
    }

    @ApiOperation(value = "协议字典")
    @CrossOrigin
    @GetMapping("/protocol/{name}")
    public JSONObject getProtocol(@PathVariable String name) {
        JSONObject result = new JSONObject();
        result.put(name,protocolsDict.findOption(name));
        return result;
    }

    @ApiOperation(value = "协议字典目录")
    @CrossOrigin
    @GetMapping("/protocol")
    public Set<String> getProtocolKeys(){
        return protocolsDict.getKeys();
    }

    @ApiOperation(value = "设备服务列表",notes = "用于添加设备时选择服务")
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

    @ApiOperation(value = "测试指定设备",notes = "也可用于读取设备读数")
    @CrossOrigin
    @GetMapping("/details/{ip}/{id}")
    public JSONObject getDetails(@PathVariable String ip, @PathVariable String id){return deviceService.getDeviceDetail(ip, id);}

    @ApiOperation(value = "监测在线设备")
    @CrossOrigin
    @GetMapping("/online/{ip}")
    public JSONArray getOnline(@PathVariable String ip){return deviceService.getOnlineDevices(ip);}

    @ApiOperation(value = "微服务订阅mq的主题")
    @CrossOrigin
    @PostMapping("/subscribe")
    public String newSubscribe(RawSubscribe rawSubscribe){
        if(!DeviceController.check(rawSubscribe.getSubTopic())){
            try{
                status.add(rawSubscribe);
                subscribeService.save(rawSubscribe.getSubTopic());
                threadPoolExecutor.execute(rawSubscribe);
                logService.error("create","设备管理已订阅主题"+rawSubscribe.getSubTopic()+",再次订阅失败");
                return "订阅成功";
            }catch (Exception e) {
                e.printStackTrace();
                logService.error("create","设备管理订阅主题"+rawSubscribe.getSubTopic()+"时，参数设定有误。");
                return "参数错误!";
            }
        }else {
            logService.error("create","设备管理已订阅主题"+rawSubscribe.getSubTopic()+",再次订阅失败");
            return "订阅主题重复";
        }
    }

    public static boolean check(String subTopic){
        boolean flag = false;
        for (RawSubscribe rawSubscribe : status) {
            if(subTopic.equals(rawSubscribe.getSubTopic())){
                flag=true;
                break;
            }
        }
        return flag;
    }

    @ApiOperation(value = "删除微服务订阅mq的主题")
    @CrossOrigin
    @DeleteMapping("/subscribe/{subTopic}")
    public boolean delete(@PathVariable String subTopic){
        boolean flag;
        synchronized (status){
            flag = status.remove(search(subTopic));
        }
        return flag;
    }

    public static RawSubscribe search(String subTopic){
        for (RawSubscribe rawSubscribe : status) {
            if(subTopic.equals(rawSubscribe.getSubTopic())){
                return rawSubscribe;
            }
        }
        return null;
    }

    @ApiOperation(value = "微服务向mq的某主题发布消息")
    @CrossOrigin
    @PostMapping("/publish")
    public String publish(@RequestParam(value = "topic") String topic,@RequestParam(value = "message") String message){
        MqProducer mqProducer = mqFactory.createProducer();
        mqProducer.publish(topic,message);
        return "发布成功";
    }

    @ApiOperation(value = "微服务健康监测")
    @CrossOrigin
    @GetMapping("/ping")
    public String ping(){
        logService.info("retrieve","对设备管理进行了一次健康检测");
        return "pong";
    }

    @ApiOperation(value = "测试用API", notes = "写一堆乱七八糟的日志进去")
    @CrossOrigin
    @PostMapping ("/logtest")
    public String logTest(){
        logService.debug("create","gwinst1");
        logService.info("delete","gwinst2");
        logService.warn("update","gwinst3");
        logService.error("retrieve","gwinst4");
        logService.debug("retrieve","增");
        logService.info("update","删");
        logService.warn("delete","改");
        logService.error("create","查");
        return "成功";
    }

}
