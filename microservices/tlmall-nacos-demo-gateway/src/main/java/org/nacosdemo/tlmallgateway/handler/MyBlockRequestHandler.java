package org.nacosdemo.tlmallgateway.handler;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springcloudmvp.tlmallcommon.Result;
import reactor.core.publisher.Mono;


import static org.springframework.web.reactive.function.BodyInserters.fromObject;

/**
 * @author: Fox
 * @Desc:
 **/

public class MyBlockRequestHandler implements BlockRequestHandler {

    @Override
    public Mono<ServerResponse> handleRequest(ServerWebExchange exchange, Throwable t) {

        //返回json数据;
        return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(fromObject(buildErrorResult(t)));
    }

    private Result buildErrorResult(Throwable ex) {

        if (ex instanceof ParamFlowException) {
            return Result.failed("请求被限流了");
        }
        return Result.failed("系统繁忙");
    }


}