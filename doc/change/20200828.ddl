create table rule_action (
    id bigint not null auto_increment comment '唯一ID',
    rule_id bigint comment '规则ID',
    seq_num int comment '顺序(优先级:值越小优先级越高)',
    condition_express varchar(200) comment '前置条件(表达式)',
    action_express varchar(200) comment '计算值(表达式)',
    create_time datetime default CURRENT_TIMESTAMP comment '创建时间',
    modify_time datetime default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
    primary key (id)
);
alter table rule_action comment '规格计算表达式';