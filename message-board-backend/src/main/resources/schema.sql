DROP TABLE IF EXISTS customer;

CREATE TABLE customer (
                    id TINYINT AUTO_INCREMENT PRIMARY KEY comment '主键',
                    name VARCHAR(64) NOT NULL UNIQUE comment '用户名',
                    password VARCHAR(255) NOT NULL comment '密码',
                    email VARCHAR(255) NOT NULL UNIQUE comment '邮箱',
                    last_login_time TIMESTAMP comment '最后登录时间',
                    created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间'
);