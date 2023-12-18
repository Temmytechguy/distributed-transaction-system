package com.temmytechie.ecommerce.deliveryms.entity;

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
public class Delivery {
    @Id
    @GeneratedValue
    private long id;

    @Column
    private String address;

    @Column
    private String status;

    @Column
    private Long orderId;


}
