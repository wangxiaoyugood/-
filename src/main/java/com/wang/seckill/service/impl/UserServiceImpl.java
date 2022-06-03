package com.wang.seckill.service.impl;

import com.wang.seckill.exception.GlobalException;
import com.wang.seckill.mapper.UserMapper;
import com.wang.seckill.pojo.User;
import com.wang.seckill.service.UserService;
import com.wang.seckill.utils.CookieUtil;
import com.wang.seckill.utils.MD5Util;
import com.wang.seckill.utils.UUIDUtil;
import com.wang.seckill.utils.ValidatorUtil;
import com.wang.seckill.vo.LoginVo;
import com.wang.seckill.vo.RespBean;
import com.wang.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wang
 * @since 2022-05-09
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    //redis模板
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        System.out.println("前端传来的password:"+password);


//        if(StringUtils.isEmpty(mobile)||StringUtils.isEmpty(password)){
//            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
//        }
//
//        if(!ValidatorUtil.isMobile(mobile)){
//            return RespBean.error(RespBeanEnum.MOBILE_ERROR);
//        }

        User user = userMapper.selectById(mobile);

        if(user==null){
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }

        System.out.println("用户二次加密的密码："+MD5Util.formPassToDBPass(password, user.getSlat()));
        System.out.println("数据库里的密码:"+user.getPasword());

        if(!MD5Util.formPassToDBPass(password, user.getSlat()).equals(user.getPasword())){
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }

        //生成cookie
        String ticket = UUIDUtil.uuid();
        //将用户信息存入redis中
        redisTemplate.opsForValue().set("user:"+ticket, user);
        //request.getSession().setAttribute(ticket, user);
        CookieUtil.setCookie(request, response, "userTicket", ticket);
        return RespBean.success(ticket);
    }

    @Override
    public User test() {
        User user = userMapper.selectById("18256224745");
        return user;
    }

    @Override
    public User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response) {
        User user =(User) redisTemplate.opsForValue().get("user:" + userTicket);
        if(user!=null){
            CookieUtil.setCookie(request, response, "userTicket", userTicket);
        }

        return user;
    }

    @Override
    public RespBean updatePassword(String userTicket, String password, HttpServletRequest request, HttpServletResponse response) {


        User user = getUserByCookie(userTicket, request, response);
        if(user==null){
            throw new GlobalException(RespBeanEnum.MOBILE_NOT_EXIST);
        }
        user.setPasword(MD5Util.inputPassToDBPass(password,user.getSlat()));
        int i = userMapper.updateById(user);
        if(i==1){
            //密码更改，重新设置redis缓存
            redisTemplate.delete("user:"+userTicket);
            return RespBean.success();
        }
        return RespBean.error(RespBeanEnum.Err_PASSWORD);
    }
}
