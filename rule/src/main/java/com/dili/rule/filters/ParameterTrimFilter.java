package com.jxxc.admin.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * 自定义过滤器，用于对请求参数去空格处理。
 */
public class ParameterTrimFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		ParameterTrimRequest trimReqeust = new ParameterTrimRequest((HttpServletRequest) request);
		chain.doFilter(trimReqeust, response);
	}

	@Override
	public void destroy() {
	}
}