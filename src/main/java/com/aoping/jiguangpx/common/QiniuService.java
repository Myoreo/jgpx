package com.aoping.jiguangpx.common;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aoping.jiguangpx.util.HttpClientUtils;
import com.aoping.jiguangpx.util.StringAddUtils;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.processing.OperationManager;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.FetchRet;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;
import com.qiniu.util.Hex;
import com.qiniu.util.StringMap;
import com.qiniu.util.UrlSafeBase64;

@Service
public class QiniuService {
	private final static Logger logger = LoggerFactory.getLogger(QiniuService.class);

	@Value("${qiniu_access_key:}")
	private String ACCESS_KEY = "dDG9UTHKWzVZmKC9vNq6tU7yWyNmh18BNTofjAC3";

	@Value("${qiniu_secret_key:}")
	private String SECRET_KEY = "o_ZbFwOLzxXCoC_UcQ5SSL0ajeLohwHAmr2HstEZ";

	@Value("${qiniu_bucket_name:}")
	private String BUCKET_NAME = "35mm";

	@Value("${qiniu_encrypt_key:}")
	private String ENCRYPT_KEY;

	private static String DOWNLOAD_HOST = "pic.yun2photo.com";

	private static Auth QINIU_AUTH = null;
	private static OperationManager OPERATION_MANAGER = null;
	private static UploadManager UPLOAD_MANAGER = null;
	private static BucketManager BUCKET_MANAGER;
	private static Configuration c;

	private static final String[] QINIU_PIPELINE = { "" };

	private static final Random random = new Random();
//	private static final String QINIU_SLIM = "imageslim";

	@Value("${qiniu.notifyURL:}")
	private String notifyURL;

	public static void main(String[] args) {
		QiniuService qiniuService = new QiniuService();
		qiniuService.init();
		String key = "px/img/7/DSC_3332-HDR-01.jpeg";

		JSONObject json = qiniuService.getImageInfo(key);
		JSONObject exifInfo = qiniuService.getImageExif(key);
		System.err.println(json);
		System.err.println(exifInfo);
//		System.err.println(qiniuService.getSmallDownUrl(key));
	}
	
	@PostConstruct
	public void init() {
		Zone z = Zone.autoZone();
		c = new Configuration(z);
		// 密钥配置
		QINIU_AUTH = Auth.create(ACCESS_KEY, SECRET_KEY);
		OPERATION_MANAGER = new OperationManager(QINIU_AUTH, c);
		UPLOAD_MANAGER = new UploadManager(c);

		// 实例化一个BucketManager对象
		BUCKET_MANAGER = new BucketManager(QINIU_AUTH, c);
	}

	public String getUpToken() {
		return getUpToken(BUCKET_NAME);
	}
	
	public String getSmallDownUrl(String url) {
		return getDownUrl(url, "imageView2/0/w/1600/q/85", 24);
	}

	public String getUpToken(long expireSeconds) {
		return getUpToken(null, expireSeconds, BUCKET_NAME);
	}

	public String getUpToken(String bucketname) {
		return getUpToken(null, 3600, bucketname);
	}

	public String getUpToken(String picName, long expireSeconds, String bucketname) {

		StringMap policy = new StringMap();
		policy.put("name", "$(fname)");
		policy.put("size", "$(fsize)");
		policy.put("width", "$(imageInfo.width)");
		policy.put("height", "$(imageInfo.height)");
		policy.put("hash", "$(etag)");
		policy.put("colorModel", "$(imageInfo.colorModel)");
		StringBuilder sb = new StringBuilder();
		sb.append("{\n" 
				+ "      \"name\": $(fname),\n" 
				+ "      \"picSize\": $(fsize),\n"
				+ "      \"width\": $(imageInfo.width),\n" 
				+ "      \"height\": $(imageInfo.height),\n"
				+ "      \"hash\": $(etag),\n" 
				+ "      \"dateTimeOriginal\":$(exif.DateTimeOriginal.val),\n"
				+ "      \"key\":${key}\n" 
				+ "}");
		policy.put("returnBody", sb.toString());
		return QINIU_AUTH.uploadToken(bucketname, picName, expireSeconds, policy, true);
	}

	// 获取带时间戳防盗链的下载地址
	public String getDownUrl(String url) {
		return getDownUrl(url, null, 24);
	}

	public String getFreeDownUrl(String picName, String style) {
		return getFreeDownUrl(picName, style, 24);
	}

	public String getFreeDownUrl(String url, String style, int expireHours) {
		return getDownUrl(url, style, expireHours);
	}

	public String getDownUrl(String url, String style, int expireHours) {
		if (StringUtils.isBlank(url) || "null".equals(url.toLowerCase())) {
			return null;
		}
		if (StringUtils.isNotBlank(style)) {
			return getTimeStampUrl(url, style, expireHours);
		} else {
			return getTimeStampUrl(url, "", expireHours);
		}
	}

	public JSONObject getImageInfo(String picUrl) {
		return getImageInfo(picUrl, null);
	}

	public JSONObject getHash(String url) {
		url = getFreeDownUrl(url, "qhash/md5");
		String info = null;
		try {
			info = HttpClientUtils.doGet(url).getContent();
		} catch (Exception e) {
			logger.error("获取文件hash错误:", url, e);
			return null;
		}
		return JSONObject.parseObject(info);

	}

	public JSONObject getImageInfo(String picUrl, String style) {
		String url = getFreeDownUrl(picUrl, (StringUtils.isNotBlank(style) ? style + "|" : "") + "imageInfo");
		String info = null;
		try {
			info = HttpClientUtils.doGet(url).getContent();
		} catch (Exception e) {
			logger.error("获取imageInfo错误:url:{} {}", url, e);
			return null;
		}
		return JSONObject.parseObject(info);
	}

	public JSONObject getImageExif(String picUrl) {
		String url = getFreeDownUrl(picUrl, "exif");

		String info = null;
		try {
			info = HttpClientUtils.doGet(url).getContent();
		} catch (Exception e) {
			logger.error("获取ImageExif错误:url:{} {}", url, e);
			return null;
		}
		return JSONObject.parseObject(info);
	}
	
	public void del(String url) {
		try {
			BUCKET_MANAGER.delete(BUCKET_NAME, url);
		} catch (QiniuException e) {
			logger.error("删除文件失败:{}", e);
		}
	}

	public String savePic(String oldUrl, String imageView, String newUrl) {
		return savePic(oldUrl, imageView, newUrl, false);
	}

	public String savePic(String oldUrl, String imageView, String newUrl, boolean notify) {

		if (StringUtils.isBlank(oldUrl) || StringUtils.isBlank(newUrl)) {
			return null;
		}
		// 设置转码操作参数
		String fops = imageView;
		// 设置转码的队列
		String pipeline = QINIU_PIPELINE[random.nextInt(4)];
		// 可以对转码后的文件进行使用saveas参数自定义命名，当然也可以不指定文件会默认命名并保存在当前空间。
		String urlbase64 = UrlSafeBase64.encodeToString(BUCKET_NAME + ":" + newUrl);
		String pfops = fops + "|saveas/" + urlbase64;
		// 设置pipeline参数
		StringMap params = new StringMap().putWhen("force", 1, true).putNotEmpty("pipeline", pipeline);
		if (notify) {
			params.putNotEmpty("notifyURL", notifyURL);
		}
		String key = oldUrl;

		try {
			logger.debug(BUCKET_NAME);
			logger.debug(key);
			logger.debug(pfops);
			String id = OPERATION_MANAGER.pfop(BUCKET_NAME, key, pfops, params);
			JSONObject json = new JSONObject();
			json.put("key", key);
			json.put("pfops", pfops);
			json.put("pfop_id", id);
			logger.info("pfop_id: " + json.toString());
			return id;
		} catch (QiniuException e) {
			Response res = e.response;
			logger.info(res.toString());
			try {
				logger.info(res.bodyString());
			} catch (QiniuException e1) {
				e1.printStackTrace();
			}
		}
		return null;

	}

	public FetchRet fetchPic(String oldUrl, String newUrl) {

		if (StringUtils.isBlank(oldUrl) || StringUtils.isBlank(newUrl)) {
			return null;
		}
		try {
			// 调用fetch方法抓取文件
			FetchRet putRet = BUCKET_MANAGER.fetch(oldUrl, BUCKET_NAME, newUrl);
			return putRet;
		} catch (QiniuException e) {
			// 捕获异常信息
			Response r = e.response;
			System.out.println(r.toString());
		}
		return null;

	}

	public String saveZip(List<String> urlList, String zipUrl) {
		HashMap<List<String>, String> urlMap = new HashMap<>();
		urlMap.put(urlList, "");
		return saveZip(urlMap, zipUrl);

	}

	private String saveZip(HashMap<List<String>, String> urlMap, String zipUrl) {

		if (urlMap == null || urlMap.isEmpty()) {
			return null;
		}

		HashSet<String> picNameSet = new HashSet<>();

		StringBuilder uploadSb = new StringBuilder();
		for (Map.Entry<List<String>, String> entry : urlMap.entrySet()) {
			List<String> urlList = entry.getKey();
			String folderName = entry.getValue();
			for (String url : urlList) {
				String alias = url;
				if (url.contains("/")) {
					int index = url.lastIndexOf("/");
					alias = url.substring(index + 1);

				}

				// get postfix
				String postfix = StringAddUtils.getPostFix(alias);
				String prefix = StringUtils.isBlank(postfix) ? StringAddUtils.picNameClear(alias)
						: StringAddUtils.picNameClear(alias.replace("." + postfix, ""));

				if (StringUtils.isBlank(prefix)) {
					prefix = "noname";
				}
				String tempAlias = StringUtils.isBlank(postfix) ? prefix : (prefix + "." + postfix);
				int i = 1;
				while (picNameSet.contains(tempAlias)) {
					tempAlias = StringUtils.isBlank(postfix) ? (prefix + "_" + i) : (prefix + "_" + i + "." + postfix);
					i++;
				}
				alias = tempAlias;
				picNameSet.add(alias);

				logger.info(url);
				logger.info(alias);

				if (StringUtils.isNotBlank(folderName)) {
					alias = folderName + "/" + alias;
				}

				uploadSb.append("/url/").append(UrlSafeBase64.encodeToString(getDownUrl(url))).append("/alias/")
						.append(UrlSafeBase64.encodeToString(alias)).append("\n");
			}

		}
		long timestamp = (new Date()).getTime();

		String key = "test/upload/file/" + timestamp;
		System.out.println("uploadKey:" + uploadSb.toString());
		System.out.println("key:" + key);

		upload(uploadSb.toString().getBytes(), key);

		StringBuilder sb = new StringBuilder();
		sb.append("mkzip/4");
		sb.append("|saveas/").append(UrlSafeBase64.encodeToString(BUCKET_NAME + ":" + zipUrl));

		// 设置转码的队列
		String pipeline = QINIU_PIPELINE[random.nextInt(4)];
		// 设置pipeline参数
		StringMap params = new StringMap().putWhen("force", 1, true).putNotEmpty("pipeline", pipeline);
		try {
			logger.debug(BUCKET_NAME);
			logger.debug(key);
			logger.debug(sb.toString());
			String id = OPERATION_MANAGER.pfop(BUCKET_NAME, key, sb.toString(), params);
			logger.info("pfop_id: " + id);
			return id;
		} catch (QiniuException e) {
			Response res = e.response;
			logger.info(res.toString());
			try {
				logger.info(res.bodyString());
			} catch (QiniuException e1) {
				e1.printStackTrace();
			}
		}
		return null;
	}

	public int getPfopState(String id) {
		String purl = "http://api.qiniu.com/status/get/prefop?id=" + id;
		logger.info("purl:" + purl);
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", id);
		String result = "";
		try {
			result = HttpClientUtils.doGet("http://api.qiniu.com/status/get/prefop", params).getContent();
		} catch (Exception e) {
			logger.error("qiniu get pfopstate错误:{}", id, e);
			return -1;
		}

		logger.debug(result);
		JSONObject jsonResult = JSONObject.parseObject(result);
		if (jsonResult.containsKey("code")) {
			return Integer.parseInt(jsonResult.get("code").toString());
		}
		return -1;
	}

	public void upload(File file, String key) {
		try {
			// 调用put方法上传
			Response res = UPLOAD_MANAGER.put(file, key, getUpToken());
			// 打印返回的信息
			System.out.println(res.bodyString());
		} catch (QiniuException e) {
			Response r = e.response;
			// 请求失败时打印的异常的信息
			System.out.println(r.toString());
			try {
				// 响应的文本信息
				System.out.println(r.bodyString());
			} catch (QiniuException e1) {
				// ignore
			}
		}
	}

	public void upload(byte[] data, String key) {
		try {
			// 调用put方法上传
			Response res = UPLOAD_MANAGER.put(data, key, getUpToken());
			// 打印返回的信息
			System.out.println(res.bodyString());
		} catch (QiniuException e) {
			Response r = e.response;
			// 请求失败时打印的异常的信息
			System.out.println(r.toString());
			try {
				// 响应的文本信息
				System.out.println(r.bodyString());
			} catch (QiniuException e1) {
				// ignore
			}
		}
	}

	public static String putb64(String key, byte[] src) {

		String token = QINIU_AUTH.uploadToken("pluspub", key, 3600, null);

		try {
			// 文件大小
			// int l = (int)(new File(file).length());
			String url = "http://up.qiniu.com/putb64/" + src.length + "/key/" + UrlSafeBase64.encodeToString(key);
			// byte[] src = new byte[l];
			// //文件-输入流-字节数组-base64字符串
			// fis = new FileInputStream(new File(file));
			// fis.read(src);
			String file64 = com.qiniu.util.Base64.encodeToString(src, com.qiniu.util.Base64.DEFAULT);

			// 构造post对象
			HttpPost post = new HttpPost(url);
			post.addHeader("Content-Type", "application/octet-stream");
			post.addHeader("Authorization", "UpToken " + token);
			post.setEntity(new StringEntity(file64));

			// 请求与响应
			HttpClient c = HttpClientBuilder.create().build();
			HttpResponse res = c.execute(post);

			// 输出
			System.out.println(res.getStatusLine());
			String responseBody = EntityUtils.toString(res.getEntity(), "UTF-8");
			System.out.println(responseBody);

			return responseBody;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private String getTimeStampUrl(String url, String style, int duration) {

		if (StringUtils.isBlank(url)) {
			return null;
		}

		String picUrl = null;
		String picName = null;
		String signedUrl = null;

		try {
			if (url.contains("/")) {
				int index = url.lastIndexOf("/");
				picUrl = url.substring(0, index + 1);
				picName = url.substring(index + 1);
			} else {
				picUrl = "";
				picName = url;
			}

			// 考虑到文件名称会有中文，所以需要做urlencode
			String encodedFileKey = URLEncoder.encode(picName, "utf-8");
			String urlToSign = String.format("http://" + DOWNLOAD_HOST + "/" + picUrl + "%s", encodedFileKey);

			if (StringUtils.isBlank(ENCRYPT_KEY)) {
				// 未开启时间戳防盗链
				signedUrl = urlToSign;
				if (StringUtils.isNotBlank(style)) {
					signedUrl = signedUrl + "?" + style;
				}
			} else {
				signedUrl = getAntiLeechAccessUrlBasedOnTimestamp(urlToSign, ENCRYPT_KEY, duration * 60 * 60);

				if (StringUtils.isNotBlank(style)) {
					signedUrl = signedUrl.replace("?", "?" + style + "&");
				}
			}
//            signedUrl += "&" + typeDesc;

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return signedUrl;
	}

	/**
	 * 生成资源基于CDN时间戳防盗链的访问外链
	 *
	 * @param url        资源原始外链
	 * @param encryptKey 结果资源的有效期，单位秒
	 * @throws MalformedURLException
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchAlgorithmException
	 */

	private static String getAntiLeechAccessUrlBasedOnTimestamp(String url, String encryptKey, int durationInSeconds)
			throws MalformedURLException, UnsupportedEncodingException, NoSuchAlgorithmException {
		URL urlObj = new URL(url);
		String path = urlObj.getPath();

		long timestampNow = System.currentTimeMillis() / 1000 + durationInSeconds;
		String expireHex = Long.toHexString(timestampNow);

		String toSignStr = String.format("%s%s%s", encryptKey, path, expireHex);
		String signedStr = md5ToLower(toSignStr);

		String signedUrl = null;
		if (urlObj.getQuery() != null) {
			signedUrl = String.format("%s&sign=%s&t=%s", url, signedStr, expireHex);
		} else {
			signedUrl = String.format("%s?sign=%s&t=%s", url, signedStr, expireHex);
		}

		return signedUrl;
	}

	private static String md5ToLower(String src) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("MD5");
		digest.update(src.getBytes("utf-8"));
		byte[] md5Bytes = digest.digest();
		return Hex.encodeHexString(md5Bytes);
	}

	public JSONObject getVideoInfo(String videoURL) {
		String url = getFreeDownUrl(videoURL, "avinfo");
		String info = null;
		try {
			info = HttpClientUtils.doGet(url).getContent();
		} catch (Exception e) {
			logger.error("获取videoInfo错误,url:{} {}", videoURL, e);
			return null;
		}
		return JSONObject.parseObject(info);
	}

	// 获取视频流的基本信息
	public JSONObject getVideoStreamInfo(String videoUrl) {
		try {
			JSONObject videoInfo = getVideoInfo(videoUrl);
			JSONArray array = videoInfo.getJSONArray("streams");
			for (int i = 0; i < array.size(); i++) {
				JSONObject json = array.getJSONObject(i);
				if ("video".equals(json.getString("codec_type"))) {
					return json;
				}
			}
		} catch (Exception e) {
			logger.error("获取视频元信息错误,{}", e);
		}
		return null;
	}

	/**
	 * 截取视频的第一帧作为封面图
	 * 
	 * @param videoUrl 视频链接
	 */
	public String createCover(String videoUrl, JSONObject videoStreamInfo) {
		int width = 1200;
		int height = 600;
		try {
			if (videoStreamInfo == null) {
				videoStreamInfo = getVideoStreamInfo(videoUrl);
			}
			width = Integer.parseInt(videoStreamInfo.getString("width"));
			height = Integer.parseInt(videoStreamInfo.getString("height"));
		} catch (Exception e) {
			logger.error("获取视频宽高错误 {}", e);
		}
		String coverPath = videoUrl.substring(0, videoUrl.lastIndexOf(".")) + ".jpg";
		savePic(videoUrl, String.format("vframe/jpg/offset/0/w/%s/h/%s", width, height), coverPath);
		return coverPath;
	}

	public FileInfo getFileInfo(String bucketName, String key) {
		try {
			return BUCKET_MANAGER.stat(BUCKET_NAME, key);
		} catch (QiniuException e) {
			logger.error("获取文件信息错误,key:{} {}", key, e);
		}
		return null;
	}

}
