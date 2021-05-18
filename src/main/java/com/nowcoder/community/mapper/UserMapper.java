package com.nowcoder.community.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nowcoder.community.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {

}
