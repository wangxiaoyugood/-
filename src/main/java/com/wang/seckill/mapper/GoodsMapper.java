package com.wang.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wang.seckill.pojo.Goods;
import com.wang.seckill.vo.GoodsVo;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wang
 * @since 2022-05-11
 */


public interface GoodsMapper extends BaseMapper<Goods> {

    //获取商品页面
    List<GoodsVo> findGoodsVo();

    //获取商品详情
    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
