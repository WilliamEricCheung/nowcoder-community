package com.nowcoder.community.mapper;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public class AlphaDAOMyBatisImpl implements AlphaDAO{

    @Override
    public String select() {
        return "MyBatis";
    }
}
