package com.proyect.orders.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proyect.orders.config.RabbitConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventPublisher {
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    public OrderEventPublisher(RabbitTemplate template) {
        this.rabbitTemplate = template;
    }

    public void publishOrderCreated(Object payload) {
        try {
            String json = mapper.writeValueAsString(payload);
            rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.ROUTING_KEY, json);
        } catch (Exception e) {
            // log y reintentos si deseas
            e.printStackTrace();
        }
    }
}
