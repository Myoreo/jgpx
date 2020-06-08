package com.aoping.jiguangpx.entity;

import java.util.Date;

import lombok.Data;

@Data
public class UserAccount {

	private int id;
	private String phone;
	private String password;
	private Date createTime;
	private Date updateTime;

}
