package com.erp.modules.platform.service;

import com.erp.modules.platform.dto.ActivityEventResponse;
import com.erp.modules.platform.entity.ActivityEvent;
import com.erp.modules.platform.repository.ActivityEventRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ActivityTimelineService {

  private final ActivityEventRepository activityEventRepository;

  public ActivityTimelineService(ActivityEventRepository activityEventRepository) {
    this.activityEventRepository = activityEventRepository;
  }

  public ActivityEventResponse record(String eventType, String module, String recordId,
                                     String actor, String summary, String details) {
    ActivityEvent e = new ActivityEvent();
    e.setEventType(eventType);
    e.setModule(module);
    e.setRecordId(recordId);
    e.setActor(actor);
    e.setSummary(summary);
    e.setDetails(details);
    e.setOccurredAt(LocalDateTime.now());
    ActivityEvent saved = activityEventRepository.save(e);
    return toResponse(saved);
  }

  public List<ActivityEventResponse> getTimeline(String module, String recordId) {
    return activityEventRepository.findByModuleAndRecordIdOrderByOccurredAtDesc(module, recordId)
        .stream().map(this::toResponse).collect(Collectors.toList());
  }

  public List<ActivityEventResponse> getModuleTimeline(String module) {
    return activityEventRepository.findByModuleOrderByOccurredAtDesc(module)
        .stream().map(this::toResponse).collect(Collectors.toList());
  }

  public List<ActivityEventResponse> getAllTimeline() {
    return activityEventRepository.findAllByOrderByOccurredAtDesc()
        .stream().map(this::toResponse).collect(Collectors.toList());
  }

  private ActivityEventResponse toResponse(ActivityEvent e) {
    ActivityEventResponse r = new ActivityEventResponse();
    r.setId(e.getId());
    r.setEventType(e.getEventType());
    r.setModule(e.getModule());
    r.setRecordId(e.getRecordId());
    r.setActor(e.getActor());
    r.setSummary(e.getSummary());
    r.setDetails(e.getDetails());
    r.setOccurredAt(e.getOccurredAt());
    return r;
  }
}
