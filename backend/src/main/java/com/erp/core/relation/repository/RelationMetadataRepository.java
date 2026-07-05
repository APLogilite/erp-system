package com.erp.core.relation.repository;

import com.erp.core.relation.dto.RelationDefinitionDto;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class RelationMetadataRepository {

  public RelationDefinitionDto findRelation(String model, String relationCode) {
    // TODO: Replace with metadata registry lookup and dynamic relation definitions.
    throw new UnsupportedOperationException("Relation metadata repository is not implemented yet.");
  }
}
