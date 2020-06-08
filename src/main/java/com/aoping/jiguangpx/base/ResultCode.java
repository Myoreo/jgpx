package com.aoping.jiguangpx.base;

public enum ResultCode {

	SUCCESS(1, "ok"), 
	FAIL(-1, "fail"), 
	NOT_LOGIN(1001, "未登录"),

	ALBUM_NO_PERMISSION(3001, "没有操作该相册的权限"),
	ALBUM_NOT_EXIST(3002,"相册不存在"),
	
	PIC_NOT_FOUND(4001, "图片不存在，检查链接是否正确"),
	PIC_CONTENT_TYPE_UN_KNOW(4002, "图片格式不正确");
	private int code;
	private String msg;

	ResultCode(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

}
