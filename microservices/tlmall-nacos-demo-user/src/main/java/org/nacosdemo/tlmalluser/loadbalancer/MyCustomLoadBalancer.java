package org.nacosdemo.tlmalluser.loadbalancer;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.loadbalancer.core.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import reactor.core.publisher.Mono;

import java.util.List;


public class MyCustomLoadBalancer implements ReactorServiceInstanceLoadBalancer {

    private static final Log log = LogFactory.getLog(MyCustomLoadBalancer.class);
    private final String serviceId;
    private ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;

    public MyCustomLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider, String serviceId) {
        this.serviceId = serviceId;
        this.serviceInstanceListSupplierProvider = serviceInstanceListSupplierProvider;
    }


    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        ServiceInstanceListSupplier supplier = serviceInstanceListSupplierProvider
                .getIfAvailable(NoopServiceInstanceListSupplier::new);
        return supplier.get(request).next()
                .map(serviceInstances -> processInstanceResponse(supplier, serviceInstances));
    }

    private Response<ServiceInstance> processInstanceResponse(ServiceInstanceListSupplier supplier, List<ServiceInstance> serviceInstances) {
        Response<ServiceInstance> serviceInstanceResponse = getInstanceResponse(serviceInstances);
        if (supplier instanceof SelectedInstanceCallback && serviceInstanceResponse.hasServer()) {
            ((SelectedInstanceCallback) supplier).selectedServiceInstance(serviceInstanceResponse.getServer());
        }
        return serviceInstanceResponse;
    }

    private Response<ServiceInstance> getInstanceResponse(List<ServiceInstance> instances) {
        if (instances.isEmpty()) {
            if (log.isWarnEnabled()) {
                log.warn("No servers available for service: " + this.serviceId);
            }
            return new EmptyResponse();
        } else {
            // 自定义负载均衡策略
            
            // 获取 Request 对象
            ServletRequestAttributes attributes = (ServletRequestAttributes)
                    RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            String ipAddress = request.getRemoteAddr();
            log.info("用户 IP：" + ipAddress);
            int hash = ipAddress.hashCode();
            // IP 哈希负载均衡算法【关键代码】
            int index = hash % instances.size();
            // 得到服务实例的方法
            ServiceInstance instance = instances.get(index);
            return new DefaultResponse(instance);
        }
    }


}
