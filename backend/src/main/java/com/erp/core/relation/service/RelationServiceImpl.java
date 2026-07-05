package com.erp.core.relation.service;

import com.erp.common.api.ApiResponse;
import com.erp.core.relation.dto.LookupResultDto;
import com.erp.core.relation.dto.RelationDefinitionDto;
import com.erp.core.relation.enums.LoadingStrategy;
import com.erp.core.relation.enums.RelationType;
import com.erp.core.relation.repository.RelationMetadataRepository;
import com.erp.core.relation.resolver.RelationResolver;
import com.erp.core.relation.validator.RelationValidator;
import com.erp.core.relation.mapper.RelationMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class RelationServiceImpl implements RelationService {

  private final RelationMetadataRepository metadataRepository;
  private final RelationResolver relationResolver;
  private final RelationValidator relationValidator;
  private final RelationMapper relationMapper;

  public RelationServiceImpl(
      RelationMetadataRepository metadataRepository,
      RelationResolver relationResolver,
      RelationValidator relationValidator,
      RelationMapper relationMapper) {
    this.metadataRepository = metadataRepository;
    this.relationResolver = relationResolver;
    this.relationValidator = relationValidator;
    this.relationMapper = relationMapper;
  }

  @Override
  public RelationDefinitionDto resolveRelation(String model, String relationCode) {
    return metadataRepository.findRelation(model, relationCode);
  }

  @Override
  public List<LookupResultDto> lookup(String model, String search, int page, int size) {
    RelationDefinitionDto relation = resolveRelation(model, "lookup");
    return relationResolver.lookup(relation, search, page, size);
  }

  @Override
  public List<LookupResultDto> autocomplete(String model, String search, int page, int size) {
    RelationDefinitionDto relation = resolveRelation(model, "autocomplete");
    return relationResolver.autocomplete(relation, search, page, size);
  }

  @Override
  public List<Object> batchLoad(String model, List<UUID> ids, String relationCode) {
    RelationDefinitionDto relation = resolveRelation(model, relationCode);
    return relationResolver.batchLoad(relation, ids);
  }

  @Override
  public void validateRelations(String model, Map<String, Object> payload) {
    relationValidator.validate(model, payload);
  }

  @Override
  @Transactional
  public void saveRelations(String model, UUID recordId, Map<String, Object> data) {
    relationResolver.save(model, recordId, data);
  }
}
