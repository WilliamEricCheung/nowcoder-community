package com.nowcoder.community.service;

import com.nowcoder.community.entity.User;

import java.util.Map;

public interface UserService {

    public Map<String, Object> register(User user);
    public int activation(int userId, String code);

    User findUserById(int id);
    User findUserByName(String name);
    User findUserByEmail(String email);
    int addUser(User user);
    int updateStatus(int id, int status);
    int updateHeader(int id, String header);
    int updatePassword(int id, String password);
}
