package com.xiaocheng.netdisk.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xiaocheng.netdisk.api.dto.BackupSettingDTO;
import com.xiaocheng.netdisk.common.enums.ResponseCode;
import com.xiaocheng.netdisk.common.exception.BusinessException;
import com.xiaocheng.netdisk.dal.entity.BackupTask;
import com.xiaocheng.netdisk.dal.mapper.BackupTaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BackupService {

    private final BackupTaskMapper backupTaskMapper;

    @Transactional
    public BackupTask createBackupTask(Long userId, BackupSettingDTO dto) {
        BackupTask task = new BackupTask();
        task.setUserId(userId);
        task.setTaskType(dto.getTaskType());
        task.setSourcePath(dto.getSourcePath());
        task.setTargetPath(dto.getTargetPath());
        task.setBackupMode(dto.getBackupMode() != null ? dto.getBackupMode() : 1);
        task.setQualityMode(dto.getQualityMode() != null ? dto.getQualityMode() : 1);
        task.setStatus(0);
        task.setLastBackupTime(new Date());
        backupTaskMapper.insert(task);
        return task;
    }

    public List<BackupTask> getBackupTaskList(Long userId) {
        QueryWrapper<BackupTask> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.orderBy(true, false, "create_time");
        return backupTaskMapper.selectList(wrapper);
    }

    public BackupTask getBackupTaskById(Long taskId) {
        return backupTaskMapper.selectById(taskId);
    }

    @Transactional
    public void updateBackupTask(Long userId, Long taskId, BackupSettingDTO dto) {
        BackupTask task = backupTaskMapper.selectById(taskId);
        if (task == null || !task.getUserId().equals(userId)) {
            throw new BusinessException(ResponseCode.NOT_FOUND.getCode(), "备份任务不存在");
        }
        if (dto.getSourcePath() != null) {
            task.setSourcePath(dto.getSourcePath());
        }
        if (dto.getTargetPath() != null) {
            task.setTargetPath(dto.getTargetPath());
        }
        if (dto.getBackupMode() != null) {
            task.setBackupMode(dto.getBackupMode());
        }
        if (dto.getQualityMode() != null) {
            task.setQualityMode(dto.getQualityMode());
        }
        backupTaskMapper.updateById(task);
    }

    @Transactional
    public void updateBackupTaskStatus(Long taskId, Integer status) {
        BackupTask task = backupTaskMapper.selectById(taskId);
        if (status == 1) {
            task.setLastBackupTime(new Date());
        }
        task.setStatus(status);
        backupTaskMapper.updateById(task);
    }

    @Transactional
    public void deleteBackupTask(Long userId, Long taskId) {
        BackupTask task = backupTaskMapper.selectById(taskId);
        if (task == null || !task.getUserId().equals(userId)) {
            throw new BusinessException(ResponseCode.NOT_FOUND.getCode(), "备份任务不存在");
        }
        task.setStatus(3);
        backupTaskMapper.updateById(task);
    }
}
