package com.temmytechie.ecommerce.paymentms.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author TemmyTechie
 */
@Getter
@Setter
@ToString
public class PaymentEvent {

    private String type;

    private CustomerOrder order;
}
