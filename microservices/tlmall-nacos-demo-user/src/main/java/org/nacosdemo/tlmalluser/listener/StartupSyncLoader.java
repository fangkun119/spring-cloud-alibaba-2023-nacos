package org.nacosdemo.tlmalluser.listener;

import lombok.extern.slf4j.Slf4j;
import org.nacosdemo.tlmalluser.listener.handler.NacosInstanceStatusManager;
import org.springcloudmvp.tlmallcommon.Result;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * 实验2.2 微服务启动阶段访问下游（方法1）
 * (1) 确保下面配置被注释，使得自动注册保持在默认的开启状态
 *     # spring.cloud.nacos.discovery.register-enabled: false
 * (2) 确保DeprecatedSyncLoader, StartupAsyncLoader的@Component注解被注释掉，防止它们提前注册，造成干扰
 * (3) 把 UserService 的 afterPropertiesSet 方法中所有代码都注释掉，防止它提前触发注册，造成干扰
 * (4) 取消这个类（StartupSyncLoader）中 @Component 的注释，让它生效
 *
 * 这个阶段、实例已经注册到Nacos，
 * - 主动设置Healthy状态为false阻止上游请求
 * - 加载启动数据
 * - 设置Healthy状态为true接收上游请求
 *
 * 这样只能缓解，并不能彻底解决问题，依然有诸多事项要考虑，包括
 * - 并发问题：同样在启动阶段，上游依然有可能发送请求过来（例如实例重启、但是上游的实例缓存还没有更新），但是与上一个方法不同的是，**此时服务已经能够接收上游请求、可以引起并发问题**。这意味着要手动拒绝这些请求，避免同步问题。
 * - 干扰监控：设置”healthy=false“会干扰监控，难以区分"正常不可用"和"异常不可用"。
 * - 框架耦合：设置”healthy=false“用到了Nacos内部的Bean，依然依赖Nacos的特定实现。
 *
 * 总之最好的办法还是定制RestTemplate Bean，参考UserService和RestConfig
 */
// @Component
@Slf4j
public class StartupSyncLoader {
    private final RestTemplate restTemplate;
    private final NacosInstanceStatusManager instanceStatusManager;

    public StartupSyncLoader(
            RestTemplate restTemplate,
            NacosInstanceStatusManager instanceStatusManager) {
        this.restTemplate = restTemplate;
        this.instanceStatusManager = instanceStatusManager;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() throws InterruptedException {
        // 初始化开始
        log.info("同步远程数据初始化...");
        log.info("服务实例设为heathy=false，阻止上游请求");
        instanceStatusManager.setHealthy(false);

        // 加载各类业务数据
        loadOrderData();

        // 初始化完成
        log.info("同步远程数据初始化完成");
        log.info("服务实例设为heathy=true，开放上游请求");
        instanceStatusManager.setHealthy(true);
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
            String result = RetryTemplate.builder()
                    .maxAttempts(5)
                    .fixedBackoff(2000)
                    .retryOn(Exception.class)
                    .build()
                    .execute(context -> {
                        log.info("尝试第{}次加载数据", context.getRetryCount() + 1);
                        String url = "http://tlmall-order/order/getOrder?userId=fox";
                        return restTemplate.getForObject(url, String.class);
                    });

            // 3. 模拟超长时间远程调用
            log.info("数据加载所需时间较长，预计30秒");
            Thread.sleep(30000);

            // 4. 加载完成
            log.info("获取启动数据: {}", result);
            log.info("数据加载成功");
        } catch (Exception e) {
            log.error("数据加载失败", e);
            throw e;
        }
    }
}
