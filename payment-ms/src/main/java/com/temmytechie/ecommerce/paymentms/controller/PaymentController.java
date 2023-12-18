package com.temmytechie.ecommerce.paymentms.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
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

import org.springframework.stereotype.Controller;

/**
 * @author TemmyTechie
 */

@Controller
public class PaymentController {

    @Autowired
    private PaymentRepository repository;

    @Autowired
    private KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    @Autowired
    private KafkaTemplate<String, OrderEvent> kafkaOrderTemplate;

    private final Logger log = LoggerFactory.getLogger(PaymentController.class);


    @KafkaListener(topics = "new-orders", groupId = "orders-group")
    public void processPayment(String event) throws JsonProcessingException {
        log.info("Received event for payment {}", event);
        OrderEvent orderEvent = new ObjectMapper().readValue(event, OrderEvent.class);

        CustomerOrder order = orderEvent.getOrder();

		Payment payment = new Payment();
        try{
            payment.setAmount(order.getAmount());
            payment.setMode(order.getPaymentMode());
            payment.setOrderId(order.getOrderId());
            payment.setStatus("SUCCESS");
            repository.save(payment);

            PaymentEvent paymentEvent = new PaymentEvent();
            paymentEvent.setOrder(orderEvent.getOrder());
            paymentEvent.setType("PAYMENT_CREATED");

            kafkaTemplate.send("new-payments", paymentEvent);
        }
        catch (Exception e)
        {
            payment.setOrderId(order.getOrderId());
            payment.setStatus("FAILED");
            repository.save(payment);

            OrderEvent oe = new OrderEvent();
            oe.setOrder(order);
            oe.setType("ORDERED_REVERSED");
            kafkaOrderTemplate.send("reversed-orders", orderEvent);

        }
    }
}
