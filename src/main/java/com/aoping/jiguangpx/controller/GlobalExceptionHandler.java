package com.aoping.jiguangpx.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.aoping.jiguangpx.base.PxException;
import com.aoping.jiguangpx.base.Result;
import com.aoping.jiguangpx.base.ResultCode;
import com.aoping.jiguangpx.base.ResultUtil;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
	/**
	 * controller方法参数校验失败处理
	 *
	 * @param e
	 * @return
	 */
	@ExceptionHandler
	public Result<?> handle(ConstraintViolationException e) {
		log.error("ConstraintViolationException ", e);
		List<String> messages = e.getConstraintViolations().stream()
				.map(i -> String.format("值'%s'无效：%s", i.getInvalidValue(), i.getMessage()))
				.collect(Collectors.toList());

		return ResultUtil.toResult(ResultCode.FAIL.getCode(), "参数错误" + StringUtils.join(messages, ";"));
	}

	@ExceptionHandler
	public Result<?> handle(MissingServletRequestParameterException e, HttpServletRequest request) {
		String uri = request.getRequestURI();
		log.error("MissingServletRequestParameter,url:{}", uri, e);
		return ResultUtil.toResult(ResultCode.FAIL.getCode(), "缺少请求参数：" + e.getParameterName());
	}

	@ExceptionHandler
	public Result<?> handle(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
		String uri = request.getRequestURI();
		Class<?> clazz = e.getRequiredType();
		Object value = e.getValue();
		log.info("MethodArgumentTypeMismatch error,url:{},paramName:{},givenValue:{},requiredType:{}", uri, e.getName(),
				value, clazz.getName(), e);
		return ResultUtil.toResult(ResultCode.FAIL.getCode(), "参数类型不正确");
	}

	@ExceptionHandler
	public Result<?> handle(MethodArgumentNotValidException e) {
		log.warn(e.getMessage(), e);
		List<String> errors = new ArrayList<>();
		for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
			if (StringUtils.isNotBlank(fieldError.getDefaultMessage())) {
				errors.add(fieldError.getDefaultMessage());
			}
		}

		if (errors == null || errors.isEmpty()) {
			errors.add("参数错误");
		}
		return ResultUtil.fail(StringUtils.join(errors, ";"));
	}

	/**
	 * 兜底
	 *
	 * @param e
	 * @return
	 */
	@ExceptionHandler(PxException.class)
	public Result<?> handle(PxException e) {
		log.error("自定义异常 {}", e);
		return ResultUtil.toResult(e.getCode(), e.getMessage());
	}

	/**
	 * 兜底
	 *
	 * @param e
	 * @return
	 * @throws Exception
	 */
	@ExceptionHandler
	public Result<?> handle(Exception e) throws Exception {
		log.error("服务器异常 {}", e);
		if (e instanceof AccessDeniedException) {
			throw e;
		}
		return ResultUtil.fail();
	}
}
