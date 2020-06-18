package com.dili.rule.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.dili.rule.domain.ConditionDefinition;
import com.dili.rule.service.ConditionDefinitionService;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.metadata.BatchProviderMeta;
import com.dili.ss.metadata.FieldMeta;
import com.dili.ss.metadata.ValuePair;
import com.dili.ss.metadata.ValueProvider;
import com.dili.ss.metadata.provider.BatchDisplayTextProviderSupport;

import one.util.streamex.StreamEx;

@Component
@Scope("prototype")
public class TargetValProvider  implements ValueProvider{
	@Autowired
	ConditionDefinitionService conditionDefinitionService;

	@Override
	public List<ValuePair<?>> getLookupList(Object val, Map metaMap, FieldMeta fieldMeta) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDisplayText(Object val, Map metaMap, FieldMeta fieldMeta) {
		// TODO Auto-generated method stub
		if(val==null) {
			return "";
		}
		String expression=String.valueOf(val);
		if(StringUtils.isBlank(expression)||expression.indexOf("[")<0) {
			return expression;
		}
		List<Long> idList = parseVariableId(expression);
		Map<Long, String> idLableMap = StreamEx.of(idList).map(id -> {
			return this.conditionDefinitionService.get(id);
		}).nonNull().toMap(ConditionDefinition::getId, ConditionDefinition::getLabel);
		
		for (Long id : idList) {
			expression = expression.replaceAll("\\[" + id + "\\]", idLableMap.getOrDefault(id, ""));
		}
		return expression;
	}
	protected List getFkList(List<String> relationIds, Map metaMap) {
		Map<Long, String> idLableMap = StreamEx.of(relationIds).nonNull().flatMap(s -> {
			return parseVariableId(s).stream();
		}).distinct().map(id -> {
			return this.conditionDefinitionService.get(id);
		}).nonNull().toMap(ConditionDefinition::getId, ConditionDefinition::getLabel);
		return StreamEx.of(relationIds).nonNull().map(s -> {
			List<Long> idList = parseVariableId(s);
			String str = s;

			for (Long id : idList) {
				str = str.replaceAll("\\[" + id + "\\]", idLableMap.getOrDefault(id, ""));
			}
			return str;
		}).toList();
	}

	public static void main(String[] args) {
		String[] str = new String[] { "50*[4]*10", "12", "{}" };
		Stream.of(str).filter(s -> s.indexOf("[") > -1).flatMap(s -> {
			return parseVariableId(s).stream();
		});

		System.out.println(parseVariableId("[23][45]"));
	}

	public static List<Long> parseVariableId(String text) {
		Pattern pattern = Pattern.compile("(\\[\\d+\\])");
		Matcher m = pattern.matcher(text);
		List<Long> idList = new ArrayList<>();
		while (m.find()) {
			System.out.println(m.group());
			idList.add(Long.parseLong(m.group().replace("[", "").replace("]", "")));
		}
		return idList;
	}



}
