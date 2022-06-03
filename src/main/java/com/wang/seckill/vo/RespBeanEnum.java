package com.wang.seckill.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RespBeanEnum {

    SUCCESS(200, "SUCCESS"),
    ERROR(500, "服务端异常"),
    LOGIN_ERROR(500210, "用户名或密码错误"),
    MOBILE_ERROR(500211, "手机号错误"),
    BIND_ERROR(500212, "参数校验异常"),
    //空库存
    EMPTY_STOCK(500500, "库存不足"),
    //重复购买
    REPEATE_ERROR(500501, "该商品每人限购一件"),
    //手机用户不存在
    MOBILE_NOT_EXIST(500213, "手机号码不存在"),
    Err_PASSWORD(500214, "更新密码失败"),
    //订单
    ORDER_NOT_EXIST(500300, "订单信息不存在"),

    REQUEST_ILLEGAL(500502, "请求非法，请重新尝试"),

    ERROR_CAPTCHA(500503, "验证码错误，请重新输入")
    ;

    private final Integer code;
    private final String message;



}
