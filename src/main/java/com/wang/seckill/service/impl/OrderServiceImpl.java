package com.wang.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wang.seckill.exception.GlobalException;
import com.wang.seckill.mapper.OrderMapper;
import com.wang.seckill.pojo.Order;
import com.wang.seckill.pojo.SeckillGoods;
import com.wang.seckill.pojo.SeckillOrder;
import com.wang.seckill.pojo.User;
import com.wang.seckill.service.GoodsService;
import com.wang.seckill.service.OrderService;
import com.wang.seckill.service.SeckillGoodsService;
import com.wang.seckill.service.SeckillOrderService;
import com.wang.seckill.utils.MD5Util;
import com.wang.seckill.utils.UUIDUtil;
import com.wang.seckill.vo.GoodsVo;
import com.wang.seckill.vo.OrderDetailVo;
import com.wang.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wang
 * @since 2022-05-11
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private SeckillGoodsService seckillGoodsService;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private SeckillOrderService seckillOrderService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Transactional
    @Override
    public Order seckill(User user, GoodsVo goodsVo) {
        //秒杀商品表减库存
        SeckillGoods goods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>().eq("goods_id", goodsVo.getId()));
        goods.setStockCount(goods.getStockCount()-1);
        seckillGoodsService.updateById(goods);
        boolean update = seckillGoodsService.update(new UpdateWrapper<SeckillGoods>().set("stock_count",
                goods.getStockCount()).eq("id", goods.getId()).gt("stock_count", 0));
        //if(!update) return null;
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //判断是否有库存
        if(goods.getStockCount()<1){
            valueOperations.set("isStockEmpty:"+goods.getId(),"0");
            return null;
        }

        //生成订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goodsVo.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goodsVo.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(goods.getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        orderMapper.insert(order);
        //生成秒杀订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setUserId(user.getId());
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setGoodsId(goodsVo.getId());
        seckillOrderService.save(seckillOrder);

        //在redis中保存订单信息
        redisTemplate.opsForValue().set("order:"+user.getId()+":"+goods.getId(),seckillOrder);

        return order;
    }

    @Override
    public OrderDetailVo detail(Long orderId) {

        if(orderId==null){
            throw new GlobalException(RespBeanEnum.ORDER_NOT_EXIST);
        }

        Order order = orderMapper.selectById(orderId);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(order.getGoodsId());
        OrderDetailVo detailVo = new OrderDetailVo(order,goodsVo);
        return detailVo;
    }


    //获取秒杀地址
    @Override
    public String createPath(User user, Long goodsId) {

        String str = MD5Util.md5(UUIDUtil.uuid() + "123456");
        redisTemplate.opsForValue().set("seckillPath:"+goodsId,str,60, TimeUnit.SECONDS);
        return str;

    }

    //校验秒杀地址
    @Override
    public boolean checkPath(User user, Long goodsId, String path) {
        if(user==null || goodsId<0 || StringUtils.isEmpty(path)){
            return false;
        }
        String redisPath = (String) redisTemplate.opsForValue().get("seckillPath:"+goodsId);

        return path.equals(redisPath);
    }

    //校验验证码
    @Override
    public boolean checkCaptcha(User user, Long goodsId, String captcha) {
        if(StringUtils.isEmpty(captcha)){
            return false;
        }
        String s =(String) redisTemplate.opsForValue().get("captcha:" + goodsId);

        return captcha.equals(s);
    }
}
