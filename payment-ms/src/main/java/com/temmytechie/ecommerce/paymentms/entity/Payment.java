package com.temmytechie.ecommerce.paymentms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

/**
 * @author TemmyTechie
 */
@Getter
@Setter
@Entity
public class Payment {
    @Id
    @GeneratedValue
    private long id;

    @Column
    private String mode;

    @Column
    private Long orderId;

    @Column
    private double amount;

    @Column
    private String status;
}
