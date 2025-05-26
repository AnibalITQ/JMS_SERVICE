package com.itq.notification.producer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal para el arranque del servicio Notification Producer.
 * Este servicio expone una API REST para recibir notificaciones y las envía
 * a una cola JMS configurada en ActiveMQ.
 */
@SpringBootApplication
public class NotificationProducerApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationProducerApplication.class, args);
    }
}