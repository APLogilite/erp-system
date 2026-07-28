# Execution Rules

This document defines startup sequences, continuous execution behavior, selection priorities, stopping conditions, and reporting standards.

Every AI agent must read this before starting execution.

---

## Startup Sequence

Every agent follows this pattern on startup:

### Step 1 — Load framework documentation

Read in order:

1. `ai/agent/rules/workflow.md` — master workflow overview
2. `ai/agent/project-memory.md` — project context and conventions
3. `ai/agent/rules/status-transitions.md` — status definitions and transition rules
4. `ai/agent/rules/communication.md` — communication and principles
5. `ai/agent/rules/execution.md` — this file
6. `ai/agent/rules/documentation.md` — file ownership and sync rules

### Step 2 — Load role-specific documentation

- Software Engineer also reads: `ai/agent/rules/git-workflow.md`
- QA Engineer also reads: `ai/agent/rules/git-workflow.md`

### Step 3 — Load project state

- `ai/agent/project-board.md` — current execution state

### Step 4 — Validate project state

Verify:

- PROJECT_BOARD.md matches existing tasks
- Every task belongs to a valid PRD
- No duplicate task or PRD IDs
- Task dependencies are valid
- Documentation is internally consistent

If inconsistencies: stop, report, recommend corrective actions.

---

## Task Selection Priority

### Software Engineer

Pick the first task satisfying ALL of:
- Status = READY_FOR_DEV
- Parent PRD = APPROVED or IN_DEVELOPMENT
- All dependencies are READY_FOR_TEST/TESTED/COMPLETED
- Task is not blocked or locked

Priority: Critical → High → Medium → Low
Within same priority: oldest first

### QA Engineer

Pick the first task satisfying ALL of:
- Status = READY_FOR_TEST
- Parent PRD = TESTING
- Task is not blocked or locked

Priority: Critical → High → Medium → Low
Within same priority: oldest first

### Product Manager

- Work on PRDs in order of business priority
- Prioritize tasks that unblock the Software Engineer

---

## Continuous Execution

Completing one unit of work is NOT a stopping condition.

After completing a task:
1. Synchronize the repository: `git fetch --all`
2. **Re-determine the base branch** — the PRD may have been merged to main since the last task. Run the versioned PRD branch detection logic (see `ai/agent/rules/git-workflow.md`).
3. Return to the determined base branch (original or versioned PRD branch)
4. Read the latest PROJECT_BOARD.md
5. Identify the next eligible task
6. Verify: all dependencies satisfied, task not blocked, no active Requirement Issue
7. If eligible work exists: begin immediately
8. Do not produce an execution summary between tasks

Continue until a stopping condition is reached.

---

## Server Verification (Software Engineer)

Before advancing a PRD from IN_DEVELOPMENT to TESTING, the SE MUST verify both servers start correctly.

### Verification Script

Use `bash start-all.sh` (from repo root) to start both servers:

- Backend logs → `/tmp/erp-backend.log`
- Frontend logs → `/tmp/erp-frontend.log`

The script waits for the backend to respond on `http://localhost:8081` before starting the frontend.

### Log Inspection

After the script reports both servers running, inspect the logs:

1. **Backend** — Check `/tmp/erp-backend.log` for:
   - `BUILD SUCCESS` or `Started ErpApplication` (Spring Boot ready)
   - No `ERROR` or `Exception` stack traces
   - No `APPLICATION FAILED TO START`
   - No port-binding errors (`Address already in use`)
   - No Flyway migration failures

2. **Frontend** — Check `/tmp/erp-frontend.log` for:
   - `VITE ready` or `Local:` with the dev server URL
   - No build/compile errors
   - No module-not-found errors

### Error Handling

If any errors are found:

1. Stop the servers (`pkill -f "spring-boot:run" && pkill -f "vite"`)
2. Diagnose and fix the issue
3. Re-run `bash start-all.sh`
4. Re-check logs
5. Repeat until both servers start cleanly

### Cleanup

After verification, stop the servers with:
```bash
pkill -f "spring-boot:run" 2>/dev/null; pkill -f "vite" 2>/dev/null
```

### When to Use --setup

If the PRD includes database schema changes or new seed data, use `bash start-all.sh --setup` instead to reset the database before verification.

---

## Stopping Conditions

Stop execution when ANY of the following is true:

- No eligible work remains
- User approval is required
- A blocker prevents progress
- A repository-wide issue prevents continuation
- All remaining work depends on blocked tasks

When stopping:
1. Leave the repository in a clean state
2. Ensure all documentation is synchronized
3. Produce an execution summary

---

## Execution Summary

Before stopping, always produce a structured summary.

### Software Engineer summary

- Tasks completed
- Tasks currently in progress
- Tasks blocked
- Tasks automatically activated
- Current branch
- Branches created and merged
- Validation results (passed/failed/skipped)
- Documentation updated
- Remaining READY_FOR_DEV tasks
- Recommendations

### QA Engineer summary

- Tasks tested
- Tasks completed (TESTED)
- Tasks blocked
- Tasks skipped
- Bugs created
- Test results (passed/failed/skipped)
- Documentation updated
- Remaining READY_FOR_TEST tasks
- Release readiness assessment
- Recommendations

### Product Manager summary

- Requirements gathered
- PRDs created or updated
- Tasks generated or updated
- Tasks activated
- Dependencies analyzed
- Risks identified
- Next recommended actions

---

## Autonomous Decision-Making

### Software Engineer may independently decide:
- Internal code structure, file organization, refactoring within scope
- Naming conventions
- Library usage already approved by the project

### QA Engineer may independently decide:
- Test execution order
- Additional regression, negative, or edge-case testing
- Evidence collection methods

### Product Manager may independently decide:
- Task breakdown within the approved PRD scope
- Dependency ordering
- Documentation structure

### No agent may independently change:
- Business requirements
- Acceptance criteria
- PRD content (beyond status/updated fields)
- Project scope
- Another agent's deliverables
