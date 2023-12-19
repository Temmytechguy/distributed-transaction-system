package com.temmytechie.ecommerce.stockms.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author TemmyTechie
 */

@Getter
@Setter
@ToString
public class CustomerOrder {

    private String item;
    private int quantity;
    private double amount;
    private String paymentMode;
    private long orderId;
    private String address;


}
