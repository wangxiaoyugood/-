package com.wang.seckill.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class RespBean {

    private long code;
    private String message;
    private Object object;

    public static RespBean success(){
        return new RespBean(RespBeanEnum.SUCCESS.getCode(),
                RespBeanEnum.SUCCESS.getMessage(),null);
    }

    public static RespBean success(Object o){
        return new RespBean(RespBeanEnum.SUCCESS.getCode(),
                RespBeanEnum.SUCCESS.getMessage(),o);
    }

    public static RespBean error(RespBeanEnum respBeanEnum){
        return new RespBean(respBeanEnum.getCode(),
                respBeanEnum.getMessage(),null);
    }

    public static RespBean error(RespBeanEnum respBeanEnum, Object o){
        return new RespBean(respBeanEnum.getCode(),
                respBeanEnum.getMessage(),o);
    }




}
