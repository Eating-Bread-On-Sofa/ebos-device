package cn.edu.bjtu.ebosdevice.service.impl;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "protocols")
@EnableConfigurationProperties(ProtocolsDict.class)
public class ProtocolsDict {
    private Map<String, String> protocol = new HashMap<>();

    public Map<String, String> getProtocol() {
        return protocol;
    }

    public void setProtocol(Map<String, String> protocol) {
        this.protocol = protocol;
    }
}
