package com.patitas.medicamento;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsMedicamentoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsMedicamentoApplication.class, args);
    }
}
