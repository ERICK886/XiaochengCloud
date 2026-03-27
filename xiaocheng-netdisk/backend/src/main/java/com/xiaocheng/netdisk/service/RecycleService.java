package com.xiaocheng.netdisk.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xiaocheng.netdisk.common.enums.ResponseCode;
import com.xiaocheng.netdisk.common.exception.BusinessException;
import com.xiaocheng.netdisk.dal.entity.File;
import com.xiaocheng.netdisk.dal.entity.Recycle;
import com.xiaocheng.netdisk.dal.entity.User;
import com.xiaocheng.netdisk.dal.mapper.FileMapper;
import com.xiaocheng.netdisk.dal.mapper.RecycleMapper;
import com.xiaocheng.netdisk.dal.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecycleService {

    private final RecycleMapper recycleMapper;
    private final FileMapper fileMapper;
    private final UserMapper userMapper;

    public List<Recycle> getRecycleList(Long userId) {
        QueryWrapper<Recycle> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.orderBy(true, false, "delete_time");
        return recycleMapper.selectList(wrapper);
    }

    @Transactional
    public void restoreFile(Long userId, Long recycleId) {
        Recycle recycle = recycleMapper.selectById(recycleId);
        if (recycle == null || !recycle.getUserId().equals(userId)) {
            throw new BusinessException(ResponseCode.NOT_FOUND.getCode(), "回收站记录不存在");
        }

        File file = fileMapper.selectById(recycle.getFileId());
        if (file == null) {
            throw new BusinessException(ResponseCode.NOT_FOUND.getCode(), "原文件不存在");
        }

        file.setIsDeleted(0);
        file.setDeleteTime(null);
        fileMapper.updateById(file);

        User user = userMapper.selectById(userId);
        user.setUsedCapacity(user.getUsedCapacity() + file.getFileSize());
        userMapper.updateById(user);

        recycleMapper.deleteById(recycleId);
    }

    @Transactional
    public void permanentlyDelete(Long userId, Long recycleId) {
        Recycle recycle = recycleMapper.selectById(recycleId);
        if (recycle == null || !recycle.getUserId().equals(userId)) {
            throw new BusinessException(ResponseCode.NOT_FOUND.getCode(), "回收站记录不存在");
        }

        fileMapper.deleteById(recycle.getFileId());
        recycleMapper.deleteById(recycleId);
    }

    @Transactional
    public void emptyRecycle(Long userId) {
        QueryWrapper<Recycle> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        List<Recycle> recycles = recycleMapper.selectList(wrapper);

        for (Recycle recycle : recycles) {
            fileMapper.deleteById(recycle.getFileId());
        }
        recycleMapper.delete(wrapper);
    }

    public void cleanExpiredRecycle() {
        QueryWrapper<Recycle> wrapper = new QueryWrapper<>();
        wrapper.le("expire_time", new Date());
        List<Recycle> expiredRecycles = recycleMapper.selectList(wrapper);

        for (Recycle recycle : expiredRecycles) {
            fileMapper.deleteById(recycle.getFileId());
            recycleMapper.deleteById(recycle.getId());
        }
    }
}
