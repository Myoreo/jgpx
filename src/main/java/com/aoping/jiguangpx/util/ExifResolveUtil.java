package com.aoping.jiguangpx.util;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

public class ExifResolveUtil {

	private static Pattern FOCALLEN_PATTERN = Pattern.compile("^([1-9]\\d*)\\D*mm$");

	private static Pattern FNumber_PATTERN = Pattern.compile("^[f]/([1-9][0-9]{0,2}\\.[0-9]{1,2})$");

	private static Pattern DECIMAL_PATTERN = Pattern.compile("^[0-9]{1,2}\\.[0-9]{1,2}$");

	public static Map<String, String> resolve(String link) throws Exception {
		URL url = new URL(link);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoInput(true);
		conn.connect();
		InputStream in = conn.getInputStream();
		Map<String, String> metaMap = new HashMap<>();
		Metadata metadata = ImageMetadataReader.readMetadata(in);
		// 输出所有附加属性数据
		for (Directory directory : metadata.getDirectories()) {
			for (Tag tag : directory.getTags()) {
				metaMap.put(tag.getTagName(), tag.getDescription());
			}
		}
		return metaMap;
	}

	public static Date shootTime(String time) {
		try {
			if (time == null || time.length() == 0) {
				return null;
			}
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
			return dateFormat.parse(time);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Integer focalLen(String value) {
		// 41 mm
		try {
			if (StringUtils.isBlank(value)) {
				return null;
			}
			Matcher matcher = FOCALLEN_PATTERN.matcher(value);
			if (matcher.find()) {
				return Integer.valueOf(matcher.group(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Float fNumber(String value) {
		// f/1.8
		try {
			if (StringUtils.isBlank(value)) {
				return null;
			}
			Matcher matcher = FNumber_PATTERN.matcher(value);
			if (matcher.find()) {
				return Float.valueOf(matcher.group(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String exposureTime(String value) {
		try {
			if (StringUtils.isBlank(value)) {
				return null;
			}
			value = value.replaceAll("\\D*sec", "");
			if (DECIMAL_PATTERN.matcher(value).matches()) {
				Float num = Float.valueOf(value);
				return "1/" + (int) (1000 / (num * 1000));
			}
			return value;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
