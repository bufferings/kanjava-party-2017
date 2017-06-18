package com.example.api.view;

import java.util.List;

import org.seasar.doma.Dao;
import org.seasar.doma.Delete;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.Update;
import org.seasar.doma.boot.ConfigAutowireable;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ConfigAutowireable
@Dao
public interface OrderGuestViewDao {

  @Insert
  int insert(OrderGuestView orderGuestView);

  @Update
  int update(OrderGuestView orderGuestView);

  @Select
  OrderGuestView selectById(String orderId);

  @Select
  List<OrderGuestView> selectByTableNumber(Integer tableNumber);

  @Delete(sqlFile = true)
  int deleteByGroupId(String orderGroupId);

}
