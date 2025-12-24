package org.nacosdemo.tlmalluserconfigdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
// 开启openFeign功能
@EnableFeignClients
public class TlmallUserConfigDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(TlmallUserConfigDemoApplication.class, args);
    }

}
