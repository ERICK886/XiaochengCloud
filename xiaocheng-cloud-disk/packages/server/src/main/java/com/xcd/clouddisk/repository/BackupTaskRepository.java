package com.xcd.clouddisk.repository;

import com.xcd.clouddisk.entity.BackupTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BackupTaskRepository extends JpaRepository<BackupTask, String> {
    List<BackupTask> findByUserId(String userId);
    List<BackupTask> findByUserIdAndIsEnabled(String userId, Boolean isEnabled);
}
