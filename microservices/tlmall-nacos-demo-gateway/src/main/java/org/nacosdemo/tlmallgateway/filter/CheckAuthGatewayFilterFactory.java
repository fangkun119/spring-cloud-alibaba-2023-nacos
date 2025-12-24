package org.nacosdemo.tlmallgateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractNameValueGatewayFilterFactory;

//@Component
public class CheckAuthGatewayFilterFactory extends AbstractNameValueGatewayFilterFactory {
    private static final Logger log = LoggerFactory.getLogger(CheckAuthGatewayFilterFactory.class);

    @Override
    public GatewayFilter apply(AbstractNameValueGatewayFilterFactory.NameValueConfig config) {
        return (exchange, chain) -> {
            log.info("调用CheckAuthGatewayFilterFactory===" + config.getName() + ":" + config.getValue());
            return chain.filter(exchange);
        };
    }
}