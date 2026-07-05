package com.erp.core.relation.service;

import com.erp.core.relation.dto.LookupResultDto;
import com.erp.core.relation.dto.RelationDefinitionDto;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface RelationService {

  RelationDefinitionDto resolveRelation(String model, String relationCode);

  List<LookupResultDto> lookup(String model, String search, int page, int size);

  List<LookupResultDto> autocomplete(String model, String search, int page, int size);

  List<Object> batchLoad(String model, List<UUID> ids, String relationCode);

  void validateRelations(String model, Map<String, Object> payload);

  void saveRelations(String model, UUID recordId, Map<String, Object> data);
}
