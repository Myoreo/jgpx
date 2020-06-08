package com.aoping.jiguangpx.base;

public class PxException extends RuntimeException {
	private static final long serialVersionUID = -8242052276733518041L;

	private int code;
	private String message;

	public PxException() {
		new PxException(ResultCode.FAIL);
	}

	public PxException(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public PxException(ResultCode code) {
		this.code = code.getCode();
		this.message = code.getMsg();
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	@Override
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
