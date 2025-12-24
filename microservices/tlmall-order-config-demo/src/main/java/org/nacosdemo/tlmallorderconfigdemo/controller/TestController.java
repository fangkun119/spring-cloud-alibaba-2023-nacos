package org.nacosdemo.tlmallorderconfigdemo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope  //动态感知修改后的值
public class TestController implements ApplicationListener<RefreshScopeRefreshedEvent> {

    @Value("${order.count}")
     String count;

    @GetMapping("/count")
    public String hello() {
        return "count:"+count;
    }

    //触发@RefreshScope执行逻辑会导致@Scheduled定时任务失效
    @Scheduled(cron = "*/3 * * * * ?")  //定时任务每隔3s执行一次
    public void execute() {
        System.out.println("定时任务正常执行。。。。。。");
    }

    /**
     * 实现Spring事件监听器，监听 RefreshScopeRefreshedEvent事件，监听方法中进行一次定时方法的调用
     * 解决@RefreshScope 导致@Scheduled定时任务失效问题
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(RefreshScopeRefreshedEvent event) {
        this.execute();
    }
}
