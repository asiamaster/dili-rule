package com.dili.rule.utils;

import java.util.List;

import com.dili.ss.domain.EasyuiPageOutput;

public class EasyuiPageOutputUtil {
	public static EasyuiPageOutput build(long total, List rows) {

		EasyuiPageOutput out = new EasyuiPageOutput();
		out.setTotal(total);
		out.setRows(rows);
		return out;
	}

}
