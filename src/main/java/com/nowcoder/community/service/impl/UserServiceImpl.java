package com.nowcoder.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.mapper.UserMapper;
import com.nowcoder.community.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User findUserById(int id) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        return userMapper.selectOne(queryWrapper);
    }
}
