package com.aoping.jiguangpx.entity.vo;

import java.util.Date;

import com.aoping.jiguangpx.entity.album.AlbumPic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("图片")
public class AlbumPicVo {
	private long id;
	@ApiModelProperty("小图")
	private String smallUrl;
	@ApiModelProperty("原图")
	private String url;
	private int albumId;

	private String picName;

	// 0-jpg等 1-raw
	private int type;
	private int hot;

	// 图片描述
	private String note;

	private int size;
	private int width;
	private int height;

	@ApiModelProperty("曝光时间")
	private String exposureTime;
	@ApiModelProperty("光圈值")
	private String fNumber;
	@ApiModelProperty("感光度")
	private Integer iso;
	@ApiModelProperty("拍摄时间")
	private Date shootTime;
	@ApiModelProperty("焦距")
	private String focalLen;
	@ApiModelProperty("相机型号")
	private String cameraModel;
	@ApiModelProperty("镜头型号")
	private String lensModel;

	private Date createTime;

	public AlbumPicVo() {
	}

	public AlbumPicVo(AlbumPic pic) {
		this.id = pic.getId();
		this.picName = pic.getPicName();
		this.albumId = pic.getAlbumId();
		this.type = pic.getType();
		this.hot = pic.getHot();
		this.note = pic.getNote();
		this.size = pic.getSize();
		this.width = pic.getWidth();
		this.height = pic.getHeight();
		this.exposureTime = pic.getExposureTime();
		this.shootTime = pic.getShootTime();
		this.fNumber = pic.getFNumber() == null ? "" : "f/" + pic.getFNumber();
		this.iso = pic.getIso();
		this.focalLen = pic.getFocalLen() == null ? "" : pic.getFocalLen() + " mm";
		this.cameraModel = pic.getCameraModel();
		this.lensModel = pic.getLensModel();
		this.createTime = pic.getCreateTime();
	}

}
