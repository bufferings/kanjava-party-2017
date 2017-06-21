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
public interface GuestOrderViewDao {

  @Insert
  int insert(GuestOrderView guestOrderView);

  @Update
  int update(GuestOrderView guestOrderView);

  @Select
  GuestOrderView selectById(String orderId);

  @Select
  List<GuestOrderView> selectByGuestId(Integer orderGuestId);

  @Delete(sqlFile = true)
  int deleteByGroupId(String orderGroupId);

}