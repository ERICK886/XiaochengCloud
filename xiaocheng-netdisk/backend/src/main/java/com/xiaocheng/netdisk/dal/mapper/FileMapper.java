package com.xiaocheng.netdisk.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaocheng.netdisk.dal.entity.File;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileMapper extends BaseMapper<File> {
}
