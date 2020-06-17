package com.dili.rule.domain.dto;

import com.dili.rule.domain.ConditionDataSource;
import com.dili.ss.domain.annotation.Operator;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import java.util.List;

/**
 * <B></B>
 * <B>Copyright:本软件源代码版权归农丰时代科技有限公司及其研发团队所有,未经许可不得任意复制与传播.</B>
 * <B>农丰时代科技有限公司</B>
 *
 * @author yuehongbo
 * @date 2020/5/23 16:58
 */
@Getter
@Setter
@ToString(callSuper = true)
public class ConditionDataSourceDto extends ConditionDataSource {

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
    
}
