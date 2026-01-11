package org.nacosdemo.tlmalluser.config;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

//@Configuration
public class NacosMetadataConfig {
    @Bean
    public NacosDiscoveryProperties nacosProperties(Environment env) {
        NacosDiscoveryProperties properties = new NacosDiscoveryProperties();
        Map<String, String> metadata = properties.getMetadata();
        metadata.put("startup.time",
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        metadata.put("java.version", env.getProperty("java.version"));
        metadata.put("build.number", env.getProperty("build.number", "unknown"));
        return properties;
    }
}