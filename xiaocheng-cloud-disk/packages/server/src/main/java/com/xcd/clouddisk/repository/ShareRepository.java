package com.xcd.clouddisk.repository;

import com.xcd.clouddisk.entity.Share;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ShareRepository extends JpaRepository<Share, String> {
    Page<Share> findByUserId(String userId, Pageable pageable);
    Optional<Share> findByShareUrl(String shareUrl);
    long countByUserId(String userId);
}
