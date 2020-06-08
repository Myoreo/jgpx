package com.aoping.jiguangpx.service.interceptor;

import java.lang.annotation.Annotation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import com.aoping.jiguangpx.base.PxException;
import com.aoping.jiguangpx.base.ResultCode;
import com.aoping.jiguangpx.config.AnnotationSupportInterceptor;
import com.aoping.jiguangpx.config.annotation.AlbumPermissonCheck;
import com.aoping.jiguangpx.config.auth.SecurityUtil;
import com.aoping.jiguangpx.entity.album.Album;
import com.aoping.jiguangpx.service.AlbumService;


@Order(10)
@Component
public class AlbumPermissonInterceptor extends AnnotationSupportInterceptor {

	@Autowired
	private AlbumService albumService;

	@Override
	protected Class<? extends Annotation> getRequiredAnnotationClass() {
		return AlbumPermissonCheck.class;
	}

	@Override
	protected boolean preHandle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod)
			throws Exception {
		int albumId = NumberUtils.toInt(request.getParameter("albumId"), 0);
		if (albumId == 0) {
			throw new PxException(ResultCode.ALBUM_NO_PERMISSION);
		}

		Album album = albumService.getById(albumId);

		if (album == null) {
			throw new PxException(ResultCode.ALBUM_NOT_EXIST);
		}
		if (album.getAccountId() != SecurityUtil.getAccountId()) {
			throw new PxException(ResultCode.ALBUM_NO_PERMISSION);
		}
		return super.preHandle(request, response, handlerMethod);
	}

}
