package org.nacosdemo.tlmalluser.controller;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.nacosdemo.tlmalluser.config.RestConfig;
import org.nacosdemo.tlmalluser.config.loadbalancer.RandomLoadBalancerConfig;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springcloudmvp.tlmallcommon.Result;

@RestController
@RequestMapping("/user")
@Slf4j
// public class UserController implements InitializingBean {
public class UserController {

    // @Qualifier(RestConfig.MANUAL_INTERCEPT)
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @RequestMapping(value = "/getOrder")
    public Result<?> getOrderByUserId(@RequestParam("userId") String userId) {
        log.info("根据userId:"+userId+"查询订单信息");

        // 方式1：restTemplate调用,url写死
        // String url = "http://localhost:8060/order/getOrder?userId="+userId;

        // 方式2： 利用负载均衡器获取tlmall-order服务列表
        // ServiceInstance serviceInstance = loadBalancerClient.choose("tlmall-order");
        // String url = String.format("http://%s:%s/order/getOrder?userId=%s",serviceInstance.getHost(),serviceInstance.getPort(),userId);
        // log.info(serviceInstance.getHost()+":"+serviceInstance.getPort());

        //方式3：利用@LoadBalanced，restTemplate需要添加@LoadBalanced注解
        String url = "http://tlmall-order/order/getOrder?userId="+userId;
        Result result = restTemplate.getForObject(url,Result.class);
        return result;
    }

    // 用于分析@LoadBalanced底层实现，该方法在依赖注入完成之后执行，具体顺序如下：
    //  → 实例化
    //  → 填充属性（依赖注入）
    //  → @PostConstruct
    //  → InitializingBean.afterPropertiesSet() // 本方法被调用
    //  → init-method
    //  → **所有单例**初始化完成
    //  → SmartInitializingSingleton.afterSingletonsInstantiated() // Spring Cloud LoadBalancer为注解了@LoadBalanced的RestTemplate添加负载均衡
    //  → ApplicationContext.refresh()完成
    //  → ContextRefreshedEvent
    //  → Spring Boot启动 → CommandLineRunner → ApplicationRunner → ApplicationReadyEvent → 接收请求 → 容器关闭 →
    //  → @PreDestroy → DisposableBean.destroy() → destroy-method
//    @Override
//    public void afterPropertiesSet() throws Exception {
//        String url = "http://tlmall-order/order/getOrder?userId=fox";
//        restTemplate.getForObject(url, Result.class);
//    }
}