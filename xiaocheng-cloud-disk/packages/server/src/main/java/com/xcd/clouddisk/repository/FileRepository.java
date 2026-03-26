package com.xcd.clouddisk.repository;

import com.xcd.clouddisk.entity.FileEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, String> {
    
    Page<FileEntity> findByUserIdAndParentIdAndIsDeleted(String userId, String parentId, Boolean isDeleted, Pageable pageable);
    
    Page<FileEntity> findByUserIdAndIsDeleted(String userId, Boolean isDeleted, Pageable pageable);
    
    List<FileEntity> findByUserIdAndParentIdAndIsDeletedOrderByUpdatedAtDesc(String userId, String parentId, Boolean isDeleted);
    
    List<FileEntity> findByUserIdAndIsStarredAndIsDeleted(String userId, Boolean isStarred, Boolean isDeleted);
    
    @Query("SELECT f FROM FileEntity f WHERE f.userId = :userId AND f.isDeleted = false AND " +
           "(f.filename LIKE %:keyword% OR f.fileType LIKE %:keyword%)")
    Page<FileEntity> searchFiles(@Param("userId") String userId, @Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT COALESCE(SUM(f.fileSize), 0) FROM FileEntity f WHERE f.userId = :userId AND f.isDeleted = false")
    Long sumFileSizeByUserId(@Param("userId") String userId);
    
    List<FileEntity> findByIdIn(List<String> ids);
    
    long countByUserIdAndIsDeleted(String userId, Boolean isDeleted);
}
