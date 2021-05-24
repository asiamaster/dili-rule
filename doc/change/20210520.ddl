ALTER TABLE `dili_rule`.`data_source_definition` ADD COLUMN  `autocomplete_query_key` VARCHAR(50) NULL comment '联想查询规则时的key值';

ALTER TABLE `dili_rule`.`data_source_query_config` MODIFY COLUMN  `query_key`   varchar(50) comment '匹配Key值';
