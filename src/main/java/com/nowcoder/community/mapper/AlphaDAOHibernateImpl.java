package com.nowcoder.community.mapper;

import org.springframework.stereotype.Repository;

@Repository("alphaHibernate")
public class AlphaDAOHibernateImpl implements AlphaDAO {

    @Override
    public String select() {
        return "Hibernate";
    }
}
