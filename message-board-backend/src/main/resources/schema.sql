-- 用户表
CREATE TABLE IF NOT EXISTS customer
(
    id            TINYINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    name          VARCHAR(64)  NOT NULL UNIQUE COMMENT '用户名',
    password      VARCHAR(255) NOT NULL COMMENT '密码',
    email         VARCHAR(255) NOT NULL UNIQUE COMMENT '邮箱',
    last_login_at TIMESTAMP COMMENT '最后登录时间',
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
);

-- 留言表
CREATE TABLE IF NOT EXISTS message
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    content      CLOB      NOT NULL comment '留言内容',
    customer_id  TINYINT   NOT NULL COMMENT '用户ID',
    parent_id    BIGINT COMMENT '父留言ID',
    created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at   TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
);

-- 点赞记录表
CREATE TABLE IF NOT EXISTS likes
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
    message_id BIGINT    NOT NULL COMMENT '关联的留言ID',
    user_id    BIGINT    NOT NULL COMMENT '点赞用户ID',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT uk_message_user UNIQUE (message_id, user_id) -- 确保用户对同一留言只能点赞一次
);