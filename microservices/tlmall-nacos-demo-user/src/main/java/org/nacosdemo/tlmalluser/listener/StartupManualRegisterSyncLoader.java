package org.nacosdemo.tlmalluser.listener;

import org.nacosdemo.tlmalluser.listener.handler.NacosManualRegistrater;
import org.springframework.boot.CommandLineRunner;
import lombok.extern.slf4j.Slf4j;
import org.springcloudmvp.tlmallcommon.Result;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestTemplate;

/**
 * 实验2.1 微服务启动阶段访问下游（方法3）
 * (1) 开启下面的配置和
 * spring.cloud.nacos.discovery.enabled: true             # 保持Nacos Discovery在启动状态
 * spring.cloud.nacos.discovery.register-enabled: false   # 阻止实例在数据加载完之前注册到Nacos
 * (2) 取消 StartupSyncLoader 和 NacosManualRegistrater 中的 @Component注解
 *
 * 但是并不推荐这个方法，原因如下
 * (1) Nacos官方并不提供“延迟注册”的功能，这是参照特定版本源码的”Walk Around“
 * (2) 这个方法只解决了启动阶段如何延迟注册，而“心跳断联”后重新注册等问题，并没有解决
 * (3) 继续打“布丁”会越打越复杂
 * (4) 所有实现都依赖Nacos Registry的特定版本的代码实现，耦合太深
 */
// @Component
@Slf4j
public class StartupManualRegisterSyncLoader implements CommandLineRunner {
    private final RestTemplate restTemplate;
    private final NacosManualRegistrater nacosManualRegistrater;

    public StartupManualRegisterSyncLoader(
            RestTemplate restTemplate, NacosManualRegistrater nacosManualRegistrater) {
        this.restTemplate = restTemplate;
        this.nacosManualRegistrater = nacosManualRegistrater;
    }

    @Override
    public void run(String... args) throws Exception {
        // 初始化开始
        log.info("远程数据初始化开始...");

        // 加载各类业务数据
        loadOrderData();

        // 初始化完成
        log.info("远程数据初始化完成");

        // 注册到Nacos
        log.info("注册到注册中心");
        nacosManualRegistrater.register();
    }

    private void loadOrderData() throws InterruptedException {
        try {
            // 0. 模拟超长时间远程调用
            // log.info("启动数据加载所需时间较长，预计30秒");
            // Thread.sleep(30000);

            // 1. 设置超时
            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(8000);
            factory.setReadTimeout(8000);
            restTemplate.setRequestFactory(factory);

            // 2. 执行远程调用（带重试）
            Result<?> result = RetryTemplate.builder()
                    .maxAttempts(5)
                    .fixedBackoff(2000)
                    .retryOn(Exception.class)
                    .build()
                    .execute(context -> {
                        log.info("尝试第{}次加载数据", context.getRetryCount() + 1);
                        String url = "http://tlmall-order/order/getOrder?userId=fox";
                        return restTemplate.getForObject(url, Result.class);
                    });

            // 3. 模拟超长时间远程调用
            log.info("启动数据加载所需时间较长，预计30秒");
            Thread.sleep(30000);

            // 4. 加载完成
            log.info("启动数据加载成功: {}", result);
        } catch (Exception e) {
            log.error("启动数据加载失败", e);
            throw e;
        }
    }
}
