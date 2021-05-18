package com.nowcoder.community.service;

import com.nowcoder.community.mapper.AlphaDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
//@Scope("prototype") // 默认为单例模式，prototype就变成了多例
public class AlphaService {

    @Autowired
    private AlphaDAO alphaDAO;

    public AlphaService(){
        System.out.println("实例化AlphaService");
    }

    @PostConstruct
    public void init(){
        System.out.println("初始化AlphaService");
    }

    @PreDestroy
    public void destroy(){
        System.out.println("销毁AlphaService");
    }

    public String find(){
       return alphaDAO.select();
    }
}
