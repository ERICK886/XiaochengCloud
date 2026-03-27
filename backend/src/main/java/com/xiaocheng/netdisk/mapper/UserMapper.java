package com.xiaocheng.netdisk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaocheng.netdisk.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
