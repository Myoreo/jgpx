package com.aoping.jiguangpx.util;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class NetUtil {
	
	/**
	 *  获取非环回地址
	 */
	public static InetAddress findFirstNonLoopbackAddress() {
		InetAddress result = null;
		try {
			int lowest = Integer.MAX_VALUE;
			for (Enumeration<NetworkInterface> nics = NetworkInterface.getNetworkInterfaces(); nics
					.hasMoreElements();) {
				NetworkInterface ifc = nics.nextElement();
				if (ifc.isUp()) {
					if (ifc.getIndex() < lowest || result == null) {
						lowest = ifc.getIndex();
					} else if (result != null) {
						continue;
					}

					for (Enumeration<InetAddress> addrs = ifc.getInetAddresses(); addrs.hasMoreElements();) {
						InetAddress address = addrs.nextElement();
						if (address instanceof Inet4Address && !address.isLoopbackAddress()) {
							result = address;
						}
					}
				}
			}
		} catch (IOException ex) {
		}

		if (result != null) {
			return result;
		}

		try {
			return InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			throw new RuntimeException("Unable to retrieve localhost");
		}
	}
}
