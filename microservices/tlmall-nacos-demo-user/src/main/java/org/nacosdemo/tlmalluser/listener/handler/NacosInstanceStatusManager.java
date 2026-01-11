package org.nacosdemo.tlmalluser.listener.handler;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NacosInstanceStatusManager {
    @Autowired
    private NacosServiceManager nacosServiceManager;

    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    public void setHealthy(boolean isHealthy) {
        try {
            // 1. 获取 NamingService
            NamingService namingService = nacosServiceManager.getNamingService();
            String serviceName = nacosDiscoveryProperties.getService();
            String groupName = nacosDiscoveryProperties.getGroup();

            // 2. 构建实例对象
            Instance instance = new Instance();
            instance.setIp(nacosDiscoveryProperties.getIp());
            instance.setPort(nacosDiscoveryProperties.getPort());
            instance.setWeight(nacosDiscoveryProperties.getWeight());
            instance.setMetadata(nacosDiscoveryProperties.getMetadata());
            instance.setHealthy(isHealthy); // 这里设置健康状态

            // 3. 更新健康状态
            namingService.registerInstance(serviceName, groupName, instance);
            log.info("服务健康状态更新为：{}", isHealthy);
        } catch (Exception e) {
            log.error("健康状态更新失败: " + e.getMessage(), e);
            throw new RuntimeException("健康状态更新失败", e);
        }
    }
}

