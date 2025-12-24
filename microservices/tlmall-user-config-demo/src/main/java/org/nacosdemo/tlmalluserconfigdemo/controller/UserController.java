package org.nacosdemo.tlmalluserconfigdemo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springcloudmvp.tlmallcommon.Result;
import org.nacosdemo.tlmalluserconfigdemo.feign.OrderFeignService;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController{

    @Autowired
    private OrderFeignService orderService;

    @RequestMapping(value = "/getOrder")
    public Result<?> getOrderByUserId(@RequestParam("userId") String userId) {
        log.info("根据userId:"+userId+"查询订单信息");

        //使用openFeign调用订单服务
        Result result = orderService.getOrder(userId);

        return result;
    }





}