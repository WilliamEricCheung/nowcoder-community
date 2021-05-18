package com.nowcoder.community.service;

import com.nowcoder.community.entity.User;

public interface UserService {
    User findUserById(int id);
    User findUserByName(int name);
    User findUserByEmail(String email);
    int addUser(User user);
    int updateStatus(int id, int status);
    int updateHeader(int id, String header);
    int updatePassword(int id, String password);
}
