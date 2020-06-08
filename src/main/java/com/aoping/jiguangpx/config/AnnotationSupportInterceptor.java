package com.aoping.jiguangpx.config;

import org.springframework.core.PriorityOrdered;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;

/**
 * 支持通过注解控制拦截器是否生效
 */
public class AnnotationSupportInterceptor implements AsyncHandlerInterceptor, AutoRegisteredInterceptor, PriorityOrdered {

    /**
     * 只要Controllert或当前Controller方法中的一个加了这个注解，当前拦截器生效
     * @return
     */
    protected Class<? extends Annotation> getRequiredAnnotationClass() {
        return null;
    }

    /**
     * 作用同{@link AsyncHandlerInterceptor#preHandle(HttpServletRequest, HttpServletResponse, Object)}
     * @param request
     * @param response
     * @param handlerMethod
     * @return
     */
    protected boolean preHandle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) throws Exception {
        return true;
    }

    /**
     * 作用同{@link AsyncHandlerInterceptor#postHandle(HttpServletRequest, HttpServletResponse, Object, ModelAndView)}
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    protected void postHandle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler, ModelAndView modelAndView) throws Exception {
    }

    /**
     * 作用同{@link AsyncHandlerInterceptor#afterCompletion(HttpServletRequest, HttpServletResponse, Object, Exception)}
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    protected void afterCompletion(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler, Exception ex) throws Exception {

    }

    /**
     * 作用同{@link AsyncHandlerInterceptor#afterConcurrentHandlingStarted(HttpServletRequest, HttpServletResponse, Object)}
     * @param request
     * @param response
     * @param handler
     * @throws Exception
     */
    protected void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler) throws Exception {

    }

    @Override
    public final boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!isSupported(handler)) {
            return true;
        }

        return preHandle(request, response, (HandlerMethod) handler);
    }

    @Override
    public final void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (!isSupported(handler)) {
            return;
        }

        postHandle(request, response, (HandlerMethod) handler, modelAndView);
    }

    @Override
    public final void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (!isSupported(handler)) {
            return;
        }

        afterCompletion(request, response, (HandlerMethod) handler, ex);
    }

    @Override
    public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!isSupported(handler)) {
            return;
        }

        afterConcurrentHandlingStarted(request, response, (HandlerMethod) handler);
    }

    protected boolean isSupported(Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return false;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Class<? extends Annotation> clazz = getRequiredAnnotationClass();
        if (clazz == null) {
            return false;
        }
        if (handlerMethod.getMethod().isAnnotationPresent(clazz)) {
            return true;
        }
        if (handlerMethod.getBeanType().isAnnotationPresent(clazz)) {
            return true;
        }

        return false;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
