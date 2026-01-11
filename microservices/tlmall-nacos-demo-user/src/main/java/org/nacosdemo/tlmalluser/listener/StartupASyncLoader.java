package org.nacosdemo.tlmalluser.listener;

import lombok.extern.slf4j.Slf4j;
import org.nacosdemo.tlmalluser.listener.handler.NacosManualRegistrater;
import org.springcloudmvp.tlmallcommon.Result;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

/**
 * 实验2.2 微服务启动阶段访问下游（方法2）
 * (1) 确保下面配置被注释，使得自动注册保持在默认的开启状态
 *     # spring.cloud.nacos.discovery.register-enabled: false
 * (2) 确保DeprecatedSyncLoader, StartupAsyncLoader的@Component注解被注释掉，防止它们提前注册，造成干扰
 * (3) 把 UserService 的 afterPropertiesSet 方法中所有代码都注释掉，防止它提前触发注册，造成干扰
 * (4) 取消这个类（StartupSyncLoader）中 @Component 的注释，让它生效
 *
 * 这个方法能够在应用完全启动后（服务已注册并准备好接收请求）调用下游加载启动数据
 *
 * 注意：此时服务已经在接收上游请求
 * - 会遇到并发同步问题
 * - 需要代码额外处理
 */
// @Component
@Slf4j
public class StartupASyncLoader {

    private final RestTemplate restTemplate;

    public StartupASyncLoader(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        // 初始化开始
        log.info("异步远程数据初始化开始...");

        // 提交异步数据加载
        CompletableFuture.runAsync(this::loadOrderData);
        log.info("异步远程数据初始化已提交异步执行");
    }

    private void loadOrderData() {
        try {
            // 1. 准备兜底用的数据
            // ……

            // 2. 执行远程调用
            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(8000);
            factory.setReadTimeout(8000);
            restTemplate.setRequestFactory(factory);
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

            // 3. 从兜底数据切换到远程调用获取的数据
            // ……

            // 4. 加载完成
            log.info("异步数据加载成功: {}", result);
        } catch (Exception e) {
            log.error("异步数据加载失败，回退兜底数据", e);

            // 继续使用兜底数据
            // ……
        }
    }
}
