package com.aoping.jiguangpx.base;

import lombok.Data;

@Data
public class Result<T> {
	
	static Result<Object> SUCCESS = new Result<Object>(ResultCode.SUCCESS);
	static Result<Object> FAIL = new Result<Object>(ResultCode.FAIL);

	private int code;
	private String msg;
	private T data;

	Result(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}
	
	public Result(ResultCode resultCode) {
		this.code = resultCode.getCode();
		this.msg = resultCode.getMsg();
	}
	
}
