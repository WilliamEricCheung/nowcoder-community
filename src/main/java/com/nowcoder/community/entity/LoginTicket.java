package com.nowcoder.community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class LoginTicket implements Serializable {

    private static final long serialVersionUID = 6440166229040160464L;
    @TableId(type = IdType.AUTO)
    private int id;
    private int userId;
    private String ticket;
    private int status;
    private Date expired;
}
