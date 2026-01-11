package org.nacosdemo.tlmalluser;

import com.alibaba.cloud.nacos.loadbalancer.NacosLoadBalancer;
import org.nacosdemo.tlmalluser.config.loadbalancer.IPHashLoadBalancerConfig;
import org.nacosdemo.tlmalluser.config.loadbalancer.RandomLoadBalancerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;

/**
 * // (1) 负载均衡策略配置实验
 * // 方法1：全局统一指定均衡策略
 *
 * @LoadBalancerClients(defaultConfiguration = RandomLoadBalancerConfig.class)
 * // 方法2：按照下游微服务名指定策略
 * // @LoadBalancerClients({
 * //    @LoadBalancerClient(name = "tlmall-order", configuration = RandomLoadBalancerConfig.class)
 * // })
 * @SpringBootApplication public class TlmallUserApplication {
 * public static void main(String[] args) {
 * SpringApplication.run(TlmallUserApplication.class, args);
 * }
 * }
 */

// (2) 自定义负载均衡实验
// 方法1：指定一个全局统一的策略
// @LoadBalancerClients(defaultConfiguration = RandomLoadBalancerConfig.class)
// 方法2：为特定下游指定特定策略
@LoadBalancerClients(value = {
    @LoadBalancerClient(name = "tlmall-order", configuration = IPHashLoadBalancerConfig.class)}
)
@SpringBootApplication
public class TlmallUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(TlmallUserApplication.class, args);
    }
}

