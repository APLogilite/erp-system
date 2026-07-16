---
id: CHANGE-TASK-009
task_id: TASK-009
parent_prd: PRD-001
branch: feature/TASK-009
type: Feature
status: IMPLEMENTED
---

# Summary
Added sub-form reorder and available-relations endpoints to FormSubFormController. Added circular reference detection. Added @PreAuthorize. Created AvailableRelationDto and SubFormReorderRequest DTOs. The available-relations endpoint scans many2one columns to find child tables referencing the parent table.

# Files Added
- AvailableRelationDto.java
- SubFormReorderRequest.java

# Files Modified
- FormSubFormController.java — added reorder, available-relations endpoints + @PreAuthorize
- FormSubFormService.java — added getAvailableRelations, reorderSubForms, checkCircularReference
- MetadataViewRepository.java — added findByModelNameAndType
- TableColumnRepository.java — added findByType
