package com.wang.seckill.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author wang
 * @since 2022-05-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_user")
@ToString
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID shoujihaoma
     */
    private Long id;

    private String nickname;

    /**
     * MD5二次加密
     */
    private String pasword;

    private String slat;

    /**
     * 头像
     */
    private String head;

    /**
     * 注册时间
     */
    private Date registerDate;

    /**
     * 最后一次登录时间
     */
    private Date lastLoginDate;

    /**
     * 登录次数
     */
    private Integer loginCount;


}
