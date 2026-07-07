package com.erp.core.metadata.service;

import com.erp.core.metadata.entity.MetadataVersion;
import com.erp.core.metadata.repository.MetadataVersionRepository;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service that records all table schema changes (create table, add/edit/delete
 * column) in sys_metadata_version for audit and rollback purposes.
 */
@Service
public class SchemaHistoryService {

  private static final Logger log = LoggerFactory.getLogger(SchemaHistoryService.class);

  private final MetadataVersionRepository metadataVersionRepository;

  public SchemaHistoryService(MetadataVersionRepository metadataVersionRepository) {
    this.metadataVersionRepository = metadataVersionRepository;
  }

  /**
   * Records a new schema change entry for the given table.
   * The version number is auto-incremented per table.
   *
   * @param tableId             the UUID of the table definition in sys_metadata_models
   * @param changeDescription   human-readable description of the change
   * @param definitionSnapshot  snapshot of the full table definition at this point
   * @param userId              the current user ID (from SecurityContext)
   */
  @Transactional
  public void logChange(UUID tableId, String changeDescription,
                        Map<String, Object> definitionSnapshot, UUID userId) {
    int nextVersion = getLatestVersion(tableId) + 1;

    MetadataVersion version = new MetadataVersion();
    version.setVersion(nextVersion);
    version.setTableId(tableId);
    version.setDescription(changeDescription);
    version.setDefinitionSnapshot(definitionSnapshot);
    version.setChangedBy(userId);
    version.setActive(true);

    metadataVersionRepository.save(version);

    log.info("Schema change logged: tableId={}, version={}, change={}",
        tableId, nextVersion, changeDescription);
  }

  /**
   * Returns a chronological (ascending) list of all schema changes for a table.
   *
   * @param tableId the UUID of the table definition
   * @return list of MetadataVersion entries ordered by version ascending
   */
  public List<MetadataVersion> getHistory(UUID tableId) {
    return metadataVersionRepository.findByTableIdOrderByVersionAsc(tableId);
  }

  /**
   * Returns the most recent version number for a table.
   * Returns 0 if no history exists yet.
   *
   * @param tableId the UUID of the table definition
   * @return the latest version number, or 0 if no entries
   */
  public int getLatestVersion(UUID tableId) {
    Integer maxVersion = metadataVersionRepository.findMaxVersionByTableId(tableId);
    return maxVersion != null ? maxVersion : 0;
  }
}
