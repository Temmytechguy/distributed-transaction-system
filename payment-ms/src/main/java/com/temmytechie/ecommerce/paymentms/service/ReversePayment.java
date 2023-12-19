package com.temmytechie.ecommerce.paymentms.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.temmytechie.ecommerce.paymentms.entity.Payment;
import com.temmytechie.ecommerce.paymentms.entity.PaymentRepository;
import com.temmytechie.ecommerce.paymentms.model.CustomerOrder;
import com.temmytechie.ecommerce.paymentms.model.OrderEvent;
import com.temmytechie.ecommerce.paymentms.model.PaymentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author TemmyTechie
 */
@Component
public class ReversePayment {

    @Autowired
    private PaymentRepository repository;

    @Autowired
    private KafkaTemplate<String, OrderEvent> kafkaTemplate;

    final Logger log = LoggerFactory.getLogger(ReversePayment.class);

    @KafkaListener(topics = "reversed-payments", groupId = "payments-group")
    public void reverseOrder(String event) {

        log.info("Inside reverse order for order {}", event);

        try {
            PaymentEvent paymentEvent = new ObjectMapper().readValue(event, PaymentEvent.class);

            CustomerOrder order = paymentEvent.getOrder();

            Iterable<Payment> payments = this.repository.findByOrderId(order.getOrderId());

           payments.forEach(p -> {
               p.setStatus("FAILED");
               repository.save(p);
           });

            OrderEvent orderEvent = new OrderEvent();
            orderEvent.setOrder(paymentEvent.getOrder());
            orderEvent.setType("ORDER_REVERSED");
            kafkaTemplate.send("reversed-orders", orderEvent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
