package com.wang.seckill.vo;


import com.wang.seckill.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DetailVo {

    private User user;
    private GoodsVo goodsVo;
    private int secKillStatus;
    private int remainSeconds;


}
