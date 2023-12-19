package com.temmytechie.ecommerce.paymentms.entity;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author TemmyTechie
 */

public interface PaymentRepository extends CrudRepository<Payment, Long>{

    public List<Payment> findByOrderId(Long orderId);

}
