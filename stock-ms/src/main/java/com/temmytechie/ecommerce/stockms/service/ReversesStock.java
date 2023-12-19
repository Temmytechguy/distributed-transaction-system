package com.temmytechie.ecommerce.stockms.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.temmytechie.ecommerce.stockms.repository.StockRepository;
import com.temmytechie.ecommerce.stockms.entity.WareHouse;
import com.temmytechie.ecommerce.stockms.model.DeliveryEvent;
import com.temmytechie.ecommerce.stockms.model.PaymentEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author TemmyTechie
 */
@RestController
public class ReversesStock {

    @Autowired
    private StockRepository repository;

    @Autowired
    private KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    @KafkaListener(topics = "reversed-stock", groupId = "stock-group")
    public void reverseStock(String event) {
        System.out.println("Inside reverse stock for order "+event);

        try {
            DeliveryEvent deliveryEvent = new ObjectMapper().readValue(event, DeliveryEvent.class);

            Iterable<WareHouse> inv = this.repository.findByItem(deliveryEvent.getOrder().getItem());

            inv.forEach(i -> {
                i.setQuantity(i.getQuantity() + deliveryEvent.getOrder().getQuantity());
                repository.save(i);
            });

            PaymentEvent paymentEvent = new PaymentEvent();
            paymentEvent.setOrder(deliveryEvent.getOrder());
            paymentEvent.setType("PAYMENT_REVERSED");
            kafkaTemplate.send("reversed-payments", paymentEvent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
