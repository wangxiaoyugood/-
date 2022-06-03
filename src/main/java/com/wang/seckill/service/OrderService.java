package com.wang.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wang.seckill.pojo.Order;
import com.wang.seckill.pojo.User;
import com.wang.seckill.vo.GoodsVo;
import com.wang.seckill.vo.OrderDetailVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wang
 * @since 2022-05-11
 */
public interface OrderService extends IService<Order> {

    Order seckill(User user, GoodsVo goodsVo);

    OrderDetailVo detail(Long orderId);

    String createPath(User user, Long goodsId);

    boolean checkPath(User user, Long goodsId, String path);

    boolean checkCaptcha(User user, Long goodsId, String captcha);
}

