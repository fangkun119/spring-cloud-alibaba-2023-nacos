package org.nacosdemo.tlmallorderconfigdemo.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;
import org.springcloudmvp.tlmallcommon.BusinessException;
import org.springcloudmvp.tlmallcommon.Result;
import org.nacosdemo.tlmallorderconfigdemo.dto.OrderDTO;
import org.nacosdemo.tlmallorderconfigdemo.service.OrderService;


/**
 * 
 *
 * @author fox
 * @email 2763800211@qq.com
 * @date 2021-01-28 15:46:19
 */
@RestController
@RequestMapping("/order")
@Slf4j
@RefreshScope
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 根据用户id查询订单信息
     * @param userId
     * @return
     */
    @GetMapping("/getOrder")
    public Result<?> getOrder(@RequestParam("userId") String userId) {

        //模拟异常
        if(("foxxxx").equals(userId)){
            throw new IllegalArgumentException("非法参数异常");
        }

        //用于模拟调用超时
//        try {
//            Thread.sleep(6000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }

        log.info("根据userId:"+userId+"查询订单信息");
        Result<?> res = null;
        try {
            res = orderService.getOrderByUserId(userId);
        }
        catch (BusinessException e) {
            return Result.failed(e.getMessage());
        }
        return res;


    }



    @Value("${order.count}")
    private int count;

    /**
     * 模拟测试openFegin的接口方法规范
     * @param orderDTO
     * @return
     */
    @PostMapping("/post1")
    public Result<?>  post1(@RequestBody OrderDTO orderDTO){
        //设置数量
        orderDTO.setCount(count);

        return Result.success(orderDTO);

    }

    @PostMapping("/post2")
    public Result<?>  post2(@RequestBody OrderDTO orderDTO,@RequestParam("token") String token){
        log.info("token:"+token);
        return Result.success(orderDTO);
    }


    @PostMapping(value = "/post3/{userId}")
    public Result<?> post3(@RequestBody OrderDTO orderDTO, @PathVariable("userId") String userId) {
        return Result.success(orderDTO);
    }

}
