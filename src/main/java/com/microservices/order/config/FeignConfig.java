package com.microservices.order.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                // On récupère le header Authorization de la requête React -> Order
                String authorizationHeader = attributes.getRequest().getHeader("Authorization");
                
                // On le transmet à la requête Order -> Product
                if (authorizationHeader != null && !authorizationHeader.isEmpty()) {
                    requestTemplate.header("Authorization", authorizationHeader);
                }
            }
        };
    }
}