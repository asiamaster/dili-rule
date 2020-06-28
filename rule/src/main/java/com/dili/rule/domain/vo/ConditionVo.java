package com.dili.rule.domain.vo;

import java.util.List;

public class ConditionVo {
	private Long definitionId;
	private String matchKey;
	private List<String>matchValues;

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
