package com.nowcoder.community.service;

public interface FollowService {

    void follow(int userId, int entityType, int entityId);

    void unfollow(int userId, int entityType, int entityId);

    // 查询关注的实体数量
    long findFolloweeCount(int userId, int entityType);

    // 查询实体的粉丝数量
    long findFollowerCount(int entityType, int entityId);

    // 查询当前用户是否已关注改实体
    boolean hasFollowed(int userId, int entityType, int entityId);
}
