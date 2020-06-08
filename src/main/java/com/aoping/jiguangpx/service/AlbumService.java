package com.aoping.jiguangpx.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aoping.jiguangpx.common.QiniuService;
import com.aoping.jiguangpx.config.auth.SecurityUtil;
import com.aoping.jiguangpx.dao.AlbumDao;
import com.aoping.jiguangpx.entity.album.Album;
import com.aoping.jiguangpx.entity.vo.AlbumVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service
public class AlbumService {

	@Autowired
	private AlbumDao albumDao;

	@Autowired
	private QiniuService qiniuService;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public PageInfo getList(int page, int size) {
		PageHelper.startPage(page, size);
		List<AlbumVo> albums = new ArrayList<>();
		List<Album> list = albumDao.getList(SecurityUtil.getAccountId());

		for (Album album : list) {
			AlbumVo albumVo = new AlbumVo(album);
			albumVo.setCover(qiniuService.getDownUrl(album.getCover()));
			albums.add(albumVo);
		}
		PageInfo pageInfo = new PageInfo<>(list);

		pageInfo.setList(albums);
		return pageInfo;
	}

	public AlbumVo edit(int id, String name, String cover) {
		Album album = null;
		boolean isUpdate = false;
		int accountId = SecurityUtil.getAccountId();
		if (id == 0) {
			album = new Album();
			album.setAccountId(accountId);

		} else {
			album = albumDao.getById(id);
			if (album.getAccountId() != accountId) {
				return null;
			}
			isUpdate = true;
		}
		album.setName(name);
		album.setCover(cover);
		if (isUpdate) {
			albumDao.update(album);
		} else {
			albumDao.insert(album);
		}

		AlbumVo albumVo = new AlbumVo(album);
		albumVo.setCover(qiniuService.getDownUrl(album.getCover()));
		return albumVo;
	}
	
	public Album getById(int id) {
		return albumDao.getById(id);
	}

}
