package com.erp.core.relation.resolver;

import com.erp.core.relation.dto.LookupResultDto;
import com.erp.core.relation.dto.RelationDefinitionDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class RelationResolver {

  public List<LookupResultDto> lookup(RelationDefinitionDto relation, String search, int page, int size) {
    throw new UnsupportedOperationException("Relation lookup implementation pending.");
  }

  public List<LookupResultDto> autocomplete(RelationDefinitionDto relation, String search, int page, int size) {
    throw new UnsupportedOperationException("Relation autocomplete implementation pending.");
  }

  public List<Object> batchLoad(RelationDefinitionDto relation, List<UUID> ids) {
    throw new UnsupportedOperationException("Relation batch load implementation pending.");
  }

  public void save(String model, UUID recordId, java.util.Map<String, Object> data) {
    throw new UnsupportedOperationException("Relation save implementation pending.");
  }
}
