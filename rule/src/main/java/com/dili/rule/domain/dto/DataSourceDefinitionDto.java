package com.dili.rule.domain.dto;

import java.util.List;

import javax.persistence.Column;

import com.dili.rule.domain.DataSourceDefinition;
import com.dili.ss.domain.annotation.Operator;

/**
 * <B></B>
 * <B>Copyright:本软件源代码版权归农丰时代科技有限公司及其研发团队所有,未经许可不得任意复制与传播.</B>
 * <B>农丰时代科技有限公司</B>
 *
 * @author yuehongbo
 * @date 2020/5/23 16:58
 */
public class DataSourceDefinitionDto extends DataSourceDefinition {

    /**
     * 查询条件中的ID集
     */
    @Column(name = "`id`")
    @Operator(Operator.IN)
    private List<Long> idList;

	public List<Long> getIdList() {
		return idList;
	}

	public void setIdList(List<Long> idList) {
		this.idList = idList;
	}

	@Override
	public String toString() {
		return "DataSourceDefinitionDto [idList=" + idList + "]";
	}
    
}
