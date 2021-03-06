package com.example.guest.dao;

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
public interface GuestOrderItemDao {

  @Insert
  int insert(GuestOrderItem guestOrderItem);

  @Update
  int update(GuestOrderItem guestOrderItem);

  @Select
  GuestOrderItem selectByOrderItemId(String orderItemId);

  @Select
  List<GuestOrderItem> selectByOrderGuestId(Integer orderGuestId);

  @Delete(sqlFile = true)
  int deleteByOrderGuestId(Integer orderGuestId);

}
