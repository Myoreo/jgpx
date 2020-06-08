package com.aoping.jiguangpx.dao;

import java.util.List;

import com.aoping.jiguangpx.entity.album.Album;

public interface AlbumDao {
	
	int insert(Album album);
	
	int update(Album album);
	
	List<Album> getList(int accountId);
	
	Album getById(int id);

}
