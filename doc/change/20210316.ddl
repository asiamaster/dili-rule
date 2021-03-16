ALTER TABLE `dili_rule`.`data_source_query_config` MODIFY COLUMN query_key varchar(40)  NULL COMMENT '匹配Key值';
ALTER TABLE `dili_rule`.`data_source_column` MODIFY COLUMN column_code varchar(40)  NULL COMMENT '列编码';
ALTER TABLE `dili_rule`.`condition_definition` MODIFY COLUMN match_key varchar(40)  NULL COMMENT '条件定义key值(关联匹配字段)';
ALTER TABLE `dili_rule`.`condition_definition` MODIFY COLUMN match_column varchar(40)  NULL COMMENT '匹配数据源中的某列值';
ALTER TABLE `dili_rule`.`condition_definition` MODIFY COLUMN parent_column varchar(40)  NULL COMMENT 'parent对应的列名';