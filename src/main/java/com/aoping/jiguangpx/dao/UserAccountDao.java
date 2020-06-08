package com.aoping.jiguangpx.dao;

import org.apache.ibatis.annotations.Param;

import com.aoping.jiguangpx.entity.UserAccount;

public interface UserAccountDao {
	
	UserAccount getByPhone(String phone);

	int updatePasswordByphone(@Param("password")String password,@Param("phone") String phone);
}
