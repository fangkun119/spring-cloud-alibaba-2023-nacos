package org.nacosdemo.tlmalluseropenfeigndemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
// 开启openFeign功能
@EnableFeignClients
public class TlmallUserOpenfeignDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(TlmallUserOpenfeignDemoApplication.class, args);
    }

}
