package org.nacosdemo.tlmalluser.config;


import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;


/**
 * @author Fox
 */
@Configuration
public class RestConfig {
    
//    @Bean
//    @LoadBalanced
//    public RestTemplate restTemplate() {
//        return new RestTemplate();
//    }

    @Bean
    public RestTemplate restTemplate(LoadBalancerInterceptor loadBalancerInterceptor) {
        RestTemplate restTemplate = new RestTemplate();
        //注入loadBalancerInterceptor拦截器（具有负载均衡的能力）
        restTemplate.setInterceptors(Arrays.asList(loadBalancerInterceptor));
        return restTemplate;
    }

}
