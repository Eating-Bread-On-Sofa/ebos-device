package cn.edu.bjtu.ebosdevice.model;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public class PostedDevice {
    private String name;
    private String description;
    @ApiModelProperty(example = "UNLOCKED")
    private String adminState;
    @ApiModelProperty(example = "ENABLED")
    private String operatingState;
    private JSONObject protocols;
    private List<String> labels;
    private PostedDeviceService service;
    private PostedDeviceProfile profile;
    private List<AutoEvent> autoEvents;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public JSONObject getProtocols() {
        return protocols;
    }

    public void setProtocols(JSONObject protocols) {
        this.protocols = protocols;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public PostedDeviceService getService() {
        return service;
    }

    public void setService(PostedDeviceService service) {
        this.service = service;
    }

    public PostedDeviceProfile getProfile() {
        return profile;
    }

    public void setProfile(PostedDeviceProfile profile) {
        this.profile = profile;
    }

    public List<AutoEvent> getAutoEvents() {
        return autoEvents;
    }

    public void setAutoEvents(List<AutoEvent> autoEvents) {
        this.autoEvents = autoEvents;
    }

    public String getAdminState() {
        return adminState;
    }

    public void setAdminState(String adminState) {
        this.adminState = adminState;
    }

    public String getOperatingState() {
        return operatingState;
    }

    public void setOperatingState(String operatingState) {
        this.operatingState = operatingState;
    }
}
