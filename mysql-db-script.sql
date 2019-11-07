-- auto-generated definition
create table sys_export_conf
(
    id               varchar(32)   not null
        primary key,
    column_conf_json varchar(1024) null comment '数据、列关系配置json',
    bean_id          varchar(64)   null comment 'spring bean id',
    method_name      varchar(64)   null comment 'spring method name',
    create_id        bigint        null comment '创建人',
    tm_create        datetime      null comment '创建时间',
    update_id        bigint        null comment '更新人',
    tm_update        datetime      null comment '更新时间',
    remark           varchar(512)  null comment 'remark',
    is_del           int default 0 null comment '逻辑删除标识'
);

create table wms.sys_export_plan
(
    id             varchar(32)   not null,
    user_id        bigint        null,
    bean_id        varchar(128)  null comment 'spring bean id',
    method_name    varchar(128)  null comment 'bean''s method name',
    request_params varchar(1024) null comment '导出表格表头信息配置json',
    line_count     bigint        null comment '数据行数',
    url            varchar(1024) null comment 'http下载地址',
    plan_status    varchar(10)   null comment '计划状态：待执行、执行成功、执行失败',
    retry_qty      int default 0 null comment '异常重试次数',
    i18n           varchar(16)   null comment '国际化标记符',
    failed_msg     varchar(1024) null comment '错误消息',
    tm_create      datetime      null comment '创建时间',
    tm_success     datetime      null comment '完成时间',
    is_del         int default 0 null comment '删除标识，0=未删除，1=删除',
    constraint sys_export_plan_id_uindex
        unique (id)
)
    comment '导出计划表';

alter table wms.sys_export_plan
    add primary key (id);

