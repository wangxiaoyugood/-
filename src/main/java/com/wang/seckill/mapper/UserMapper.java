package com.wang.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wang.seckill.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wang
 * @since 2022-05-09
 */

@Mapper
public interface UserMapper extends BaseMapper<User> {

}
