package com.veterinaria.deliverymascotas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
// habilita la detección y creación de clientes Feign en este servicio
@EnableFeignClients
public class DeliveryMascotasApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeliveryMascotasApplication.class, args);
    }

}
