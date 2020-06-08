package com.aoping.jiguangpx.entity.album;

import java.util.Date;

import lombok.Data;

@Data
public class Album {
	private int id;
	private String name;
	private String cover;
	private int accountId;
	private int width;
	private int height;
	private int status;
	private Date createTime;
	private Date updateTime;

}
