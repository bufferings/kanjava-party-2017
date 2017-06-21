package com.example.guest.dao;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ConfigAutowireable
@Dao
public interface GuestProductViewDao {

  @Select
  List<GuestProductView> selectAll();

  @Select
  GuestProductView selectById(String productId);

}
