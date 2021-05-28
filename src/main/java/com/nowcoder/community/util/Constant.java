package com.nowcoder.community.util;

public interface Constant {

    /**
     * 账号激活成功
     */
    int ACTIVATION_SUCCESS = 0;

    /**
     * 账户反复激活
     */
    int ACTIVATION_REPEAT = 1;

    /**
     * 账户激活失败
     */
    int ACTIVATION_FAILURE = 2;

    /**
     * 重置密码成功
     */
    int RESET_SUCCESS = 0;

    /**
     * 重复密码
     */
    int RESET_REPEAT = 1;

    /**
     * 重置密码失败
     */
    int RESET_FAILURE = 2;

    /**
     * 默认状态的登录凭证超时时间
     */
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;
    /**
     * 记住状态的登录凭证超时时间
     */
    int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 100;

    /**
     * 实体类型：帖子
     */
    int ENTITY_TYPE_POST = 1;

    /**
     * 实体类型：评论
     */
    int ENTITY_TYPE_COMMENT = 2;
}
