package cn.edu.bjtu.ebosdevice.model;

import io.swagger.annotations.ApiModelProperty;

public class AutoEvent {
    @ApiModelProperty(example = "Humidity")
    private String resource;
    @ApiModelProperty(example = "30s")
    private String frequency;

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }
}
