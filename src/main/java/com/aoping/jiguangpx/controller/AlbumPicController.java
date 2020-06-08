package com.aoping.jiguangpx.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aoping.jiguangpx.base.Result;
import com.aoping.jiguangpx.base.ResultUtil;
import com.aoping.jiguangpx.config.annotation.AlbumPermissonCheck;
import com.aoping.jiguangpx.entity.album.AlbumPic;
import com.aoping.jiguangpx.service.AlbumPicService;
import com.aoping.jiguangpx.util.StringAddUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/pic")
@Api(tags = "图片管理")
public class AlbumPicController {

	@Autowired
	private AlbumPicService albumPicService;

	@PostMapping("/save")
	@AlbumPermissonCheck
	@ApiOperation("上传图片")
	public Result<?> save(@ApiParam(name = "url", value = "url") @RequestParam("url") String url,
			@ApiParam(name = "picName", value = "文件名") @RequestParam("picName") String picName,
			@ApiParam(name = "picHash", value = "图片hash值") @RequestParam(value = "picHash") String picHash,
			@ApiParam(name = "albumId", value = "相册id") @RequestParam(value = "albumId") int albumId,
			@ApiParam(name = "size", value = "文件大小") @RequestParam(value = "size", defaultValue = "0") int size,
			@ApiParam(name = "width", value = "宽") @RequestParam(value = "width", defaultValue = "0") int width,
			@ApiParam(name = "height", value = "高") @RequestParam(value = "height", defaultValue = "0") int height) {
		AlbumPic pic = new AlbumPic();
		pic.setAlbumId(albumId);
		pic.setUrl(StringAddUtils.pureImgUrl(url));
		pic.setPicHash(picHash);
		pic.setSize(size);
		pic.setWidth(width);
		pic.setHeight(height);
		pic.setPicName(picName);
		albumPicService.save(pic);
		return ResultUtil.success();
	}

	@GetMapping("/list")
	@ApiOperation("图片列表")
	@AlbumPermissonCheck
	public Result<?> list(@ApiParam(name = "albumId", value = "相册id") @RequestParam(value = "albumId") int albumId,
			@ApiParam(name = "page", value = "页数") @RequestParam(value = "page", defaultValue = "1") int page,
			@ApiParam(name = "size", value = "每页数量") @RequestParam(value = "size", defaultValue = "20") int size,
			@ApiParam(name = "sortType", value = "0-上传时间倒序 1-拍摄时间倒序") @RequestParam(value = "sortType", defaultValue = "0") int sortType) {
		page = page <= 0 ? 1 : page;
		size = size <= 0 ? 20 : size;
		return ResultUtil.success(albumPicService.getList(albumId, sortType, page, size));
	}

}
