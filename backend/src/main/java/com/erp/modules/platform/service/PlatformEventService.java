package com.erp.modules.platform.service;

import com.erp.modules.platform.dto.PlatformEventResponse;
import com.erp.modules.platform.entity.PlatformEvent;
import com.erp.modules.platform.repository.PlatformEventRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlatformEventService {

  private final PlatformEventRepository platformEventRepository;

  public PlatformEventService(PlatformEventRepository platformEventRepository) {
    this.platformEventRepository = platformEventRepository;
  }

  @Transactional
  public PlatformEventResponse publish(String eventType, String sourceModule,
                                      String sourceId, String payload) {
    PlatformEvent event = new PlatformEvent();
    event.setEventType(eventType);
    event.setSourceModule(sourceModule);
    event.setSourceId(sourceId);
    event.setPayload(payload);
    event.setStatus("PUBLISHED");
    event.setOccurredAt(LocalDateTime.now());
    PlatformEvent saved = platformEventRepository.save(event);
    return toResponse(saved);
  }

  public List<PlatformEventResponse> getBySourceModule(String sourceModule) {
    return platformEventRepository.findBySourceModuleOrderByOccurredAtDesc(sourceModule)
        .stream().map(this::toResponse).collect(Collectors.toList());
  }

  public List<PlatformEventResponse> getByEventType(String eventType) {
    return platformEventRepository.findByEventTypeOrderByOccurredAtDesc(eventType)
        .stream().map(this::toResponse).collect(Collectors.toList());
  }

  public List<PlatformEventResponse> getAll() {
    return platformEventRepository.findAllByOrderByOccurredAtDesc()
        .stream().map(this::toResponse).collect(Collectors.toList());
  }

  public PlatformEventResponse getById(UUID id) {
    PlatformEvent event = platformEventRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Event not found: " + id));
    return toResponse(event);
  }

  private PlatformEventResponse toResponse(PlatformEvent event) {
    PlatformEventResponse r = new PlatformEventResponse();
    r.setId(event.getId());
    r.setEventType(event.getEventType());
    r.setSourceModule(event.getSourceModule());
    r.setSourceId(event.getSourceId());
    r.setPayload(event.getPayload());
    r.setStatus(event.getStatus());
    r.setOccurredAt(event.getOccurredAt());
    return r;
  }
}
