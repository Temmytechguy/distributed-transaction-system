package com.temmytechie.ecommerce.paymentms.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.temmytechie.ecommerce.paymentms.entity.Payment;
import com.temmytechie.ecommerce.paymentms.entity.PaymentRepository;
import com.temmytechie.ecommerce.paymentms.model.PaymentEvent;
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
public class ReversePayment {

    @Autowired
    private PaymentRepository repository;

    final Logger log = LoggerFactory.getLogger(ReversePayment.class);

    @KafkaListener(topics = "reversed-payments", groupId = "payments-group")
    public void reverseOrder(String event) {

        log.info("Inside reverse order for order {}", event);

        try {
            PaymentEvent orderEvent = new ObjectMapper().readValue(event, PaymentEvent.class);

            Optional<Payment> order = repository.findById(orderEvent.getOrder().getOrderId());

            order.ifPresent(o -> {
                o.setStatus("FAILED");
                this.repository.save(o);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
