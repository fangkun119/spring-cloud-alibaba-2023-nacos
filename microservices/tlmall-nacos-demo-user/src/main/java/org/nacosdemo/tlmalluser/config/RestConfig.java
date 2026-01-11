package org.nacosdemo.tlmalluser.config;


import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Configuration
public class RestConfig {

    /**
     * 默认的RestTemplate
     *
     * 使用@LoadBalanced注解，Spring Cloud LoadBalancer会在所有Bean都装配完毕后，来给它注入负债均衡能力
     * 因此在Bean装配期间，它只是普通RestTemplate，无法通过微服务名来调用下游
     *
     * 这个Bean可用于分析Spring Boot LoadBalancer的底层实现
     */
      @Bean
      @Primary
      @LoadBalanced
      public RestTemplate restTemplate() {
        return new RestTemplate();
      }

     /**
     * 实验2.1（方法1）：手动添加Spring Cloud Load Balancer拦截器，它可以
     * 它可以在Bean装配阶段就
     *   具备负载均衡能力
     *   调用下游获取Bean
     */
     @Bean(MANUAL_INTERCEPT)
     public RestTemplate manualInterceptRestTemplate(LoadBalancerInterceptor loadBalancerInterceptor) {
       RestTemplate restTemplate = new RestTemplate();
       // 注入loadBalancerInterceptor拦截器（具有负载均衡的能力）
       restTemplate.setInterceptors(Arrays.asList(loadBalancerInterceptor));
       return restTemplate;
     }

     public static final String MANUAL_INTERCEPT = "manualIntercepted";
}
