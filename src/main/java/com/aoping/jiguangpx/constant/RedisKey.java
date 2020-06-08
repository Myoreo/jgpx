package com.aoping.jiguangpx.constant;

public class RedisKey {

	public static class Account {

		public static String ticket2Account(String ticket) {
			return "jgpx:account:ticket2account:" + ticket;
		}
	}

}
