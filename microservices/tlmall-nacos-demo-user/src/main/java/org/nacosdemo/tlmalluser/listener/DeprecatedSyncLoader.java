package org.nacosdemo.tlmalluser.listener;

import org.nacosdemo.tlmalluser.listener.handler.NacosManualRegistrater;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import lombok.extern.slf4j.Slf4j;
import org.springcloudmvp.tlmallcommon.Result;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestTemplate;

/**
 * 实验2.2 微服务启动阶段访问下游（方法3）
 * (1) 开启下面的配置和
 *     # spring.cloud.nacos.discovery.register-enabled: false   # 阻止实例在数据加载完之前注册到Nacos
 *     # spring.cloud.nacos.discovery.enabled: true             # 保持Nacos Discovery在启动状态
 * (2) 把 StartupAsyncLoader 和 StartupSyncLoader 的 @Component 注释掉，防止它们提前注册，造成干扰
 * (3) 把 UserService 的 afterPropertiesSet 方法中所有代码都注释掉，防止它提前触发注册，造成干扰
 * (4) 取消这个类（DeprecatedSyncLoader）中 @Component 的注释，让它生效
 *
 * 但是并不推荐这个方法，原因如下
 * (1) 这个方法只解决了启动阶段如何延迟注册
 * (2) register-enabled=false关闭注册功能的同时，也会把服务变成只能发现其他服务但不能被发现的纯客户端。
 * 除了手动注册，还需额外实现：
 * - 心跳维持
 * - 优雅下线
 * - 元数据更新
 * - 健康状态切换
 * - ……
 */
//@Component
@Deprecated
@Slf4j
public class DeprecatedSyncLoader {
    private final RestTemplate restTemplate;
    private final NacosManualRegistrater nacosManualRegistrater;

    public DeprecatedSyncLoader(
            RestTemplate restTemplate,
            NacosManualRegistrater nacosManualRegistrater) {
        this.restTemplate = restTemplate;
        this.nacosManualRegistrater = nacosManualRegistrater;
    }

//    @EventListener(ContextRefreshedEvent.class)
//    public void onApplicationReady(ContextRefreshedEvent event) throws InterruptedException {
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() throws InterruptedException {
        // 初始化开始
        log.info("同步远程数据初始化...");

        // 加载各类业务数据
        loadOrderData();

        // 初始化完成
        log.info("同步远程数据初始化完成");

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
            log.info("数据加载所需时间较长，预计30秒");
            Thread.sleep(30000);

            // 4. 加载完成
            log.info("数据加载成功: {}", result);
        } catch (Exception e) {
            log.error("数据加载失败", e);
            throw e;
        }
    }
}
