package org.nacosdemo.tlmalluserconfigdemo.feign;

import feign.Param;
import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springcloudmvp.tlmallcommon.Result;


@FeignClient(value = "tlmall-order-config-demo",path = "/order")
public interface OrderFeignService {


    /**
     * 根据用户id查询订单信息
     * @param userId
     * @return
     */
    @GetMapping("/getOrder")
    Result<?> getOrder(@RequestParam("userId") String userId);



}