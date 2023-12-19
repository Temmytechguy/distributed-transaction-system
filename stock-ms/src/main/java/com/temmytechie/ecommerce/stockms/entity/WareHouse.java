package com.temmytechie.ecommerce.stockms.entity;

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
public class WareHouse {
    @Id
    @GeneratedValue
    private long id;

    @Column
    private int quantity;

    @Column
    private String item;



}
