package org.nacosdemo.tlmalluser.service;

import lombok.extern.slf4j.Slf4j;
import org.nacosdemo.tlmalluser.config.RestConfig;
import org.springcloudmvp.tlmallcommon.Result;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;


/**
 * 实验2.1 Beaa装配阶段访问下游（方法1）
 * (1) 确保下面配置被注释，采用默认配置，自动注册保持在默认的开启状态
 *     # spring.cloud.nacos.discovery.register-enabled: false
 * (2) 确保DeprecatedSyncLoader, StartupAsyncLoader, StartupSyncLoader 的 @Component注解被注释掉，防止它们也注册Nacos，造成干扰
 * (3) 把afterPropertiesSet方法代码的注释取消，让它在初始化阶段调用下游微服务
 * (4) 把@Qualifier(RestConfig.EARLY_BALANCER_INJECTED)的注释取消，改成注入普通的@LoadBalanced RestTemplate Bean
 *
 * 这个阶段，默认的RestTemplate还没有被注入负载均衡你能力，因此实现了一个提前注入负载均衡能力的RestTemplate名为"early-balancer-injected"
 * 需要注意，注入方法要与所用版本的Spring Cloud LoadBalancer保持相同，详见RestConfig类
 */
@Component
@Slf4j
public class UserService implements InitializingBean {
    @Autowired
    @Qualifier(RestConfig.EARLY_BALANCER_INJECTED)
    private RestTemplate restTemplate;

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    public UserService() {
    }

    public Result<?> getOrderByUserId(@RequestParam("userId") String userId) {
        // 方式1：restTemplate调用,url写死
        // String url = "http://localhost:8060/order/getOrder?userId="+userId;

        // 方式2： 利用负载均衡器获取tlmall-order服务列表
        // ServiceInstance serviceInstance = loadBalancerClient.choose("tlmall-order");
        // String url = String.format("http://%s:%s/order/getOrder?userId=%s",serviceInstance.getHost(),serviceInstance.getPort(),userId);
        // log.info(serviceInstance.getHost()+":"+serviceInstance.getPort());

        //方式3：利用@LoadBalanced，restTemplate需要添加@LoadBalanced注解
        String url = "http://tlmall-order/order/getOrder?userId="+userId;
        return restTemplate.getForObject(url,Result.class);
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
    @Override
    public void afterPropertiesSet() throws Exception {
//        log.info("自定义RestTemplate在afterPropertiesSet()期间调用下游服务");
//        log.info("初始化需要大约30秒时间"); // 模拟慢调用，以便观测它是否会过早注册到Nacos接收请求
//        Thread.sleep(30000);
//        String url = "http://tlmall-order/order/getOrder?userId=fox";
//        String result = restTemplate.getForObject(url, String.class);
//        log.info("自定义RestTemplate远程服务调用成功: " + result);
    }
}
