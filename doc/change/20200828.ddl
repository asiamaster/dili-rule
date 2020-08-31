ALTER TABLE `charge_rule` ADD COLUMN    `action_expression`  varchar(500) null comment '计算指标';
ALTER TABLE `charge_rule` ADD COLUMN    `action_expression_params`  json comment '计算指标条件参数';
ALTER TABLE `charge_rule` ADD COLUMN    `action_expression_type`  INT comment '计算指标类型';
UPDATE `charge_rule` SET `action_expression_type`=1 ;
UPDATE `charge_rule` SET `action_expression`=`target_val`;
ALTER TABLE `charge_rule`  DROP COLUMN `target_val`;

