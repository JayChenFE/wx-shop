
CREATE TABLE users(
    gmt_create DATETIME    COMMENT '创建时间' ,
    gmt_modified DATETIME    COMMENT '更新时间' ,
    id bigint unsigned NOT NULL AUTO_INCREMENT  COMMENT 'id' ,
    name VARCHAR(128)    COMMENT '姓名' ,
    tel VARCHAR(32)    COMMENT '电话' ,
    avatar_url VARCHAR(1024)    COMMENT '头像' ,
    PRIMARY KEY (id)
) COMMENT = '用户表';

ALTER TABLE users ADD UNIQUE uk_tel(tel);
ALTER TABLE users COMMENT '用户表';
