package cn.edu.bjtu.ebosdevice.service.impl;

import cn.edu.bjtu.ebosdevice.dao.DeviceRepository;
import cn.edu.bjtu.ebosdevice.entity.Device;
import cn.edu.bjtu.ebosdevice.service.DeviceService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class DeviceServiceImpl implements DeviceService {

    //private final DateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    DeviceRepository deviceRepository;


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
    public boolean addDevice(Device device) {
        Device findDevice = deviceRepository.findDeviceByDeviceName(device.getDeviceName());
        if (findDevice == null) {
            Device device1 = deviceRepository.save(device);
            ObjectId objectId = new ObjectId(device1.getDeviceId());
            device1.setDeviceCreateTime(objectId.getDate());
            deviceRepository.save(device1);
            return true;

        } else {
            return false;
        }
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
}
