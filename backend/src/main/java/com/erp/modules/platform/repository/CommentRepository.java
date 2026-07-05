package com.erp.modules.platform.repository;

import com.erp.modules.platform.entity.Comment;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
  List<Comment> findByModuleAndRecordIdOrderByCreatedAtAsc(String module, String recordId);
  List<Comment> findByParentIdOrderByCreatedAtAsc(String parentId);
  List<Comment> findByAuthor(String author);
}
