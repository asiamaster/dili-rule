package com.dili.rule.domain.vo;

import java.util.List;

/**
 * 页面条件指标Vo对象
 */
public class ConditionVo {

	/**
	 * 预定义ID
	 */
	private Long definitionId;
	/**
	 * 匹配key
	 */
	private String matchKey;
	/**
	 * 匹配值信息
	 */
	private List<String> matchValues;

	public Long getDefinitionId() {
		return definitionId;
	}
	public void setDefinitionId(Long definitionId) {
		this.definitionId = definitionId;
	}
	public String getMatchKey() {
		return matchKey;
	}
	public void setMatchKey(String matchKey) {
		this.matchKey = matchKey;
	}
	public List<String> getMatchValues() {
		return matchValues;
	}
	public void setMatchValues(List<String> matchValues) {
		this.matchValues = matchValues;
	}
	

}
