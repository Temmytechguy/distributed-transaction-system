package com.temmytechie.ecommerce.paymentms.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author TemmyTechie
 */

@Getter
@Setter
public class OrderEvent {

    private String type;

    private CustomerOrder order;

}
