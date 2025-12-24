package org.nacosdemo.tlmallgateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

//@Component
public class CheckAuthFilter implements GlobalFilter, Ordered {
    private static final Logger log = LoggerFactory.getLogger(CheckAuthFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst("token");
        if (null == token) {
            log.info("token is null");
            ServerHttpResponse response = exchange.getResponse();
            response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            byte[] bytes = HttpStatus.UNAUTHORIZED.getReasonPhrase().getBytes();
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } else {
            log.info("校验token");
            return chain.filter(exchange);
        }
    }

    @Override
    public int getOrder() {
        return 10;
    }
}