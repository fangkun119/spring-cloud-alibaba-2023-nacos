package org.nacosdemo.tlmalluser.controller;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.nacosdemo.tlmalluser.config.RestConfig;
import org.nacosdemo.tlmalluser.config.loadbalancer.RandomLoadBalancerConfig;
import org.nacosdemo.tlmalluser.service.UserService;
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
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/getOrder")
    public Result<?> getOrderByUserId(@RequestParam("userId") String userId) {
        log.info("根据userId:"+userId+"查询订单信息");
        return userService.getOrderByUserId(userId);
    }
}