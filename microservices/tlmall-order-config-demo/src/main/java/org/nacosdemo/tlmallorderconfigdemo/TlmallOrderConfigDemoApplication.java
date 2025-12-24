package org.nacosdemo.tlmallorderconfigdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling   // 开启定时任务功能
public class TlmallOrderConfigDemoApplication {

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(TlmallOrderConfigDemoApplication.class, args);

        while (true) {
            //当动态配置刷新时，会更新到 Enviroment中，因此这里每隔3秒中从Enviroment中获取配置
            String count = applicationContext.getEnvironment().getProperty("order.count");
            System.err.println("order.count:" + count);
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
