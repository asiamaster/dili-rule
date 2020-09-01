ALTER TABLE `charge_rule` ADD COLUMN    `action_expression`  varchar(500) null comment '计算指标';
ALTER TABLE `charge_rule` ADD COLUMN    `action_expression_params`  json comment '计算指标条件参数';
ALTER TABLE `charge_rule` ADD COLUMN    `action_expression_type`  INT comment '计算指标类型';
UPDATE `charge_rule` SET `action_expression_type`=1 ;
UPDATE `charge_rule` SET `action_expression`=`target_val`;
ALTER TABLE `charge_rule`  DROP COLUMN `target_val`;



/*==============================================================*/
/* Table: datasource_query_config                                    */
/*==============================================================*/
create table datasource_query_config
(
   id                   bigint not null auto_increment comment '主键ID',
   `data_source_id`     bigint comment '所属规则',
   `label`              varchar(20) comment '查询标签',
   `query_key`          varchar(20) comment '匹配Key值',
   `data_type`          integer comment '值类型值类型(小数、整数等)',
   `default_val`        varchar(20) comment '默认值',
   create_time          datetime default CURRENT_TIMESTAMP comment '创建时间',
   modify_time          datetime default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
   primary key (id)
);
alter table datasource_query_config comment '远程数据源查询输入框配置信息';

