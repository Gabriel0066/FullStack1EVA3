package com.veterinaria.deliverymascotas.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Base64;

@Configuration
public class FeignClientConfig {

    @Bean
    public RequestInterceptor basicAuthRequestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                // Añade autenticación básica a todas las requests de Feign
                String auth = "admin:admin123";
                String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
                template.header("Authorization", "Basic " + encodedAuth);
            }
        };
    }
}
