package com.aoping.jiguangpx.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class HttpUtil {

	private static final Pattern PAGE_URL_PATTERN = Pattern.compile("^/?page\\d*/.*");

	public static String readPostBody(HttpServletRequest inv) {
		String body = "";
		try {
			BufferedReader bf = new BufferedReader(new InputStreamReader(inv.getInputStream()));
			String line = "";
			StringBuilder sBuilder = new StringBuilder();
			while ((line = bf.readLine()) != null) {
				sBuilder.append(line).append("\n");
			}
			body = sBuilder.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return body;
	}

	public static String urlEncode(String url) {
		try {
			return URLEncoder.encode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return StringUtils.EMPTY;
		}
	}

	public static String urlDecode(String encodedUrl) {
		try {
			return URLDecoder.decode(encodedUrl, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return StringUtils.EMPTY;
		}
	}

	/**
	 * 请求页面还是接口
	 *
	 * @param inv
	 * @return
	 */
	public static boolean isPageRequest(HttpServletRequest inv) {
		HttpServletRequest req = inv;
		String uri = req.getRequestURI();
		return PAGE_URL_PATTERN.matcher(uri).matches();
	}

	/**
	 * 将QueryString查询参数转换成Map结构。 未考虑数组的情况, 如?k1=1&k1=2,则取最后一个值,即k1=2
	 *
	 * @param req
	 * @return
	 */
	public static Map<String, String> getQueryParameterMap(HttpServletRequest req) {
		return getStringStringMap(req.getQueryString());
	}

	public static Map<String, String> getStringStringMap(String reqStr) {
		Map<String, String> map = new HashMap<>();
		String[] parts = StringUtils.isBlank(reqStr) ? ArrayUtils.EMPTY_STRING_ARRAY : reqStr.split("&");
		for (String part : parts) {
			String[] paramPair = part.split("=");
			if (paramPair.length != 2) {
				continue;
			}
			String key = paramPair[0];
			String value = paramPair[1];
			map.put(key, value);
		}
		return map;
	}

	/**
	 * 把键值对转换为queryString, 如: k1=v1&k2=v2
	 *
	 * @param params
	 * @return
	 */
	public static String toQueryString(Map<String, String> params) {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
		}
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	/**
	 * 设置一个cookie
	 *
	 * @param inv
	 * @param key
	 * @param value
	 */
	public static void setCookie(HttpServletResponse inv, String key, String value, String domain) {
		setCookie(inv, key, value, null, domain);
	}

	/**
	 * 设置cookie
	 *
	 * @param inv
	 * @param key
	 * @param value
	 * @param maxAge cookie存活期
	 */
	public static void setCookie(HttpServletResponse inv, String key, String value, Integer maxAge, String domain) {
		Cookie cookie = new Cookie(key, value);
		cookie.setPath("/");
		if (domain != null) {
			cookie.setDomain(domain);
		}
		if (maxAge != null) {
			cookie.setMaxAge(maxAge);
		}
		inv.addCookie(cookie);
	}

	/**
	 * 获取cookie值
	 *
	 * @param inv
	 * @param key
	 * @return
	 */
	public static String getCookie(HttpServletRequest request, String key) {
		Cookie[] cookies = request.getCookies();
		String value = StringUtils.EMPTY;
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(key)) {
					value = cookie.getValue();
				}
			}
		}
		return value;
	}

	public static String getHeader(HttpServletRequest request, String key) {
		Enumeration<String> headers = request.getHeaderNames();
		while (headers.hasMoreElements()) {
			String headerName = headers.nextElement();
			if (headerName.equals(key)) {
				return request.getHeader(headerName);
			}
		}
		return StringUtils.EMPTY;
	}

	public static String urlPure(String img) {
		if (StringUtils.isBlank(img)) {
			return null;
		} else {
			return img.replace("https:", "");
		}
	}
}
