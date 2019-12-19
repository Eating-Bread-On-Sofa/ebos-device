package cn.edu.bjtu.ebosdevice.service;

import cn.edu.bjtu.ebosdevice.entity.Device;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DeviceService {
    public Page<Device> findAllDevice(Pageable pageable);
    public List<Device> findAllDevice();
    public boolean addDevice(Device device);
    public String deleteDevice(String deviceId);
    public Device findDeviceByDeviceId(String deviceId);
    Device findByName(String name);
    boolean deleteByEdgexId(String id);
    boolean deleteByName(String name);
    public Page<Device> findDeviceByDeviceType(String deviceType, Pageable pageable);
    public void changeDeviceStatus(Device dev, int status);
}
