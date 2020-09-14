package com.jxxc.admin.filters;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 重新封装request，并对请求参数做去空格处理
 */
public class ParameterTrimRequest extends HttpServletRequestWrapper {

	private Map<String, String[]> params = new HashMap<String, String[]>();// 保存处理后的参数

	public ParameterTrimRequest(HttpServletRequest request) {
		super(request);
		this.params.putAll(request.getParameterMap());
		this.modifyParameterValues(); // 自定义方法，用于参数去重
	}

	public void modifyParameterValues() {// 将parameter的值去除空格后重写回去
		Set<Entry<String, String[]>> entrys = params.entrySet();
		for (Entry<String, String[]> entry : entrys) {
			String[] values = entry.getValue();
			for (int i = 0; i < values.length; i++) {
				values[i] = values[i].trim();
			}
			this.params.put(entry.getKey(), values);
		}
	}

	@Override
	public Enumeration<String> getParameterNames() {// 重写getParameterNames()
		return new Vector<String>(params.keySet()).elements();
	}

	@Override
	public String getParameter(String name) {// 重写getParameter()
		String[] values = params.get(name);
		if (values == null || values.length == 0) {
			return null;
		}
		return values[0];
	}

	@Override
	public String[] getParameterValues(String name) {// 重写getParameterValues()
		return params.get(name);
	}

	@Override
	public Map<String, String[]> getParameterMap() { // 重写getParameterMap()
		return this.params;
	}

	/**
	 * 重写getInputStream方法 post类型的请求参数必须通过流才能获取到值
	 */
	@Override
	public ServletInputStream getInputStream() throws IOException {
		// 非json类型，直接返回
		if (!MediaType.APPLICATION_JSON_VALUE.equalsIgnoreCase(super.getHeader(HttpHeaders.CONTENT_TYPE))) {
			return super.getInputStream();
		}
		// 为空，直接返回
		String json =IOUtils.toString(super.getInputStream(), StandardCharsets.UTF_8);
		if (StringUtils.isEmpty(json)) {
			return super.getInputStream();
		}
		ObjectMapper mapper=new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
		mapper.getDeserializationConfig().without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
				DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES);
		
		Map<String, Object> map= mapper.readValue(json.getBytes(StandardCharsets.UTF_8), Map.class);
		Set<String> set = map.keySet();
		Iterator<String> it = set.iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			Object values = map.get(key);
			if (values instanceof String) {
				values = ((String) values).trim();
			}
			map.put(key, values);
		}
		
		ByteArrayInputStream bis = new ByteArrayInputStream(mapper.writeValueAsBytes(map));
		return new MyServletInputStream(bis);
	}
	
	
	class MyServletInputStream extends ServletInputStream {
		private ByteArrayInputStream bis;

		public MyServletInputStream(ByteArrayInputStream bis) {
			this.bis = bis;
		}

		@Override
		public boolean isFinished() {
			return true;
		}

		@Override
		public boolean isReady() {
			return true;
		}

		@Override
		public void setReadListener(ReadListener listener) {

		}

		@Override
		public int read() throws IOException {
			return bis.read();
		}

	}

}