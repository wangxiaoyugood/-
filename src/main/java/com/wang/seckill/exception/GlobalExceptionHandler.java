package com.wang.seckill.exception;


import com.wang.seckill.vo.RespBean;
import com.wang.seckill.vo.RespBeanEnum;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;



//@RestControllerAdvice
//public class GlobalExceptionHandler {
//
//    @ExceptionHandler(Exception.class)
//    public RespBean ExceptionHandler(Exception e){
//        if(e instanceof GlobalException){
//            GlobalException ex = (GlobalException) e;
//            return RespBean.error(ex.getRespBeanEnum());
//        }else if(e instanceof BindException){
//            BindException e1 = (BindException) e;
//            RespBean error = RespBean.error(RespBeanEnum.BIND_ERROR);
//            error.setMessage("参数校验异常："+e1.getBindingResult().getAllErrors().get(0).getDefaultMessage());
//            return error;
//        }
//        return RespBean.error(RespBeanEnum.ERROR);
//    }
//
//
//}
