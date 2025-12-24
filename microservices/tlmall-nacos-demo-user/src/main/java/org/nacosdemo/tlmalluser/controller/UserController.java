package org.nacosdemo.tlmalluser.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springcloudmvp.tlmallcommon.Result;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController implements InitializingBean {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @RequestMapping(value = "/getOrder")
    public Result<?> getOrderByUserId(@RequestParam("userId") String userId) {
        log.info("根据userId:"+userId+"查询订单信息");
        // 方式1：restTemplate调用,url写死
        //String url = "http://localhost:8060/order/getOrder?userId="+userId;

        //方式2： 利用负载均衡器获取tlmall-order服务列表
//        ServiceInstance serviceInstance = loadBalancerClient.choose("tlmall-order");
//        String url = String.format("http://%s:%s/order/getOrder?userId=%s",serviceInstance.getHost(),serviceInstance.getPort(),userId);
//        log.info(serviceInstance.getHost()+":"+serviceInstance.getPort());

        //方式3：利用@LoadBalanced，restTemplate需要添加@LoadBalanced注解
        String url = "http://tlmall-order/order/getOrder?userId="+userId;

        Result result = restTemplate.getForObject(url,Result.class);

        return result;
    }


    /**
     * 用于分析@LoadBalanced底层实现
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        //TODO  用于验证 @LoadBalanced 修饰的RestTemplate 不能在 init方法中使用
        String url = "http://tlmall-order/order/getOrder?userId=fox";
        restTemplate.getForObject(url,Result.class);

    }
}