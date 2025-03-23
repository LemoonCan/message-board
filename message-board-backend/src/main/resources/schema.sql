-- 先禁用外键约束检查
SET REFERENTIAL_INTEGRITY FALSE;

-- 创建用户表
CREATE TABLE IF NOT EXISTS customer (
                    id TINYINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
                    name VARCHAR(64) NOT NULL UNIQUE COMMENT '用户名',
                    password VARCHAR(255) NOT NULL COMMENT '密码',
                    email VARCHAR(255) NOT NULL UNIQUE COMMENT '邮箱',
                    last_login_at TIMESTAMP COMMENT '最后登录时间',
                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
);

-- 创建留言表
CREATE TABLE IF NOT EXISTS message (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
                    content TEXT NOT NULL COMMENT '留言内容',
                    customer_id TINYINT NOT NULL COMMENT '用户ID',
                    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                    updated_at TIMESTAMP COMMENT '更新时间',
                    FOREIGN KEY (customer_id) REFERENCES customer(id)
);

-- 创建闭包表
CREATE TABLE IF NOT EXISTS message_closure (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
                    ancestor_id BIGINT NOT NULL COMMENT '祖先留言ID',
                    descendant_id BIGINT NOT NULL COMMENT '后代留言ID',
                    depth INT NOT NULL COMMENT '层级深度',
                    FOREIGN KEY (ancestor_id) REFERENCES message(id),
                    FOREIGN KEY (descendant_id) REFERENCES message(id),
                    UNIQUE (ancestor_id, descendant_id)
);

-- 重新启用外键约束检查
SET REFERENTIAL_INTEGRITY TRUE;