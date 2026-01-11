package org.nacosdemo.tlmalluser.config;


import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerInterceptor;
import org.springframework.cloud.client.loadbalancer.LoadBalancerRequestFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestConfig {
    /**
     * 默认的RestTemplate
     * <p>
     * 使用@LoadBalanced注解，Spring Cloud LoadBalancer会在所有Bean都装配完毕后，来给它注入负债均衡能力
     * 因此在Bean装配期间，它只是普通RestTemplate，无法通过微服务名来调用下游
     * <p>
     * 这个Bean可用于分析Spring Boot LoadBalancer的底层实现
     */
    @Bean
    @LoadBalanced
    @Primary
    public RestTemplate defaultRestTemplate() {
        return new RestTemplate();
    }

    /**
     * 实验2.1（方法1）：手动添加Spring Cloud Load Balancer拦截器，它可以
     * 它可以在Bean装配阶段，就具备负载均衡能力，并调用下游
     * <p>
     * 注意：
     * (1) 不要添加@LoadBalanced注解，会被重复定制化
     * (2) 逻辑需要与LoadBalancerInterceptorConfig对RestTemplate做的定制化操作保持一致
     */
    @Bean(EARLY_BALANCER_INJECTED)
    public RestTemplate earlyBalanceInjectedTemplate(LoadBalancerClient loadBalancerClient,
                                                     LoadBalancerRequestFactory requestFactory) {
        // 实例化RestTemplate
        RestTemplate restTemplate = new RestTemplate();
        // 添加拦截器
        LoadBalancerInterceptor loadBalancerInterceptor
                = new LoadBalancerInterceptor(loadBalancerClient, requestFactory);
        restTemplate.getInterceptors().add(loadBalancerInterceptor);
        return restTemplate;
    }

    public static final String EARLY_BALANCER_INJECTED = "early-balancer-injected";
}
