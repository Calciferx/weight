package com.calcifer.weight.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "weight")
public class ScaleConfigProperties {
    private String activeScale;
    private Map<String, ScaleConfig> scales;

    @Data
    public static class ScaleConfig {
        private int frameLength = 18;
        private String terminator = "0D0A";
        private int weightStart = 16;
        private int weightEnd = 28;
        private int statusStart = 0;
        private int statusEnd = 4;
        private String stableValue = "5354";
        private boolean reverse = false;
    }
}
