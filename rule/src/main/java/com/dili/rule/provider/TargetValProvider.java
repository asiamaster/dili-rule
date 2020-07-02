package com.dili.rule.provider;

import com.dili.rule.service.ConditionDefinitionService;
import com.dili.ss.metadata.FieldMeta;
import com.dili.ss.metadata.ValuePair;
import com.dili.ss.metadata.ValueProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Scope("prototype")
public class TargetValProvider  implements ValueProvider{
	@Autowired
	private ConditionDefinitionService conditionDefinitionService;

	@Override
	public List<ValuePair<?>> getLookupList(Object val, Map metaMap, FieldMeta fieldMeta) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDisplayText(Object val, Map metaMap, FieldMeta fieldMeta) {
		if (val == null) {
			return "";
		}
		return conditionDefinitionService.convertTargetValDefinition(String.valueOf(val),true);
	}

}
