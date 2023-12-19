package com.temmytechie.ecommerce.stockms.repository;

import com.temmytechie.ecommerce.stockms.entity.WareHouse;
import org.springframework.data.repository.CrudRepository;

/**
 * @author TemmyTechie
 */

public interface StockRepository extends CrudRepository<WareHouse, Long>{

 Iterable<WareHouse> findByItem(String item);

}
