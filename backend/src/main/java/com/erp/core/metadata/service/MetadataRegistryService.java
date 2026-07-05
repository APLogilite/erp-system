package com.erp.core.metadata.service;

import com.erp.core.metadata.entity.*;
import com.erp.core.metadata.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional(readOnly = true)
public class MetadataRegistryService {

  private final MetadataModelRepository modelRepository;
  private final MetadataViewRepository viewRepository;
  private final MetadataWorkflowRepository workflowRepository;
  private final MetadataPermissionRepository permissionRepository;
  private final MetadataVersionRepository versionRepository;

  // Thread-safe in-memory caches
  private final Map<String, MetadataModel> modelCache = new ConcurrentHashMap<>();
  private final Map<String, MetadataView> viewCache = new ConcurrentHashMap<>();
  private final Map<String, MetadataWorkflow> workflowCache = new ConcurrentHashMap<>();
  private final Map<String, List<MetadataPermission>> permissionCache = new ConcurrentHashMap<>();

  private volatile Integer activeVersion = 0;

  public MetadataRegistryService(
      MetadataModelRepository modelRepository,
      MetadataViewRepository viewRepository,
      MetadataWorkflowRepository workflowRepository,
      MetadataPermissionRepository permissionRepository,
      MetadataVersionRepository versionRepository) {
    this.modelRepository = modelRepository;
    this.viewRepository = viewRepository;
    this.workflowRepository = workflowRepository;
    this.permissionRepository = permissionRepository;
    this.versionRepository = versionRepository;
  }

  // Load and cache a model
  public Optional<MetadataModel> getModel(String name) {
    return Optional.ofNullable(modelCache.computeIfAbsent(name, key -> 
        modelRepository.findByName(key).orElse(null)));
  }

  // Load and cache a view
  public Optional<MetadataView> getView(String name) {
    return Optional.ofNullable(viewCache.computeIfAbsent(name, key -> 
        viewRepository.findByName(key).orElse(null)));
  }

  // Load and cache a workflow
  public Optional<MetadataWorkflow> getWorkflowByModel(String modelName) {
    return Optional.ofNullable(workflowCache.computeIfAbsent(modelName, key -> 
        workflowRepository.findByModelName(key).orElse(null)));
  }

  // Load and cache permissions by role
  public List<MetadataPermission> getPermissionsByRole(String role) {
    return permissionCache.computeIfAbsent(role, key -> 
        permissionRepository.findByRole(key));
  }

  public List<MetadataModel> getAllModels() {
    return modelRepository.findAll();
  }

  public List<MetadataView> getAllViews() {
    return viewRepository.findAll();
  }

  public List<MetadataWorkflow> getAllWorkflows() {
    return workflowRepository.findAll();
  }

  public List<MetadataPermission> getAllPermissions() {
    return permissionRepository.findAll();
  }

  // Invalidate all in-memory caches
  public void invalidateCache() {
    modelCache.clear();
    viewCache.clear();
    workflowCache.clear();
    permissionCache.clear();
    
    // Fetch active version from DB
    versionRepository.findFirstByIsActiveTrueOrderByVersionDesc()
        .ifPresent(v -> this.activeVersion = v.getVersion());
  }

  // Get current active metadata schema package
  public Map<String, Object> getFullMetadataPackage() {
    Map<String, Object> pkg = new HashMap<>();
    
    // Ensure caches are primed
    List<MetadataModel> models = modelRepository.findAll();
    List<MetadataView> views = viewRepository.findAll();
    List<MetadataWorkflow> workflows = workflowRepository.findAll();
    List<MetadataPermission> permissions = permissionRepository.findAll();
    
    Map<String, Object> modelsMap = new HashMap<>();
    models.forEach(m -> {
      modelCache.put(m.getName(), m);
      modelsMap.put(m.getName(), m);
    });

    Map<String, Object> viewsMap = new HashMap<>();
    views.forEach(v -> {
      viewCache.put(v.getName(), v);
      viewsMap.put(v.getName(), v);
    });

    Map<String, Object> workflowsMap = new HashMap<>();
    workflows.forEach(w -> {
      workflowCache.put(w.getModelName(), w);
      workflowsMap.put(w.getName(), w);
    });

    Map<String, List<Object>> rolesMap = new HashMap<>();
    permissions.forEach(p -> {
      rolesMap.computeIfAbsent(p.getRole(), k -> new ArrayList<>()).add(p);
    });

    pkg.put("models", modelsMap);
    pkg.put("views", viewsMap);
    pkg.put("workflows", workflowsMap);
    pkg.put("permissions", rolesMap);
    pkg.put("version", activeVersion);

    return pkg;
  }
}
