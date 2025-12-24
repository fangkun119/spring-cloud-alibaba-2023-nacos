package org.nacosdemo.tlmalluseropenfeigndemo.feign;

import feign.Param;
import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;
import org.springcloudmvp.tlmallcommon.Result;

//FeignConfig局部配置，让指定的微服务生效，在@FeignClient 注解中指定configuration
//@FeignClient(value = "tlmall-order",path = "/order",configuration = FeignConfig.class)
@FeignClient(value = "tlmall-order", path = "/order")
public interface OrderFeignService {


    /**
     * 根据用户id查询订单信息
     *
     * @param userId
     * @return
     */
//    @GetMapping("/getOrder")
//    Result<?> getOrder(@RequestParam("userId") String userId);
    @RequestLine("GET /getOrder?userId={userId}")
    Result<?> getOrder(@Param("userId") String userId);


//    @GetMapping(value = "/post1")
//    Result<?>  post1(@RequestBody OrderDTO orderDTO);
//
//
//    @PostMapping("/post2")
//    Result<?>  post2(@RequestBody OrderDTO orderDTO,@RequestParam("token") String token);
//
//
//    @PostMapping(value = "/post3/{userId}")
//    Result<?> post3(@RequestBody OrderDTO orderDTO, @PathVariable("userId") String userId);


}