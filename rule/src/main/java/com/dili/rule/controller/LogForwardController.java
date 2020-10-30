package com.dili.rule.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.hutool.http.HttpRequest;
import one.util.streamex.StreamEx;

/**
 * 日志转发
 * 
 * @author admin
 *
 */
@Controller
@RequestMapping("/logForwardController")
public class LogForwardController {
	@Value("${logger.contextPath:http://logger.diligrp.com:8283/api/businessLog/save}")
	private String loggerContextPath;

	private ObjectMapper mapper = new ObjectMapper();

	/**
	 * 转发
	 * 
	 * @param req
	 * @param logObj
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping("/sendLog.action")
	public Object sendLog(HttpServletRequest req, @RequestBody Map<String, Object> logObj)
			throws JsonProcessingException {
		String remoteIp = req.getRemoteHost();
		logObj.put("remoteIp", remoteIp);
		logObj.put("serverIp", req.getLocalAddr());
		String requestBody = mapper.writeValueAsString(logObj);
		Map<String, List<String>> headers = StreamEx.of(req.getHeaderNames()).mapToEntry(name -> name, name -> {
			return req.getHeaders(name);
		}).mapValues(enu -> {
			return StreamEx.of(enu).toList();

		}).toMap();

		String respBody = HttpRequest.post(loggerContextPath).header(headers).body(requestBody).execute().body();
		return mapper.readValue(respBody, Map.class);

	}
}
