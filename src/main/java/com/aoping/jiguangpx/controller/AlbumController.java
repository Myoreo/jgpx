package com.aoping.jiguangpx.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aoping.jiguangpx.base.Result;
import com.aoping.jiguangpx.base.ResultUtil;
import com.aoping.jiguangpx.entity.vo.AlbumVo;
import com.aoping.jiguangpx.service.AlbumService;
import com.aoping.jiguangpx.util.StringAddUtils;
import com.github.pagehelper.PageInfo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/album")
@Api(tags = "相册管理")
public class AlbumController {

	@Autowired
	private AlbumService albumService;

	@SuppressWarnings("unchecked")
	@GetMapping("/list")
	public Result<PageInfo<AlbumVo>> list(
			@ApiParam(name = "page", value = "页码") @RequestParam(value = "page", defaultValue = "1") int page,
			@ApiParam(name = "size", value = "每页数量") @RequestParam(value = "size", defaultValue = "10") int size) {
		page = page <= 0 ? 1 : page;
		size = size <= 0 ? 10 : size;
		return ResultUtil.success(albumService.getList(page, size));
	}

	@PostMapping("edit")
	@ApiOperation("编辑/新增相册")
	public Result<?> edit(@ApiParam(name = "id", value = "id，0为新增") @RequestParam("id") int id,
			@ApiParam(name = "name", value = "相册名") @RequestParam("name") String name,
			@ApiParam(name = "cover", value = "相册封面") @RequestParam(value = "cover", defaultValue = "") String cover) {
		cover = StringAddUtils.pureImgUrl(cover);
		return ResultUtil.success(albumService.edit(id, name, cover));
	}

}
