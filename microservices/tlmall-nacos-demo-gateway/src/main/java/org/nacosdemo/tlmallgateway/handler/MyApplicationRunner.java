package org.nacosdemo.tlmallgateway.handler;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @author: Fox
 * @Desc:
 **/
@Component
public class MyApplicationRunner implements ApplicationRunner {


    @Override
    public void run(ApplicationArguments args) throws Exception {
        GatewayCallbackManager.setBlockHandler(new MyBlockRequestHandler());
    }
}