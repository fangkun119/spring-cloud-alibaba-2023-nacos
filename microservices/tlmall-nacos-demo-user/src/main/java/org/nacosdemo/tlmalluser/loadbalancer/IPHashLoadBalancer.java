package org.nacosdemo.tlmalluser.loadbalancer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.loadbalancer.core.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;


public class IPHashLoadBalancer implements ReactorServiceInstanceLoadBalancer {

    private static final Log log = LogFactory.getLog(IPHashLoadBalancer.class);

    private static final String X_CLIENT_IP = "X-Client-IP";

    private final String serviceId;

    private final ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;

    public IPHashLoadBalancer(
            ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider,
            String serviceId) {
        this.serviceId = serviceId;
        this.serviceInstanceListSupplierProvider = serviceInstanceListSupplierProvider;
    }

    @Override
    public Mono<Response<ServiceInstance>> choose(Request loadBalancerAdaptorRequest) {
        // 获取supplier
        ServiceInstanceListSupplier supplier = serviceInstanceListSupplierProvider
                .getIfAvailable(NoopServiceInstanceListSupplier::new);

        // 获取Client IP，这里仅供演示如何实现自定义LoadBalancer
        // 可靠且符合Reactor的做法是
        // 注入一个上下文传递Bean，用Reactor Context把WebFilter写入的IP传递到这里并取出
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        String ipAddress = extractIP(attributes);
        log.info("X-CLIENT-IP：" + ipAddress);

        // 返回Mono
        return supplier.get(loadBalancerAdaptorRequest).next()
                .map(serviceInstances -> processInstanceResponse(
                        supplier, serviceInstances, ipAddress));
    }

    private static String extractIP(RequestAttributes attributes) {
        //  检查
        if (Objects.isNull(attributes)) {
            log.warn("ServletRequestAttributes is null");
            return null;
        }
        if (! (attributes instanceof ServletRequestAttributes)) {
            log.warn("only available of servlet request");
            return null;
        }
        // 提取ServletRequestAttributes
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) attributes;
        // 方法1：从HTTP Header “X-CLIENT-IP”提取IP，方便演示
        return servletRequestAttributes.getRequest().getHeader(X_CLIENT_IP);
        // 方法2：返回客户端IP，实际应用还需要考虑网关转发等情况，从请求头中找到转发前的原始IP
        // return servletRequestAttributes.getRequest().getRemoteAddr();
    }

    private Response<ServiceInstance> processInstanceResponse(
            ServiceInstanceListSupplier supplier,
            List<ServiceInstance> serviceInstances,
            String ipAddress) {
        Response<ServiceInstance> serviceInstanceResponse = getInstanceResponse(serviceInstances, ipAddress);
        if (supplier instanceof SelectedInstanceCallback && serviceInstanceResponse.hasServer()) {
            ((SelectedInstanceCallback) supplier).selectedServiceInstance(serviceInstanceResponse.getServer());
        }
        return serviceInstanceResponse;
    }

    private Response<ServiceInstance> getInstanceResponse(
            List<ServiceInstance> instances, String ipAddress) {
        if (instances.isEmpty()) {
            // 候选实例列表为空的情况
            if (log.isWarnEnabled()) {
                log.warn("No servers available for service: " + this.serviceId);
            }
            return new EmptyResponse();
        } else if (Objects.nonNull(ipAddress) && !ipAddress.isEmpty()) {
            // 根据IP Hash负载均衡选择实例
            int hash = ipAddress.hashCode();
            int index = Math.abs(hash) % instances.size(); // 用Math.abs防止负数取模得到负索引
            ServiceInstance instance = instances.get(index);
            return new DefaultResponse(instance);
        } else {
            // 没有IP，随机选择实例
            // 注意：实际应用中需要考虑更多影响
            // - 假如只是资源复用等考虑希望发到同一台instance上，这样不失为一个方法，避免把大量请求集中发给同一台机器
            // - 假如同一个用户的请求必须发给同一台Instance，那么这个约束就不太合理（例如机器重启添加实例等都会打破约束），要提高整个系统的鲁棒性
            int index = ThreadLocalRandom.current().nextInt(instances.size());
            ServiceInstance instance = instances.get(index);
            return new DefaultResponse(instance);
        }
    }
}

