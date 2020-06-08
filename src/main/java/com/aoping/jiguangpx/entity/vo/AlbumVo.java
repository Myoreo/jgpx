package com.aoping.jiguangpx.entity.vo;

import java.util.Date;

import com.aoping.jiguangpx.entity.album.Album;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class AlbumVo {

	@ApiModelProperty("id")
	private int id;
	@ApiModelProperty("相册名")
	private String name;
	@ApiModelProperty("相册封面")
	private String cover;
	@ApiModelProperty("创建时间")
	private Date createTime;
	@ApiModelProperty("accountId")
	private int accountId;

	public AlbumVo() {
	}

	public AlbumVo(Album album) {
		this.id = album.getId();
		this.name = album.getName();
		this.cover = album.getCover();
		this.createTime = album.getCreateTime();
		this.accountId = album.getAccountId();
	}
}
