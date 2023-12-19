package com.temmytechie.ecommerce.orderms.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.temmytechie.ecommerce.orderms.entity.Order;
import com.temmytechie.ecommerce.orderms.entity.OrderRepository;
import com.temmytechie.ecommerce.orderms.model.OrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author TemmyTechie
 */
@Component
public class ReverseOrder {

    @Autowired
    private OrderRepository repository;

    final Logger log = LoggerFactory.getLogger(ReverseOrder.class);

    @KafkaListener(topics = "reversed-orders", groupId = "orders-group")
    public void reverseOrder(String event) {

        log.info("reverse order for order {}", event);

        try {
            OrderEvent orderEvent = new ObjectMapper().readValue(event, OrderEvent.class);

            Optional<Order> order = repository.findById(orderEvent.getOrder().getOrderId());

            order.ifPresent(o -> {
                o.setStatus("FAILED");
                this.repository.save(o);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
