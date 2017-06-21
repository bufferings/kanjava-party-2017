package com.example.staff.dao;

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
public interface StaffOrderItemDao {

  @Insert
  int insert(StaffOrderItem staffOrderItem);

  @Update
  int update(StaffOrderItem staffOrderItem);

  @Select
  StaffOrderItem selectByOrderItemId(String orderItemId);

  @Select
  List<StaffOrderItem> selectForStaff();

  @Delete(sqlFile = true)
  int deleteByOrderItemId(String orderItemId);

  @Delete(sqlFile = true)
  int deleteByOrderGuestId(Integer orderGuestId);

}
