drop table if exists charge_rule;

drop table if exists charge_condition_val;

drop table if exists condition_data_source;

drop table if exists data_source_column;

drop table if exists condition_definition;

/*==============================================================*/
/* Table: charge_rule                                                  */
/*==============================================================*/
create table charge_rule
(
   id                   bigint not null auto_increment comment '唯一ID',
   original_id          bigint comment '原始ID(在非启用状态下，修改数据，会生成一条新规则，需记录原始规则ID)',
   code                 varchar(50) comment '计费规则编码',
   market_id            bigint comment '规则所属于某个市场',
   system_code          varchar(20) comment '规则所属于的某系统',
   business_type        varchar(20) comment '所属的业务类型',
   group_id             bigint comment '组别',
   charge_item          varchar(20) comment '收费项',
   rule_name            varchar(30) comment '规则名称',
   state                int comment '规则状态',
   priority             int comment '优先级',
   expire_start         datetime comment '有效期起始',
   expire_end           datetime comment '有效期止',
   target_type          int comment '计算指标类型',
   target_val           json comment '计算指标',
   min_payment          decimal(8,2) comment '匹配到此规则时最低应支付的金额',
   max_payment          decimal(10,2) comment '匹配到此规则时最高支付金额',
   remark               varchar(50) comment '备注',
   revisable            tinyint comment '是否可修改,如果因修改而产生了新记录，则本记录不可再修改',
   operator_id          bigint comment '操作员',
   operator_name        varchar(20) comment '操作员姓名',
   create_time          datetime default CURRENT_TIMESTAMP comment '创建时间',
   modify_time          datetime default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
   approver_id          bigint comment '审核人ID',
   approver_name        varchar(20) comment '审核人姓名',
   approval_time        datetime comment '审核时间',
   primary key (id)
);
alter table charge_rule comment '计费规则';

/*==============================================================*/
/* Table: charge_condition_val                                    */
/*==============================================================*/
create table charge_condition_val
(
   id                   bigint not null auto_increment comment '主键ID',
   rule_id              bigint comment '所属规则',
   `label`              varchar(20) comment '条件标签',
   matched_key          varchar(20) comment '匹配Key值(即：需要验证的值)',
   condition_type       int comment '条件类型(大于,小于,等于)',
   data_type            integer comment '值类型值类型(小数、整数等)',
   val                  json comment '值',
   definition_id        bigint comment '条件定义',
   create_time          datetime default CURRENT_TIMESTAMP comment '创建时间',
   modify_time          datetime default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
   primary key (id)
);
alter table charge_condition_val comment '计费规则中的条件值';

/*==============================================================*/
/* Table: condition_data_source                                 */
/*==============================================================*/
create table condition_data_source
(
   id                   bigint not null auto_increment comment '唯一ID',
   name                 varchar(20) comment '数据源名称',
   query_url            varchar(255) comment '通过用户输入的url查询数据',
   keys_url             varchar(255) comment '通过ids/keys输入查询url',
   data_json            json comment '如果是本地数据，则保存再此json中，如果为远程数据，则不保存',
   data_source_type     varchar(20) comment '数据来源类型(本地、远程),具体参考DataSourceTypeEnum',
   paged                tinyint comment '是否分页',
   create_time          datetime default CURRENT_TIMESTAMP comment '创建时间',
   modify_time          datetime default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
   primary key (id)
);
alter table condition_data_source comment '预定义数据源';

/*==============================================================*/
/* Table: data_source_column                                    */
/*==============================================================*/
create table data_source_column
(
   id                   bigint not null auto_increment comment '唯一ID',
   data_source_id       bigint comment '所属数据源ID',
   column_code          varchar(20) comment '列编码',
   column_name          varchar(20) comment '列名称',
   column_index         integer comment '列索引',
   display              tinyint comment '是否用于显示，当选择数据后，此字段是否用于展示',
   visible              tinyint comment '是否可见',
   create_time          datetime default CURRENT_TIMESTAMP comment '创建时间',
   modify_time          datetime default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
   primary key (id)
);
alter table data_source_column comment '数据来源的属性列';

/*==============================================================*/
/* Table: condition_definition                                  */
/*==============================================================*/
create table condition_definition
(
   id                   bigint not null auto_increment comment '唯一ID',
   market_id            bigint comment '所属市场',
   system_code          varchar(20) comment '条件属于某个系统',
   business_type        varchar(20) comment '所属某个业务',
   `label`              varchar(20) comment '条件标签(显示文本)',
   matched_key          varchar(20) comment '条件定义key值(关联匹配字段)',
   condition_type       integer comment '条件类型(大于,小于,等于),具体参考ConditionTypeEnum',
   default_values       varchar(255) comment '条件默认值，多个以逗号隔开',
   data_type            integer comment '值类型(小数、整数等),具体参考ValueDataTypeEnum',
   data_source_id       bigint comment '数据来源ID(用于设置规则时，通过什么方法获取数据)',
   matched_column       varchar(20) comment '匹配数据源中的某列值',
   view_mode            int comment '来源数据显示方法',
   data_target_id       bigint comment '数据归于某个来源(用于设置查询条件等时的数据)',
   rule_condition       tinyint comment '此条件是否用户规则条件定义，如果不是，则认为是查询条件',
   create_time          datetime default CURRENT_TIMESTAMP comment '创建时间',
   modify_time          datetime default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '修改时间',
   primary key (id)
);
alter table condition_definition comment '规则条件预定义';

