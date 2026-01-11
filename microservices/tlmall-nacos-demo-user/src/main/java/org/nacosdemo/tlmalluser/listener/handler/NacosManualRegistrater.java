package org.nacosdemo.tlmalluser.listener.handler;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

@Component
@Slf4j
public class NacosManualRegistrater {
    private final NacosServiceManager nacosServiceManager;
    private final NacosDiscoveryProperties discoveryProperties;
    private final ApplicationContext applicationContext;

    private boolean isRegistered = false;

    public NacosManualRegistrater(
            NacosServiceManager nacosServiceManager,
            NacosDiscoveryProperties discoveryProperties,
            ApplicationContext applicationContext) {
        this.nacosServiceManager = nacosServiceManager;
        this.discoveryProperties = discoveryProperties;
        this.applicationContext = applicationContext;
    }

    /**
     * 在自定义操作完成后调用此方法
     */
    public void register() {
        if (isRegistered) {
            log.warn("Nacos服务已注册，无需重复操作");
            return;
        }

        try {
            // 手动触发注册（2023.0.1.0 推荐方式）
            NamingService namingService = nacosServiceManager.getNamingService(discoveryProperties.getNacosProperties());

            Instance instance = new Instance();
            instance.setIp(getServiceIp());
            instance.setPort(getServicePort());
            instance.setServiceName(discoveryProperties.getService());
            instance.setWeight(discoveryProperties.getWeight());
            instance.setClusterName(discoveryProperties.getClusterName());
            instance.setMetadata(discoveryProperties.getMetadata());
            instance.setEphemeral(discoveryProperties.isEphemeral());

            namingService.registerInstance(instance.getServiceName(), instance);

            // 更新状态
            isRegistered = true;
            log.info("Nacos服务手动注册成功: {}:{}/{}",
                    instance.getIp(), instance.getPort(), instance.getServiceName());

        } catch (Exception e) {
            // 注册异常
            log.error("Nacos手动注册失败", e);
            throw new RuntimeException("服务注册失败", e);
        }
    }

    /**
     * 获取服务IP（优先使用Nacos自动获取的）
     */
    private String getServiceIp() throws Exception {
        String ip = discoveryProperties.getIp();
        if (ip != null && !ip.isEmpty()) {
            return ip;
        }

        // 备用方案
        InetAddress inetAddress = InetAddress.getLocalHost();
        return inetAddress.getHostAddress();
    }

    /**
     * 获取服务端口
     */
    private int getServicePort() {
        // 首选：使用Nacos自动解析出来的
        int port = discoveryProperties.getPort();
        if (port > 0) {
            return port;
        }
        // 备用：从Environment获取
        String portStr = applicationContext.getEnvironment().getProperty("server.port", "-1");
        return Integer.parseInt(portStr);
    }
}
