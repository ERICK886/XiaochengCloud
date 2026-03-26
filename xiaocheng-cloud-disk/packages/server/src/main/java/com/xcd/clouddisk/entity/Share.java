package com.xcd.clouddisk.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "shares")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Share {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "file_id", nullable = false)
    private String fileId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "share_type", nullable = false)
    private String shareType = "public"; // public, password, private

    @Column(name = "share_url", unique = true)
    private String shareUrl;

    @Column(name = "share_password")
    private String sharePassword;

    private String permission = "view"; // view, download

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "view_count")
    private Integer viewCount = 0;

    @Column(name = "download_count")
    private Integer downloadCount = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
