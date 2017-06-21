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
public interface StaffOrderViewDao {

  @Insert
  int insert(StaffOrderView staffOrderView);

  @Update
  int update(StaffOrderView staffOrderView);

  @Select
  StaffOrderView selectById(String orderId);

  @Select
  List<StaffOrderView> selectForStaff();

  @Delete
  int delete(StaffOrderView staffOrderView);

  @Delete(sqlFile = true)
  int deleteByGroupId(String orderGroupId);

}
