package com.aoping.jiguangpx.util;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.lang3.StringUtils;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringAddUtils {

	private static final Pattern URL_PATTERN = Pattern.compile("^.*(yun2photo)\\.*(com|cn)\\/|\\?.*$");
	private static final Pattern POSTFIX_PATTERN = Pattern.compile(".*\\.([a-zA-z0-9]+)");
	private static final Pattern URL_NAME_PATTERN = Pattern.compile(".*\\/([a-zA-z0-9.]+)$");
	private static final Pattern NUMBER_PATTERN = Pattern.compile("[0-9]*");
	private static final Pattern NUMBER_CLEAR_PATTERN = Pattern.compile("[^(0-9)]");
	private static final Pattern CHART_CLEAR_PATTERN = Pattern.compile("[^(A-Za-z)]");
	private static final Pattern CHINESE_CLEAR_PATTERN = Pattern.compile("[^(\\u4e00-\\u9fa5)]");
	private static final Pattern CHINESE_CHART_NUMBER_CLEAR_PATTERN = Pattern.compile("[^(a-zA-Z0-9\\u4e00-\\u9fa5)]");
	private static final Pattern PIC_NAME_CLEAR_PATTERN = Pattern.compile("[^(A-Za-z0-9)|_|-]");
	private static final Pattern PIC_SUFFIX_CLEAR_PATTERN = Pattern
			.compile("(bmp|dib|jfif|jpeg|jpg|png|tif|tiff|ico|gif|mp4|mov)$");
	private static final Pattern WEI_VISIT_PASSWORD = Pattern.compile("^[0-9a-zA-Z]{8}$");

	private static Pattern CHINESE_PATTERN = Pattern.compile("[\u4e00-\u9fa5]");// 中文正则

	private static final URLCodec URL_ENCODER = new URLCodec("UTF-8");
	
	public static String removeChinese(String fileName) {
		String suffix = getPostFix(fileName);
		String prefix = fileName.substring(0, fileName.indexOf(suffix) - 1);

		Matcher mat = CHINESE_PATTERN.matcher(prefix);
		prefix = mat.replaceAll("");
		if (prefix.length() == 0) {
			prefix = RandomString(10);
		}
		return prefix + "." + suffix;
	}

	/**
	 * 产生一个随机的字符串
	 */
	public static String RandomString(int length) {
		String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		Random random = new Random();
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int num = random.nextInt(62);
			buf.append(str.charAt(num));
		}
		return buf.toString();
	}

	/**
	 * 产生一个随机的字符串
	 */
	public static String RandomNumber(int length) {
		String str = "0123456789";
		Random random = new Random();
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int num = random.nextInt(10);
			buf.append(str.charAt(num));
		}
		return buf.toString();
	}

	// 生成笑脸手机号
	public static String mosaicPhone(String phone) {
		if (phone.length() >= 7) {
			char[] charNum = phone.toCharArray();
			charNum[3] = 42;
			charNum[4] = 94;
			charNum[5] = 95;
			charNum[6] = 94;
			charNum[7] = 42;
			return String.valueOf(charNum);
		}
		return "";
	}

	// 清洗七牛的url，获取pure的url
	public static String pureImgUrl(String url) {
		if (StringUtils.isBlank(url)) {
			return null;
		}
		try {
			String decodeUrl = URL_ENCODER.decode(url);
			Matcher matcher1 = URL_PATTERN.matcher(decodeUrl);
			url = matcher1.replaceAll("");
			int index = url.lastIndexOf("-");
			if (index > 0) {
				url = url.substring(0, index);
			}
		} catch (DecoderException e) {
			e.printStackTrace();
		}
		return url;
	}

	// 获取文件后缀
	public static String getPostFix(String url) {
		if (StringUtils.isBlank(url)) {
			return null;
		}
		try {
			String decodeUrl = URL_ENCODER.decode(url);
			Matcher matcher1 = POSTFIX_PATTERN.matcher(decodeUrl);
			if (matcher1.find()) {
				return matcher1.group(1);
			} else {
				return null;
			}
		} catch (DecoderException e) {
			e.printStackTrace();
		}
		return null;
	}

	// 获取文件名称
	public static String getFileName(String url) {
		if (StringUtils.isBlank(url)) {
			return null;
		}
		try {
			String decodeUrl = URL_ENCODER.decode(url);
			Matcher matcher1 = URL_NAME_PATTERN.matcher(decodeUrl);
			if (matcher1.find()) {
				return matcher1.group(1);
			} else {
				return null;
			}
		} catch (DecoderException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean isNumeric(String str) {
		Matcher isNum = NUMBER_PATTERN.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

	public static String picNameClear(String picName) {
		Matcher matcher1 = PIC_NAME_CLEAR_PATTERN.matcher(picName);
		return matcher1.replaceAll("").replaceAll("[(|)]", "");
	}

	public static String numberClear(String input) {
		Matcher matcher1 = NUMBER_CLEAR_PATTERN.matcher(input);
		return matcher1.replaceAll("").replaceAll("[(|)]", "");
	}

	public static String chartrClear(String input) {
		Matcher matcher1 = CHART_CLEAR_PATTERN.matcher(input);
		return matcher1.replaceAll("").replaceAll("[(|)]", "");
	}

	public static String chineseClear(String input) {
		Matcher matcher1 = CHINESE_CLEAR_PATTERN.matcher(input);
		return matcher1.replaceAll("").replaceAll("[(|)]", "");
	}

	public static String ChineseChartNumberClear(String input) {
		Matcher matcher1 = CHINESE_CHART_NUMBER_CLEAR_PATTERN.matcher(input);
		return matcher1.replaceAll("").replaceAll("[(|)]", "");
	}

	public static String Chinese2Pinyin(String input) {
		Matcher matcher1 = CHINESE_CHART_NUMBER_CLEAR_PATTERN.matcher(input);
		return matcher1.replaceAll("").replaceAll("[(|)]", "");
	}

	public static boolean checkPhoneNum(String mobiles) {
		Pattern p = Pattern.compile("^1\\d{10}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();

	}

	/**
	 * 验证固话号码
	 *
	 * @param telephone
	 * @return
	 */
	public static boolean checkTelephone(String telephone) {
		String regex = "^(0\\d{2}-\\d{8}(-\\d{1,4})?)|(0\\d{3}-\\d{7,8}(-\\d{1,4})?)$";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(telephone);
		return m.matches();
	}

	public static boolean checkNumber(String number) {
		Matcher matcher = NUMBER_PATTERN.matcher(number);
		return matcher.matches();
	}

	public static boolean checkEmail(String email) {
		Pattern p = Pattern.compile("^[A-Za-z0-9\\u4e00-\\u9fa5/.]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$");
		Matcher m = p.matcher(email);
		return m.matches();
	}

	public static boolean checkPicSuffix(String picSuffix) {
		Matcher m = PIC_SUFFIX_CLEAR_PATTERN.matcher(picSuffix.toLowerCase());
		return m.matches();
	}

	public static boolean checkWeiVisitpassword(String picSuffix) {
		Matcher m = WEI_VISIT_PASSWORD.matcher(picSuffix);
		return m.matches();
	}

	/**
	 * uri名添加后缀
	 * 
	 * @param url
	 * @param suffix
	 * @return
	 */
	public static String appendSuffix(String url, String suffix) {
		String postfix = StringAddUtils.getPostFix(url);
		return url.substring(0, url.indexOf(postfix) - 1) + suffix + "." + postfix;
	}
}
