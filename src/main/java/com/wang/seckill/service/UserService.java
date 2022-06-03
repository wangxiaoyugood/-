package com.wang.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wang.seckill.pojo.User;
import com.wang.seckill.vo.LoginVo;
import com.wang.seckill.vo.RespBean;
import com.wang.seckill.vo.RespBeanEnum;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wang
 * @since 2022-05-09
 */
public interface UserService{
    public RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);

    public User test();

    public User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response);

    //更新密码
    public RespBean updatePassword(String userTicket, String password, HttpServletRequest request, HttpServletResponse response);


}
