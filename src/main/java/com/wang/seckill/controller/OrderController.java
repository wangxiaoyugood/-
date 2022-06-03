package com.wang.seckill.controller;


import com.wang.seckill.pojo.User;
import com.wang.seckill.service.OrderService;
import com.wang.seckill.vo.OrderDetailVo;
import com.wang.seckill.vo.RespBean;
import com.wang.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wang
 * @since 2022-05-11
 */
@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @RequestMapping("/detail")
    @ResponseBody
    public RespBean detail(User user, Long orderId){
        if(user==null){
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }
        OrderDetailVo orderDetailVo =orderService.detail(orderId);
        return RespBean.success(orderDetailVo);
    }
}
