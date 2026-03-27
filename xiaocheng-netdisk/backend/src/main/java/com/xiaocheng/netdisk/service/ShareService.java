package com.xiaocheng.netdisk.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xiaocheng.netdisk.api.dto.CreateShareDTO;
import com.xiaocheng.netdisk.common.enums.ResponseCode;
import com.xiaocheng.netdisk.common.exception.BusinessException;
import com.xiaocheng.netdisk.dal.entity.File;
import com.xiaocheng.netdisk.dal.entity.Share;
import com.xiaocheng.netdisk.dal.entity.ShareAccess;
import com.xiaocheng.netdisk.dal.mapper.FileMapper;
import com.xiaocheng.netdisk.dal.mapper.ShareAccessMapper;
import com.xiaocheng.netdisk.dal.mapper.ShareMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShareService {

    private final ShareMapper shareMapper;
    private final ShareAccessMapper shareAccessMapper;
    private final FileMapper fileMapper;

    @Transactional
    public Share createShare(Long userId, CreateShareDTO dto) {
        String shareCode = UUID.randomUUID().toString().replace("-", "");

        StringBuilder fileIdsBuilder = new StringBuilder();
        for (int i = 0; i < dto.getFileIds().size(); i++) {
            if (i > 0) fileIdsBuilder.append(",");
            fileIdsBuilder.append(dto.getFileIds().get(i));
        }

        Share share = new Share();
        share.setShareCode(shareCode);
        share.setUserId(userId);
        share.setFileIds(fileIdsBuilder.toString());
        share.setShareType(dto.getShareType() != null ? dto.getShareType() : 1);
        share.setPassword(dto.getPassword());
        share.setViewCount(0);
        share.setDownloadCount(0);
        share.setStatus(0);

        if (dto.getExpireDays() != null && dto.getExpireDays() > 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, dto.getExpireDays());
            share.setExpireTime(calendar.getTime());
        } else {
            share.setExpireTime(null);
        }

        shareMapper.insert(share);
        return share;
    }

    public List<Share> getShareList(Long userId) {
        QueryWrapper<Share> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("status", 0);
        wrapper.orderBy(true, false, "create_time");
        return shareMapper.selectList(wrapper);
    }

    public Share getShareByCode(String shareCode) {
        QueryWrapper<Share> wrapper = new QueryWrapper<>();
        wrapper.eq("share_code", shareCode);
        wrapper.eq("status", 0);
        Share share = shareMapper.selectOne(wrapper);

        if (share == null) {
            throw new BusinessException(ResponseCode.NOT_FOUND.getCode(), "分享不存在或已取消");
        }

        if (share.getExpireTime() != null && share.getExpireTime().before(new Date())) {
            throw new BusinessException(ResponseCode.NOT_FOUND.getCode(), "分享已过期");
        }

        return share;
    }

    public List<File> getShareFiles(String shareCode) {
        Share share = getShareByCode(shareCode);
        String[] fileIds = share.getFileIds().split(",");
        return fileIds.stream()
                .map(id -> fileMapper.selectById(Long.parseLong(id.trim())))
                .collect(java.util.stream.Collectors.toList());
    }

    @Transactional
    public Share validateSharePassword(String shareCode, String password) {
        Share share = getShareByCode(shareCode);
        if (share.getShareType() == 2 && !password.equals(share.getPassword())) {
            throw new BusinessException(ResponseCode.FAIL.getCode(), "密码错误");
        }
        return share;
    }

    @Transactional
    public void recordShareAccess(Long shareId, Long accessUserId, Integer accessType, String ipAddress, String deviceInfo) {
        ShareAccess access = new ShareAccess();
        access.setShareId(shareId);
        access.setAccessUserId(accessUserId);
        access.setAccessType(accessType);
        access.setAccessTime(new Date());
        access.setIpAddress(ipAddress);
        access.setDeviceInfo(deviceInfo);
        shareAccessMapper.insert(access);

        Share share = shareMapper.selectById(shareId);
        if (accessType == 1) {
            share.setViewCount(share.getViewCount() + 1);
        } else if (accessType == 2) {
            share.setDownloadCount(share.getDownloadCount() + 1);
        }
        shareMapper.updateById(share);
    }

    @Transactional
    public void cancelShare(Long userId, Long shareId) {
        Share share = shareMapper.selectById(shareId);
        if (share == null || !share.getUserId().equals(userId)) {
            throw new BusinessException(ResponseCode.NOT_FOUND.getCode(), "分享不存在");
        }
        share.setStatus(1);
        shareMapper.updateById(share);
    }

    public List<ShareAccess> getShareAccessList(Long shareId) {
        QueryWrapper<ShareAccess> wrapper = new QueryWrapper<>();
        wrapper.eq("share_id", shareId);
        wrapper.orderBy(true, false, "access_time");
        return shareAccessMapper.selectList(wrapper);
    }
}
