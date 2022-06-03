package com.wang.seckill.rabbitmq;


import com.wang.seckill.pojo.SeckillMessage;
import com.wang.seckill.pojo.SeckillOrder;
import com.wang.seckill.pojo.User;
import com.wang.seckill.service.GoodsService;
import com.wang.seckill.service.OrderService;
import com.wang.seckill.utils.JsonUtil;
import com.wang.seckill.vo.GoodsVo;
import com.wang.seckill.vo.RespBean;
import com.wang.seckill.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MQReceiver {

//    @RabbitListener(queues = "queue")
//    public void receive(Object msg){
//        log.info("接收消息"+msg);
//    }
//
//    @RabbitListener(queues = "queue_fanout01")
//    public void receive01(Object msg){
//        log.info("接收消息"+msg);
//    }
//
//    @RabbitListener(queues = "queue_fanout02")
//    public void receive02(Object msg){
//        log.info("接收消息"+msg);
//    }
//
//    @RabbitListener(queues = "queue_direct01")
//    public void receive03(Object msg){
//        log.info("QUEUE01接收消息"+msg);
//    }
//
//    @RabbitListener(queues = "queue_direct02")
//    public void receive04(Object msg){
//        log.info("QUEUE02接收消息"+msg);
//    }
//
//    @RabbitListener(queues = "queue_topic01")
//    public void receive05(Object msg){
//        log.info("QUEUE01接受"+msg);
//    }
//
//    @RabbitListener(queues = "queue_topic02")
//    public void receive06(Object msg){
//        log.info("Queue02接受"+msg);
//    }

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private OrderService orderService;

    //下单操作
    @RabbitListener(queues = "seckillQueue")
    public void receive(String message){
        log.info("接受到的消息："+message);
        SeckillMessage seckillMessage = JsonUtil.jsonStr2Object(message, SeckillMessage.class);
        Long goodsId = seckillMessage.getGoodsId();
        User user = seckillMessage.getUser();
        //下单
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        if(goodsVo.getStockCount()<1){
            return;
        }
        //获取redis模板
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //是否重复购买
        SeckillOrder seckillOrder =(SeckillOrder)redisTemplate.opsForValue().get("order:"+user.getId()+":"+goodsId);
        if(seckillOrder!=null) {
            return;
        }
        //没有上述问题，开始正式下单
        orderService.seckill(user, goodsVo);


    }


}
