package com.temmytechie.ecommerce.stockms.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author TemmyTechie
 */
@Getter
@Setter
public class PaymentEvent {

    private String type;

    private CustomerOrder order;
}
