package com.aoping.jiguangpx.base;

/**
 * Created by aoping on May 23, 2020. Email: aoping.xu@plusx.cn Copyright(c)
 * 2014 承影互联(科技)有限公司 版权所有
 */
public class ResultUtil {
	
	public static Result<Object> toResult(int code, String msg) {
		Result<Object> result = new Result<Object>(code, msg);
		return result;
	}

	public static <T> Result<T> toResult(int code, String msg, T data) {
		Result<T> result = new Result<T>(code, msg);
		result.setData(data);
		return result;
	}

	public static <T> Result<T> success(T data) {
		Result<T> result = new Result<T>(ResultCode.SUCCESS);
		result.setData(data);
		return result;
	}

	public static Result<Object> success() {
		return Result.SUCCESS;
	}

	public static Result<Object> fail() {
		return Result.FAIL;
	}

	public static Result<Object> fail(String msg) {
		Result<Object> result = new Result<>(ResultCode.FAIL);
		result.setMsg(msg);
		return result;
	}
}
