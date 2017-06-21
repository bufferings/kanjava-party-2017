package com.example.order.port.table.product;

import org.seasar.doma.Dao;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;

@ConfigAutowireable
@Dao
public interface ProductTableDao {

  @Update
  int update(ProductTable product);

  @Select
  ProductTable selectById(String productId);

}
