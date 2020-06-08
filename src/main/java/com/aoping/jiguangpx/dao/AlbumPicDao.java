package com.aoping.jiguangpx.dao;

import java.util.List;

import com.aoping.jiguangpx.entity.album.AlbumPic;

import io.lettuce.core.dynamic.annotation.Param;

public interface AlbumPicDao {

	int insert(AlbumPic albumPic);

	int updateExif(AlbumPic pic);

	int updateStatus(@Param("id") long id, @Param("status") int status);

	AlbumPic getByPicHash(String picHash);

	List<AlbumPic> getList(@Param("albumId") int albumId, @Param("sortType") int sortType);

}
