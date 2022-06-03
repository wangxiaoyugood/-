package com.wang.seckill.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wang.seckill.exception.GlobalException;
import com.wang.seckill.pojo.Order;
import com.wang.seckill.pojo.SeckillMessage;
import com.wang.seckill.pojo.SeckillOrder;
import com.wang.seckill.pojo.User;
import com.wang.seckill.rabbitmq.MQSender;
import com.wang.seckill.service.GoodsService;
import com.wang.seckill.service.OrderService;
import com.wang.seckill.service.SeckillOrderService;
import com.wang.seckill.service.UserService;
import com.wang.seckill.utils.JsonUtil;
import com.wang.seckill.vo.GoodsVo;
import com.wang.seckill.vo.RespBean;
import com.wang.seckill.vo.RespBeanEnum;
import com.wf.captcha.ArithmeticCaptcha;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RequestMapping("/seckill")
@Controller
public class SecKillController implements InitializingBean {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private UserService userService;

    @Autowired
    private SeckillOrderService seckillOrderService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MQSender mqSender;


    private Map<Long, Boolean> EmptyStockMap= new HashMap<>();

//    @RequestMapping("/doSeckill1")
//    public String deSecKill2(HttpServletRequest request, HttpServletResponse response, Model model,
//                            @CookieValue("userTicket") String ticket, Long goodsId){
//
//        User user = userService.getUserByCookie(ticket, request, response);
//        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
//        //判断库存
//        if(goodsVo.getStockCount()<1){
//            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
//            return "secKillFail";
//        }
//        //验证是否重复购买
//        SeckillOrder one = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>(
//        ).eq("user_id", user.getId()).eq("goods_id", goodsId));
//        if(one!=null) {
//            model.addAttribute("errmsg", RespBeanEnum.REPEATE_ERROR);
//            return "secKillFail";
//        }
//        //此时用户可以秒杀购买
//        Order order=orderService.seckill(user, goodsVo);
//        model.addAttribute("order",order);
//        model.addAttribute("goods",goodsVo);
//        return "orderDetail";
//    }


    @RequestMapping(value = "/{path}/doSeckill", method = RequestMethod.POST)
    @ResponseBody
    public RespBean deSecKill(@PathVariable String path, HttpServletRequest request, HttpServletResponse response, @CookieValue("userTicket") String ticket, Long goodsId){

        User user = userService.getUserByCookie(ticket, request, response);

        if (user == null) {
            return RespBean.error(RespBeanEnum.ERROR);
        }

        //获取redis模板
        ValueOperations valueOperations = redisTemplate.opsForValue();

        boolean check = orderService.checkPath(user, goodsId,path);
        //redis里不包含该路径
        if(!check){
            //请求非法问题
            return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
        }

        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        /*
        //判断库存
        if(goodsVo.getStockCount()<1){
            //model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //验证是否重复购买
//        SeckillOrder one = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>(
//        ).eq("user_id", user.getId()).eq("goods_id", goodsId));

        SeckillOrder seckillOrder =(SeckillOrder)redisTemplate.opsForValue().get("order:"+user.getId()+":"+goodsId);
        if(seckillOrder!=null) {
            //model.addAttribute("errmsg", RespBeanEnum.REPEATE_ERROR);
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }

        //此时用户可以秒杀购买
        Order order=orderService.seckill(user, goodsVo);
        //model.addAttribute("order",order);
        //model.addAttribute("goods",goodsVo);
        return RespBean.success(order);

         */


        //是否重复购买
        SeckillOrder seckillOrder =(SeckillOrder)redisTemplate.opsForValue().get("order:"+user.getId()+":"+goodsId);
        if(seckillOrder!=null) {
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }

        //通过内存标记，减少Redis的访问
        if(EmptyStockMap.get(goodsId)){
            //为true则表示库存以空
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }

        //递减之后的库存
        //redis预减库存
        Long stock = valueOperations.decrement("seckillGoods:" + goodsId);
        if(stock<0){
            //这一步防止变成-1
            EmptyStockMap.put(goodsId, true);
            valueOperations.increment("seckillGoods:" + goodsId);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //消息队列发送信息
        SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
        mqSender.sendSeckillMessage(JsonUtil.object2JsonStr(seckillMessage));


        //Order order=orderService.seckill(user, goodsVo);

        return RespBean.success(0);


    }


    @RequestMapping(value = "/getResult", method = RequestMethod.GET)
    @ResponseBody
    public RespBean getResult(User user, Long goodsId){
        if(user==null) return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        System.out.println(goodsId);
        Long orderId = seckillOrderService.getResult(user, goodsId);
        System.out.println("orderId是"+orderId);
        return RespBean.success(orderId);

    }


    @RequestMapping(value="/captcha", method = RequestMethod.GET)
    public void verifyCode(User user, Long goodsId, HttpServletResponse response){
        if(user==null || goodsId<0){
            throw new GlobalException(RespBeanEnum.REQUEST_ILLEGAL);
        }

        //设置请求头为输出图片的类型
        response.setContentType("image/jpg");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        //生成验证码, 结果放入redis
        ArithmeticCaptcha arithmeticCaptcha = new ArithmeticCaptcha(130, 32, 3);
        redisTemplate.opsForValue().set("captcha:"+goodsId,arithmeticCaptcha.text(),300, TimeUnit.SECONDS);
        try {
            arithmeticCaptcha.out(response.getOutputStream());
        } catch (IOException e) {
            System.out.println("验证码有点问题");
            e.printStackTrace();
        }

    }


    //获取秒杀地址
    @RequestMapping(value = "/path", method = RequestMethod.GET)
    @ResponseBody
    public RespBean getPath(User user, Long goodsId, String captcha){
        if(user==null){
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }
        boolean check = orderService.checkCaptcha(user, goodsId, captcha);
        if(!check){
            return RespBean.error(RespBeanEnum.ERROR_CAPTCHA);
        }


        String path = orderService.createPath(user, goodsId);
        return RespBean.success(path);
    }




    //初始化时可以加载的一些方法
    //初始化时，把商品数量加载到redis中去
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> list = goodsService.findGoodsVo();
        if(CollectionUtils.isEmpty(list)){
            return;
        }
        list.forEach(goodsVo ->{
            redisTemplate.opsForValue().set("seckillGoods:"+goodsVo.getId(),goodsVo.getStockCount());
            EmptyStockMap.put(goodsVo.getId(), false);
        });

    }
}
