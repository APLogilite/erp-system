package com.erp.modules.platform.repository;

import com.erp.modules.platform.entity.Document;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends JpaRepository<Document, UUID> {
  List<Document> findByModuleAndRecordId(String module, String recordId);
  List<Document> findByCategory(String category);
  List<Document> findByOwner(String owner);
}
