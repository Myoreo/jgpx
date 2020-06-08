package com.aoping.jiguangpx.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.aoping.jiguangpx.base.PxException;
import com.aoping.jiguangpx.base.ResultCode;
import com.aoping.jiguangpx.common.QiniuService;
import com.aoping.jiguangpx.config.auth.SecurityUtil;
import com.aoping.jiguangpx.dao.AlbumPicDao;
import com.aoping.jiguangpx.entity.album.AlbumPic;
import com.aoping.jiguangpx.entity.vo.AlbumPicVo;
import com.aoping.jiguangpx.entity.vo.AlbumVo;
import com.aoping.jiguangpx.util.ExifResolveUtil;
import com.aoping.jiguangpx.util.HttpClientUtils;
import com.aoping.jiguangpx.util.StringAddUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AlbumPicService {

	@Autowired
	private IdGenerator idGenerator;

	@Autowired
	private QiniuService qiniuService;

	@Autowired
	private AlbumPicDao albumPicDao;

//	private static List<String> RAW_SUFFIX = Arrays.asList("DNG", "CR2", "ARW", "NEF");

	private static List<String> JPG_FORMAT = Arrays.asList("png", "jpeg");

	private static List<String> IMAGE_CONTENT_TYPE = Arrays.asList("image/png", "image/jpeg");

	private static List<String> IMAGE_RAW_CONTENT_TYPE = Arrays.asList("image/x-nikon-nef", "image/x-canon-cr2",
			"image/tiff");

//	private static List<String> CAMERA_MAKE = Arrays.asList("NIKON CORPORATION", "Canon", "SONY");

	private static ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 10, 0, TimeUnit.SECONDS,
			new LinkedBlockingQueue<>(100), new ThreadPoolExecutor.CallerRunsPolicy());

	public void save(AlbumPic pic) {
		pic.setId(idGenerator.nextId());
		pic.setAccountId(SecurityUtil.getAccountId());

		pic.setUniqueCode(pic.getPicName());
		pic.setPicHash(pic.getAlbumId() + "_" + pic.getPicHash());
		AlbumPic exist = albumPicDao.getByPicHash(pic.getPicHash());
		if (exist != null) {
			albumPicDao.updateStatus(exist.getId(), 5);
			return;
		}
		pic.setPicName(StringAddUtils.removeChinese(pic.getPicName()));
		JSONObject iamgeInfo = qiniuService.getImageInfo(pic.getUrl());
		if (iamgeInfo == null) {
			throw new PxException(ResultCode.PIC_NOT_FOUND);
		}

		// 判断图片类型
		String format = iamgeInfo.getString("format");
		if (JPG_FORMAT.contains(format)) {
			// jpg\png
			pic.setType(0);
		} else {
			String contentType = HttpClientUtils.doHead(qiniuService.getDownUrl(pic.getUrl()), "content-type");
			if (IMAGE_CONTENT_TYPE.contains(contentType)) {
				pic.setType(0);
			} else if (IMAGE_RAW_CONTENT_TYPE.contains(contentType)) {
				pic.setType(1);
			} else {
				qiniuService.del(pic.getUrl());
				throw new PxException(ResultCode.PIC_CONTENT_TYPE_UN_KNOW);
			}
		}

		if (pic.getSize() == 0) {
			// 图片基本信息
			if (iamgeInfo != null) {
				pic.setSize(iamgeInfo.getIntValue("size") / 1024);
				pic.setWidth(iamgeInfo.getIntValue("width"));
				pic.setHeight(iamgeInfo.getIntValue("height"));

			}
		}
		boolean result = albumPicDao.insert(pic) > 0;
		if (result) {
			executor.execute(new ExifResolveThread(pic));
		}

	}

	private class ExifResolveThread extends Thread {
		private AlbumPic pic;

		ExifResolveThread(AlbumPic pic) {
			try {
				this.pic = (AlbumPic) pic.clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			try {
				if (pic.getType() == 0) {
					long start = System.currentTimeMillis();
					Map<String, String> metaMap = ExifResolveUtil.resolve(qiniuService.getDownUrl(pic.getUrl()));
					if (metaMap.isEmpty()) {
						log.info("pic:{} exif not exist", pic.getUrl());
						return;
					}

					pic.setFocalLen(ExifResolveUtil.focalLen(metaMap.get("Focal Length 35")));
					if (pic.getFocalLen() == null) {
						pic.setFocalLen(ExifResolveUtil.focalLen(metaMap.get("Focal Length")));
					}
					pic.setFNumber(ExifResolveUtil.fNumber(metaMap.get("F-Number")));
					pic.setExposureTime(ExifResolveUtil.exposureTime(metaMap.get("Exposure Time")));
					pic.setShootTime(ExifResolveUtil.shootTime(metaMap.get("Date/Time Original")));
					pic.setIso(NumberUtils.toInt(metaMap.get("ISO Speed Ratings"), 0));
					String model = metaMap.get("Model");
					pic.setCameraModel(model);
					pic.setLensModel(metaMap.get("Lens Model"));
					if (StringUtils.isBlank(pic.getLensModel())) {
						pic.setLensModel(metaMap.get("Lens"));
					}
					albumPicDao.updateExif(pic);
					long end = System.currentTimeMillis();
					log.info("解析图片exif cost time:{} ms", end - start);
				}
			} catch (Exception e) {
				log.error("解析图片exif信息错误 {}", e);
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public PageInfo getList(int albumId, int sortType, int page, int size) {
		PageHelper.startPage(page, size);
		List<AlbumPicVo> pics = new ArrayList<>();
		List<AlbumPic> list = albumPicDao.getList(albumId, sortType);
		for (AlbumPic albumPic : list) {
			AlbumPicVo pic = new AlbumPicVo(albumPic);
			pic.setUrl(qiniuService.getDownUrl(albumPic.getUrl()));
			pic.setSmallUrl(qiniuService.getSmallDownUrl(albumPic.getUrl()));
			pics.add(pic);
		}
		PageInfo pageInfo = new PageInfo<>(list);
		pageInfo.setList(pics);
		return pageInfo;
	}
}
