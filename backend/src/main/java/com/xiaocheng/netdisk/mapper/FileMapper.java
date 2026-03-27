package com.xiaocheng.netdisk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaocheng.netdisk.entity.File;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileMapper extends BaseMapper<File> {
}
