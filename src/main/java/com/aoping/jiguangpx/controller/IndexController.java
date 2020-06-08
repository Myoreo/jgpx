package com.aoping.jiguangpx.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.aoping.jiguangpx.base.Result;
import com.aoping.jiguangpx.base.ResultUtil;
import com.aoping.jiguangpx.common.QiniuService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
public class IndexController {

	@Autowired
	private QiniuService qiniuService;

	@PostMapping("/login")
	@ApiOperation("登录")
	public Result<?> login(@ApiParam(name = "username", value = "用户名/手机号") @RequestParam("username") String name,
			@ApiParam(name = "password", value = "密码") @RequestParam(value = "password") String password) {
		return ResultUtil.success();
	}

	@GetMapping("/base/getUploadToken")
	@ApiOperation(value = "获取七牛Token")
	public Result<JSONObject> getUploadToken() {
		String token = qiniuService.getUpToken();
		JSONObject json = new JSONObject();
		json.put("upToken", token);
		return ResultUtil.success(json);
	}

	@ApiOperation(value = "获取图片url")
	@RequestMapping(value = "/base/getUrl", method = RequestMethod.GET)
	public Result<JSONObject> getUrl(
			@ApiParam(name = "url", value = "图片url", required = true) @RequestParam(value = "url") String url) {
		url = qiniuService.getDownUrl(url);

		JSONObject json = new JSONObject();
		json.put("url", url);
		return ResultUtil.success(json);
	}

}
