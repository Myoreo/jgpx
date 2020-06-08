package com.aoping.jiguangpx.util;

import javax.servlet.http.HttpServletRequest;

public class RemoteIpUtil {


    private static final String ALLOWABLE_IP_REGEX = "(127[.]0[.]0[.]1)|" + "(localhost)|" +
            "(10[.]\\d{1,3}[.]\\d{1,3}[.]\\d{1,3})|" +
            "(172[.]((1[6-9])|(2\\d)|(3[01]))[.]\\d{1,3}[.]\\d{1,3})|" +
            "(192[.]168[.]\\d{1,3}[.]\\d{1,3})";

    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public static String getOutIp(HttpServletRequest request){
        String ipString = getIpAddr(request);
        String[] ips = ipString.split(",");
        for (int i = 0; i < ips.length; i++) {
            String ip = ips[i].trim();
            if(!RemoteIpUtil.isInnerNet(ip)){
                return ip;
            }
        }
        return  "127.0.0.1";
    }

    public static boolean isInnerNet(String ip){
        return ip.matches(ALLOWABLE_IP_REGEX);
    }


    public  static void main(String[] argus){
        System.out.println("220.181.171.65".matches(ALLOWABLE_IP_REGEX));
        System.out.println("123.151.139.156".matches(ALLOWABLE_IP_REGEX));
        System.out.println("172.16.1.1".matches(ALLOWABLE_IP_REGEX));
    }

}
