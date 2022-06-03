package com.wang.seckill.exception;


import com.wang.seckill.vo.RespBeanEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GlobalException extends RuntimeException {
    private RespBeanEnum respBeanEnum;





}
