package org.nacosdemo.tlmallorder.controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.nacosdemo.tlmallorder.dto.OrderDTO;
import org.nacosdemo.tlmallorder.service.OrderService;
import org.springcloudmvp.tlmallcommon.BusinessException;
import org.springcloudmvp.tlmallcommon.Result;

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
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 根据用户id查询订单信息
     * @param userId
     * @return
     */
    @RequestMapping("/getOrder")
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

    @RequestMapping("/getOrderById/{id}")
    public Result<?> getOrderById(@PathVariable("id") Integer id){

        Result<?> res = null;
        try {
            res = orderService.getOrderById(id);
        }
        catch (BusinessException e) {
            return Result.failed(e.getMessage());
        }
        return res;
    }



    /**
     * 模拟测试openFegin的接口方法规范
     * @param orderDTO
     * @return
     */
    @PostMapping("/post1")
    public Result<?>  post1(@RequestBody OrderDTO orderDTO){
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

    @GetMapping("/testgateway")
    public String testGateway(HttpServletRequest request) throws Exception {
        log.info("gateWay获取请求头X-Request-color："
                +request.getHeader("X-Request-color"));
        return "success";
    }
    @GetMapping("/testgateway2")
    public String testGateway(@RequestHeader("X-Request-color") String color) throws Exception {
        log.info("gateWay获取请求头X-Request-color："+color);
        return "success";
    }

    @GetMapping("/testgateway3")
    public String testGateway3(@RequestParam("color") String color) throws Exception {
        log.info("gateWay获取请求参数color:"+color);
        return "success";
    }



}
