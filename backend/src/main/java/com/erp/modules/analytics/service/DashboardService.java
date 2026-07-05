package com.erp.modules.analytics.service;

import com.erp.common.base.BaseService;
import com.erp.modules.analytics.dto.DashboardRequest;
import com.erp.modules.analytics.dto.DashboardRequest.WidgetRequest;
import com.erp.modules.analytics.dto.DashboardResponse;
import com.erp.modules.analytics.dto.WidgetResponse;
import com.erp.modules.analytics.entity.Dashboard;
import com.erp.modules.analytics.entity.DashboardWidget;
import com.erp.modules.analytics.repository.DashboardRepository;
import com.erp.modules.analytics.repository.DashboardWidgetRepository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardService extends BaseService<Dashboard> {

  private final DashboardRepository dashboardRepository;
  private final DashboardWidgetRepository widgetRepository;

  public DashboardService(DashboardRepository dashboardRepository,
                          DashboardWidgetRepository widgetRepository) {
    this.dashboardRepository = dashboardRepository;
    this.widgetRepository = widgetRepository;
  }

  @Override
  protected JpaRepository<Dashboard, UUID> getRepository() {
    return dashboardRepository;
  }

  @Override
  protected void beforeCreate(Dashboard entity) {
    if (entity.getDashboardCode() == null || entity.getDashboardCode().trim().isEmpty()) {
      throw new IllegalArgumentException("Dashboard code is required");
    }
    if (entity.getIsDefault() == null) {
      entity.setIsDefault(false);
    }
  }

  @Transactional
  public UUID createWithWidgets(DashboardRequest request) {
    Dashboard dashboard = new Dashboard();
    dashboard.setDashboardCode(request.getDashboardCode());
    dashboard.setName(request.getName());
    dashboard.setDescription(request.getDescription());
    dashboard.setLayout(request.getLayout());
    dashboard.setIsDefault(request.getIsDefault() != null ? request.getIsDefault() : false);
    dashboard.setRoles(request.getRoles());

    beforeCreate(dashboard);
    Dashboard saved = dashboardRepository.save(dashboard);

    if (request.getWidgets() != null) {
      for (WidgetRequest wr : request.getWidgets()) {
        DashboardWidget w = new DashboardWidget();
        w.setDashboardId(saved.getId());
        w.setWidgetType(wr.getWidgetType());
        w.setTitle(wr.getTitle());
        w.setWidgetConfig(wr.getWidgetConfig());
        w.setPositionX(wr.getPositionX() != null ? wr.getPositionX() : 0);
        w.setPositionY(wr.getPositionY() != null ? wr.getPositionY() : 0);
        w.setWidth(wr.getWidth() != null ? wr.getWidth() : 6);
        w.setHeight(wr.getHeight() != null ? wr.getHeight() : 4);
        w.setDataSource(wr.getDataSource());
        w.setRefreshInterval(wr.getRefreshInterval() != null ? wr.getRefreshInterval() : 0);
        widgetRepository.save(w);
      }
    }

    return saved.getId();
  }

  public DashboardResponse getDashboardWithWidgets(UUID id) {
    Dashboard dashboard = findByIdOrThrow(id);
    return toResponse(dashboard);
  }

  public List<DashboardResponse> getAllWithWidgets() {
    return findAll().stream().map(this::toResponse).collect(Collectors.toList());
  }

  public List<DashboardWidget> getWidgets(UUID dashboardId) {
    return widgetRepository.findByDashboardIdOrderByPositionY(dashboardId);
  }

  public DashboardResponse toResponse(Dashboard d) {
    DashboardResponse r = new DashboardResponse();
    r.setId(d.getId());
    r.setDashboardCode(d.getDashboardCode());
    r.setName(d.getName());
    r.setDescription(d.getDescription());
    r.setLayout(d.getLayout());
    r.setIsDefault(d.getIsDefault());
    r.setRoles(d.getRoles());
    r.setIsActive(d.getIsActive());
    r.setCreatedAt(d.getCreatedAt());
    r.setUpdatedAt(d.getUpdatedAt());
    try {
      List<WidgetResponse> widgets = widgetRepository
          .findByDashboardIdOrderByPositionY(d.getId()).stream()
          .map(this::toWidgetResponse).collect(Collectors.toList());
      r.setWidgets(widgets);
    } catch (Exception e) {
      r.setWidgets(List.of());
    }
    return r;
  }

  private WidgetResponse toWidgetResponse(DashboardWidget w) {
    WidgetResponse r = new WidgetResponse();
    r.setId(w.getId());
    r.setDashboardId(w.getDashboardId());
    r.setWidgetType(w.getWidgetType());
    r.setTitle(w.getTitle());
    r.setWidgetConfig(w.getWidgetConfig());
    r.setPositionX(w.getPositionX());
    r.setPositionY(w.getPositionY());
    r.setWidth(w.getWidth());
    r.setHeight(w.getHeight());
    r.setDataSource(w.getDataSource());
    r.setRefreshInterval(w.getRefreshInterval());
    return r;
  }
}
