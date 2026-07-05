package com.erp.core.relation.mapper;

import com.erp.core.relation.dto.LookupResultDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class RelationMapper {

  public List<LookupResultDto> toLookupResults(List<Object> entities, String valueField, String displayField) {
    return entities.stream()
        .map(entity -> new LookupResultDto(UUID.randomUUID(), "", ""))
        .collect(Collectors.toList());
  }
}
