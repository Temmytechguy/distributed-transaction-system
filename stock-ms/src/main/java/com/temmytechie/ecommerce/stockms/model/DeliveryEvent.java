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
public class DeliveryEvent {

    private String type;

    private CustomerOrder order;
}
