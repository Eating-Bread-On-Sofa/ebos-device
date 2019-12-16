package cn.edu.bjtu.ebosdevice.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document
public class Device {
    @Id
    private String deviceId;
    private String deviceName;
    private String deviceDesc;
    private String deviceType;
    private int deviceStatus;
    private Date deviceCreateTime;
    private String deviceAddress;
    private int deviceAddressPort;
    private String deviceProfile;
    private String deviceFailDate;
    private String deviceProtocol;
    private String devicePath;
    private String deviceLocation;
    private String edgexId;

    public String getEdgexId() {
        return edgexId;
    }

    public void setEdgexId(String edgexId) {
        this.edgexId = edgexId;
    }

    public String getDeviceLocation() { return deviceLocation; }

    public void setDeviceLocation(String deviceLocation) { this.deviceLocation = deviceLocation; }

    public String getDevicePath() { return devicePath; }

    public void setDevicePath(String devicePath) { this.devicePath = devicePath; }

    public String getDeviceProtocol() { return deviceProtocol; }

    public void setDeviceProtocol(String deviceProtocol) { this.deviceProtocol = deviceProtocol; }

    public String getDeviceFailDate() { return deviceFailDate; }

    public void setDeviceFailDate(String deviceFailDate) { this.deviceFailDate = deviceFailDate; }

    public int getDeviceAddressPort() { return deviceAddressPort; }

    public void setDeviceAddressPort(int deviceAddressPort) { this.deviceAddressPort = deviceAddressPort; }

    public String getDeviceProfile() { return deviceProfile; }

    public void setDeviceProfile(String deviceProfile) { this.deviceProfile = deviceProfile; }

    public String getDeviceAddress() { return deviceAddress; }

    public void setDeviceAddress(String deviceAddress) { this.deviceAddress = deviceAddress; }

    public Date getDeviceCreateTime() {
        return deviceCreateTime;
    }

    public void setDeviceCreateTime(Date deviceCreateTime) {
        this.deviceCreateTime = deviceCreateTime;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceDesc() {
        return deviceDesc;
    }

    public void setDeviceDesc(String deviceDesc) {
        this.deviceDesc = deviceDesc;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public int getDeviceStatus() { return deviceStatus; }

    public void setDeviceStatus(int deviceStatus) {this.deviceStatus = deviceStatus; }

}
