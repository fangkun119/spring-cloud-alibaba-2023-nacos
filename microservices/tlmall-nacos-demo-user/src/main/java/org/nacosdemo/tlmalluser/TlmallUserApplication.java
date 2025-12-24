package org.nacosdemo.tlmalluser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// 设置全局的负载均衡策略
//@LoadBalancerClients(defaultConfiguration = LoadBalancerConfig.class)
public class TlmallUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(TlmallUserApplication.class, args);
    }

}
