package com.wang.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wang.seckill.pojo.SeckillOrder;
import com.wang.seckill.pojo.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wang
 * @since 2022-05-11
 */
public interface SeckillOrderService extends IService<SeckillOrder> {

    Long getResult(User user, Long goodsId);
}
