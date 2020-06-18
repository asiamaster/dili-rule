package com.dili.rule.domain.vo;

import java.util.List;

public class ConditionVo {
	private Long definitionId;
	private String matchKey;
	private List<String>matchedValues;

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
	public List<String> getMatchedValues() {
		return matchedValues;
	}
	public void setMatchedValues(List<String> matchedValues) {
		this.matchedValues = matchedValues;
	}
	

}
