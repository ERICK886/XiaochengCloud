package com.xcd.clouddisk.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "backup_tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BackupTask {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    private String name;

    @Column(name = "backup_type")
    private String backupType; // photo, video, folder, custom

    @Column(name = "backup_path")
    private String backupPath;

    @Column(name = "local_path")
    private String localPath;

    @Column(name = "is_enabled")
    private Boolean isEnabled = true;

    @Column(name = "last_backup_at")
    private LocalDateTime lastBackupAt;

    private String status = "idle"; // idle, running, paused, error, completed

    @Column(name = "total_files")
    private Integer totalFiles = 0;

    @Column(name = "completed_files")
    private Integer completedFiles = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
