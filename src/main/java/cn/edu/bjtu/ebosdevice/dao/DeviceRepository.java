package cn.edu.bjtu.ebosdevice.dao;

import cn.edu.bjtu.ebosdevice.entity.Device;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceRepository extends MongoRepository<Device,String> {
    Device findDeviceByDeviceName(String deviceName);
    Page<Device> findAll(Pageable pageable);
    Device findDeviceByDeviceId(String deviceId);
    Device findByEdgexId(String id);
    Device findByDeviceName(String name);
    Device findByDeviceNameAndGateway(String name, String ip);
    void deleteByEdgexId(String id);
    void deleteByDeviceName(String name);
    void deleteByDeviceId(String id);
    Page<Device> findDeviceByDeviceType(String deviceType, Pageable pageable);
    List<Device> findAll();
}
