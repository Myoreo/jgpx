package com.aoping.jiguangpx.service;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



/**
 * Created by aoping on Apr 15, 2020. 
 * Email: aoping.xu@plusx.cn Copyright(c)
 * 2014 承影互联(科技)有限公司 版权所有
 */
@Service
public class IdGenerator {
	// ==============================Fields===========================================
	/** 开始时间截 (2020-01-01) */
	private final long twepoch = 1577808000000L;

	/** 机器id所占的位数 */
	private final long workerIdBits = 14L;

	/** 支持的最大机器id (这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数) */
	private final long maxWorkerId = -1L ^ (-1L << workerIdBits);

	/** 序列在id中占的位数 */
	private final long sequenceBits = 8L;

	/** 机器ID向左移8位 */
	private final long workerIdShift = sequenceBits;

	/** 时间截向左移22位(14+8) */
	private final long timestampLeftShift = sequenceBits + workerIdBits;

	/** 生成序列的掩码 255(0b11111111=0xfff) */
	private final long sequenceMask = -1L ^ (-1L << sequenceBits);
	
	/** 工作机器ID(0~16383) */
	private long workerId;

	/** 毫秒内序列(0~255) */
	private long sequence = 0L;

	/** 上次生成ID的时间截 */
	private long lastTimestamp = -1L;
	
	// ==============================Constructors=====================================
	@Autowired
	public IdGenerator() {
		this(0);
	}

	/**
	 * 构造函数
	 * 
	 * @param workerId     工作ID (0~31)
	 * @param datacenterId 数据中心ID (0~31)
	 */
	public IdGenerator(long workerId) {
		if (workerId > maxWorkerId || workerId < 0) {
			throw new IllegalArgumentException(
					String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
		}
		this.workerId = workerId;
	}

	// ==============================Methods==========================================
	/**
	 * 获得下一个ID (该方法是线程安全的)
	 * 
	 * @return SnowflakeId
	 */
	public synchronized long nextId() {
		long timestamp = timeGen();

		// 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
		if (timestamp < lastTimestamp) {
			throw new RuntimeException(String.format(
					"Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
		}

		// 如果是同一时间生成的，则进行毫秒内序列
		if (lastTimestamp == timestamp) {
			sequence = (sequence + 1) & sequenceMask;
			// 毫秒内序列溢出
			if (sequence == 0) {
				// 阻塞到下一个毫秒,获得新的时间戳
				timestamp = tilNextMillis(lastTimestamp);
			}
		}
		// 时间戳改变，毫秒内序列重置，从0-10随机一个数开始
		else {
			sequence = new Random().nextInt(10);
		}

		// 上次生成ID的时间截
		lastTimestamp = timestamp;

		// 移位并通过或运算拼到一起组成64位的ID
		return ((timestamp - twepoch) << timestampLeftShift) //
				| (workerId << workerIdShift) //
				| sequence;
	}

	/**
	 * 阻塞到下一个毫秒，直到获得新的时间戳
	 * 
	 * @param lastTimestamp 上次生成ID的时间截
	 * @return 当前时间戳
	 */
	protected long tilNextMillis(long lastTimestamp) {
		long timestamp = timeGen();
		while (timestamp <= lastTimestamp) {
			timestamp = timeGen();
		}
		return timestamp;
	}

	/**
	 * 返回以毫秒为单位的当前时间
	 * 
	 * @return 当前时间(毫秒)
	 */
	protected long timeGen() {
		return System.currentTimeMillis();
	}

}
