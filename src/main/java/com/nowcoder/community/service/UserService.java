package com.nowcoder.community.service;

import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;

import java.util.Map;

public interface UserService {

    public Map<String, Object> register(User user);
    public int activation(int userId, String code);
    public Map<String, Object> verify(String email, String code);
    public int resetPassword(String email,String password);
    public Map<String, Object> login(String username, String password, int expiredSeconds);
    public void logout(String ticket);

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
