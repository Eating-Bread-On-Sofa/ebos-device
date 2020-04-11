package cn.edu.bjtu.ebosdevice.service;

import cn.edu.bjtu.ebosdevice.entity.Device;
import com.alibaba.fastjson.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DeviceService {
    Page<Device> findAllDevice(Pageable pageable);
    List<Device> findAllDevice();
    String addDevice(Device device);
    String plusDevice(Device device);
    String deleteDevice(String deviceId);
    Device findDeviceByDeviceId(String deviceId);
    Device findByName(String name);
    boolean deleteByEdgexId(String id);
    boolean deleteByName(String name);
    Page<Device> findDeviceByDeviceType(String deviceType, Pageable pageable);
    void changeDeviceStatus(Device dev, int status);
    JSONObject addInfo2Json(JSONObject jo, Device device);
}
