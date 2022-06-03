package com.wang.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wang.seckill.mapper.SeckillOrderMapper;
import com.wang.seckill.pojo.SeckillOrder;
import com.wang.seckill.pojo.User;
import com.wang.seckill.service.SeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wang
 * @since 2022-05-11
 */
@Service
public class SeckillOrderServiceImpl extends ServiceImpl<SeckillOrderMapper, SeckillOrder> implements SeckillOrderService {

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    //获取秒杀结果
    //orderId:成功、-1失败、0排队中
    @Override
    public Long getResult(User user, Long goodsId) {

        System.out.println("user_id:"+user.getId());
        System.out.println("goods_id:"+goodsId);
        SeckillOrder seckillOrder = seckillOrderMapper.selectOne(new QueryWrapper<SeckillOrder>().eq("goods_id", goodsId));
        //System.out.println(seckillOrder.toString());
        if(seckillOrder!=null) {

            return seckillOrder.getOrderId();
        }else if(redisTemplate.hasKey("isStockEmpty:"+goodsId)){
            System.out.println("卖完了");
            return -1L;
        }else{
            System.out.println("我还在等");
            return 0L;
        }

    }
}
