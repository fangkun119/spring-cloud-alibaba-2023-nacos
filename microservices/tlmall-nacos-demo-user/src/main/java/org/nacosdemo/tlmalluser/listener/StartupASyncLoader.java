package org.nacosdemo.tlmalluser.listener;

import lombok.extern.slf4j.Slf4j;
import org.nacosdemo.tlmalluser.listener.handler.NacosManualRegistrater;
import org.springcloudmvp.tlmallcommon.Result;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

/**
 * 实验2.1 微服务启动阶段访问下游（方法3）
 *
 * 取消下面@Component的注释即可
 *
 * 这个方法能够在服务注册阶段调用下游加载启动数据
 * 但是：
 * - 此时服务已经在接收上游请求
 * - 会遇到并发同步问题，需需在操作中自行处理
 */
// @Component
@Slf4j
public class StartupASyncLoader implements CommandLineRunner {
    private final RestTemplate restTemplate;

    public StartupASyncLoader(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        // 初始化开始
        log.info("远程数据初始化开始...");

        // 提交异步数据加载
        CompletableFuture.runAsync(this::loadOrderData);
        log.info("远程数据初始化已提交异步执行");
    }

    private void loadOrderData() {
        try {
            // 0. 准备兜底用的数据
            // ……

            // 1. 执行远程调用
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
            log.info("启动数据加载成功: {}", result);
        } catch (Exception e) {
            log.error("启动数据加载失败，回退兜底数据", e);

            // 继续使用兜底数据
            // ……
        }
    }
}
