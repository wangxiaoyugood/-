package com.wang.seckill.controller;


import com.wang.seckill.pojo.User;
import com.wang.seckill.rabbitmq.MQSender;
import com.wang.seckill.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wang
 * @since 2022-05-09
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private MQSender mqSender;

    //用户信息测试
    @RequestMapping("/info")
    @ResponseBody
    public RespBean info(User user){
        return RespBean.success(user);
    }


//    //测试发送rabbitmq的消息
//    @RequestMapping("/mq")
//    @ResponseBody
//    public void mq(){
//        mqSender.send("hello");
//    }
//
//    @RequestMapping("/mq/direct01")
//    @ResponseBody
//    public void mq01(){
//        mqSender.send01("hello,red");
//    }
//
//    @RequestMapping("/mq/direct02")
//    @ResponseBody
//    public void mq02(){
//        mqSender.send02("hello,green");
//    }
//
//    @RequestMapping("/mq/topic01")
//    @ResponseBody
//    public void mq03(){
//        mqSender.send03("hello,topic");
//    }
//
//    @RequestMapping("/mq/topic02")
//    @ResponseBody
//    public void mq04(){
//        mqSender.send04("hello,topic");
//    }


}
