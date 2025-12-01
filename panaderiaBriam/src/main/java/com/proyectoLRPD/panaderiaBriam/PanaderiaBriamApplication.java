package com.proyectoLRPD.panaderiaBriam;

import jakarta.annotation.PostConstruct; // <--- IMPORTANTE
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone; // <--- IMPORTANTE

@SpringBootApplication
public class PanaderiaBriamApplication {

    public static void main(String[] args) {
        SpringApplication.run(PanaderiaBriamApplication.class, args);
    }

    // ESTE ES EL CÓDIGO QUE ARREGLA LA HORA
    @PostConstruct
    public void init() {
        // Configuramos la hora a Perú (America/Lima)
        TimeZone.setDefault(TimeZone.getTimeZone("America/Lima"));
        System.out.println("✅ ZONA HORARIA CONFIGURADA: " + new java.util.Date());
    }
}