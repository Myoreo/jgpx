package com.aoping.jiguangpx.entity.album;

import java.util.Date;

import lombok.Data;

@Data
public class AlbumPic implements Cloneable {

	private long id;

	private String url;
	private int albumId;
	private int accountId;
	private String picName;
	private String picHash;

	// 0-jpg等 1-raw
	private int type;
	private int hot;

	// 图片描述
	private String note;

	private int size;
	private int width;
	private int height;

	private String uniqueCode;

	// 原图id
	private Long jpgId;

	// 曝光时间
	private String exposureTime;
	// 光圈值
	private Float fNumber;
	// 感光度
	private Integer iso;
	// 拍摄时间
	private Date shootTime;
	// 焦距
	private Integer focalLen;
	// 相机型号
	private String cameraModel;
	// 镜头型号
	private String lensModel;

	private int status;
	private Date createTime;
	private Date updateTime;

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}
