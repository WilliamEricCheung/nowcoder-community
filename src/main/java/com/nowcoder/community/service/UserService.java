package com.nowcoder.community.service;

import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;

import java.util.Map;

public interface UserService {

    Map<String, Object> register(User user);
    int activation(int userId, String code);
    Map<String, Object> verify(String email, String code);
    int resetPassword(String email, String password);
    Map<String, Object> login(String username, String password, int expiredSeconds);
    void logout(String ticket);
    LoginTicket findLoginTicket(String ticket);

    User findUserById(int id);
    User findUserByName(String name);
    User findUserByEmail(String email);
    int addUser(User user);
    int updateStatus(int id, int status);
    int updateHeader(int id, String header);
    int updatePassword(int id, String password);
    int insertLoginTicket(LoginTicket loginTicket);
    LoginTicket selectByTicket(String ticket);
    int updateStatus(String ticket, int status);
}
