package com.wang.seckill.controller;


import com.wang.seckill.pojo.User;
import com.wang.seckill.service.GoodsService;
import com.wang.seckill.service.UserService;
import com.wang.seckill.vo.DetailVo;
import com.wang.seckill.vo.GoodsVo;
import com.wang.seckill.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.spring5.view.reactive.ThymeleafReactiveViewResolver;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/goods")
public class GoodsController {


    @Autowired
    private UserService userService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ThymeleafViewResolver thymeleaf;

    //跳转到商品列表页
    @RequestMapping(value="/toList",produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toList(User user, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model, @CookieValue("userTicket") String ticket){
        if(StringUtils.isEmpty(ticket)){
            return "login";
        }
        //User user = (User) session.getAttribute(ticket);
//        User user = userService.getUserByCookie(ticket, request, response);
//        if(user==null) return "login";

        model.addAttribute("user", user);
        model.addAttribute("goodsList", goodsService.findGoodsVo());

        //redis中获取页面，如果不为空，直接返回页面
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodsList");
        if(!StringUtils.isEmpty(html)){
            return html;
        }

        //如果获取页面为空，手动渲染
        WebContext webContext = new WebContext(request,response,
                request.getServletContext(),request.getLocale(),model.asMap());
        html = thymeleaf.getTemplateEngine().process("goodsList", webContext);
        if(!StringUtils.isEmpty(html)){
            valueOperations.set("goodsList",html, 60, TimeUnit.SECONDS);
        }

        //return "goodsList";
        return html;
    }

    //跳转到商品详情页
    @RequestMapping(value = "/toDetail/{goodsId}", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toDetail(HttpServletRequest request, HttpServletResponse response, Model model, @CookieValue("userTicket") String ticket, @PathVariable Long goodsId){
        User user = userService.getUserByCookie(ticket, request, response);
        model.addAttribute("user", user);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date now = new Date();
        int secKillStatus = 0;
        int remainSeconds = -1;  //倒计时
        //秒杀还未开始
        if(now.before(startDate)) {
            secKillStatus=0;
            remainSeconds = (int)(startDate.getTime()-now.getTime())/1000;
        }
        //秒杀已结束
        else if(now.after(endDate)) secKillStatus=2;
        //正在进行中
        else {
            secKillStatus=1;
            remainSeconds=0;
        }

        model.addAttribute("goods", goodsVo);
        model.addAttribute("secKillStatus", secKillStatus);
        model.addAttribute("remainSeconds",remainSeconds);
        System.out.println(goodsId);

        //redis中获取页面，如果不为空，直接返回页面
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodsDetail:" + goodsId);
        if(!StringUtils.isEmpty(html)){
            valueOperations.set("goodsList",html, 60, TimeUnit.SECONDS);
        }

        //如果获取页面为空，手动渲染
        WebContext webContext = new WebContext(request,response,
                request.getServletContext(),request.getLocale(),model.asMap());
        html = thymeleaf.getTemplateEngine().process("goodsDetail", webContext);
        if(!StringUtils.isEmpty(html)){
            valueOperations.set("goodsDetail:"+goodsId, html, 60, TimeUnit.SECONDS);
        }

        return html;
    }



    //跳转到商品详情页
    @RequestMapping("/toDetail/{goodsId}")
    @ResponseBody
    public RespBean toDetail2(Model model, @PathVariable Long goodsId, User user){
//        User user = userService.getUserByCookie(ticket, request, response);
        model.addAttribute("user", user);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date now = new Date();
        int secKillStatus = 0;
        int remainSeconds = -1;  //倒计时
        //秒杀还未开始
        if(now.before(startDate)) {
            secKillStatus=0;
            remainSeconds = (int)(startDate.getTime()-now.getTime())/1000;
        }
        //秒杀已结束
        else if(now.after(endDate)) secKillStatus=2;
            //正在进行中
        else {
            secKillStatus=1;
            remainSeconds=0;
        }

        DetailVo detailVo = new DetailVo();
        detailVo.setUser(user);
        detailVo.setGoodsVo(goodsVo);
        detailVo.setSecKillStatus(secKillStatus);
        detailVo.setRemainSeconds(remainSeconds);

//        model.addAttribute("goods", goodsVo);
//        model.addAttribute("secKillStatus", secKillStatus);
//        model.addAttribute("remainSeconds",remainSeconds);
//        System.out.println(goodsId);

//        //redis中获取页面，如果不为空，直接返回页面
//        ValueOperations valueOperations = redisTemplate.opsForValue();
//        String html = (String) valueOperations.get("goodsDetail:" + goodsId);
//        if(!StringUtils.isEmpty(html)){
//            valueOperations.set("goodsList",html, 60, TimeUnit.SECONDS);
//        }

//        //如果获取页面为空，手动渲染
//        WebContext webContext = new WebContext(request,response,
//                request.getServletContext(),request.getLocale(),model.asMap());
//        html = thymeleaf.getTemplateEngine().process("goodsDetail", webContext);
//        if(!StringUtils.isEmpty(html)){
//            valueOperations.set("goodsDetail:"+goodsId, html, 60, TimeUnit.SECONDS);
//        }

        return RespBean.success(detailVo);
    }

}
