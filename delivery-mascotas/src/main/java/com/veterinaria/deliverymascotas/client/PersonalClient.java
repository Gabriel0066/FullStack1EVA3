package com.veterinaria.deliverymascotas.client;

import com.veterinaria.deliverymascotas.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Cliente OpenFeign que llama al servicio personal-medico para validar existencia de personal
@FeignClient(name = "personal-medico-client", url = "http://localhost:8081/api/v1/personal", configuration = FeignClientConfig.class)
public interface PersonalClient {

    @GetMapping("/exists/id/{id}")
    // Método proxy Feign para la llamada remota al endpoint de personal
    boolean existsById(@PathVariable("id") Long id);
}