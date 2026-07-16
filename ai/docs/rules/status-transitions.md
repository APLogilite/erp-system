# Status Transitions

This document defines every status, who owns each transition, and the rules for moving between states.

Every AI agent must read this before modifying any task or PRD status.

---

## PRD Lifecycle

```
DRAFT → REVIEW → APPROVED → IN_DEVELOPMENT → TESTING → READY_FOR_DEPLOYMENT → COMPLETED
                                                                                ↓
                                                                           REOPENED
                                                                                ↓
                                                                          IN_DEVELOPMENT
```

### Transition Triggers

| From | To | Trigger | Owner |
|------|----|---------|-------|
| DRAFT | REVIEW | Planning complete, waiting for user approval | Product Manager |
| REVIEW | APPROVED | User explicitly approves the PRD | Product Manager |
| APPROVED | IN_DEVELOPMENT | SE creates PRD branch and starts first task | Software Engineer |
| IN_DEVELOPMENT | TESTING | ALL tasks under PRD are READY_FOR_TEST (merged to PRD branch) | Software Engineer |
| TESTING | READY_FOR_DEPLOYMENT | ALL tasks under PRD are TESTED by QA | QA Engineer |
| READY_FOR_DEPLOYMENT | COMPLETED | PRD branch merged to main (after PM + user confirmation) | Release |
| COMPLETED | REOPENED | Bug filed against a released PRD (post-release regression) | Product Manager |
| REOPENED | IN_DEVELOPMENT | New bug fix branch created, work resumes | Software Engineer |

### Valid statuses

- DRAFT — Initial discussion, requirements incomplete
- REVIEW — Waiting for user approval
- APPROVED — Requirements finalized, tasks can be generated
- IN_DEVELOPMENT — SE is actively working on tasks
- TESTING — QA is validating the implementation
- READY_FOR_DEPLOYMENT — All tasks tested, awaiting merge
- COMPLETED — Released and merged to main
- REOPENED — Post-release bug found, PRD needs additional work

---

## Task Lifecycle

```
PLANNING → PLANNED → READY_FOR_DEV → IN_DEVELOPMENT → READY_FOR_TEST → TESTING → TESTED → COMPLETED
```

Plus exception states: `BLOCKED | ON_HOLD | CANCELLED`

### Transition Triggers

| From | To | Trigger | Owner |
|------|----|---------|-------|
| — | PLANNING | Product Manager creates the task | Product Manager |
| PLANNING | PLANNED | Task definition is complete | Product Manager |
| PLANNED | READY_FOR_DEV | All dependencies are satisfied (after a task merge) | Software Engineer |
| READY_FOR_DEV | IN_DEVELOPMENT | SE locks task and creates branch | Software Engineer |
| IN_DEVELOPMENT | READY_FOR_TEST | Implementation complete, merged to PRD branch | Software Engineer |
| READY_FOR_TEST | TESTING | QA locks task, starts testing on PRD branch | QA Engineer |
| TESTING | TESTED | QA passes all tests | QA Engineer |
| TESTED | COMPLETED | Cascade after PRD branch merges to main | Release |
| COMPLETED | REOPENED | Post-release bug found against this PRD (see PRD REOPENED) | Product Manager |
| — | READY_FOR_DEV | PM creates a new bug (starting status for bugs) | Product Manager |
| — | BLOCKED | Work cannot continue | Current Owner |
| — | ON_HOLD | Temporarily paused | Current Owner |
| — | CANCELLED | Task will not be implemented | Product Manager |

### Valid statuses

- PLANNING — Task is being defined by Product Manager
- PLANNED — Task defined, waiting for dependencies
- READY_FOR_DEV — Dependencies met, ready for SE
- IN_DEVELOPMENT — SE is implementing
- READY_FOR_TEST — Implemented, merged, waiting for QA
- TESTING — QA is actively testing
- TESTED — QA verified, passes
- COMPLETED — Released via PRD merge
- BLOCKED — Current Owner, work stopped
- ON_HOLD — Current Owner, paused
- CANCELLED — Product Manager, removed

---

## Bug Lifecycle

```
READY_FOR_DEV → IN_DEVELOPMENT → READY_FOR_TEST → TESTING → RESOLVED
```

| From | To | Trigger | Owner |
|------|----|---------|-------|
| — | READY_FOR_DEV | QA creates bug task on test failure | QA Engineer |
| READY_FOR_DEV | IN_DEVELOPMENT | SE locks bug and starts fixing | Software Engineer |
| IN_DEVELOPMENT | READY_FOR_TEST | Fix complete, merged to PRD branch | Software Engineer |
| READY_FOR_TEST | TESTING | QA locks bug, starts verification | QA Engineer |
| TESTING | RESOLVED | Fix verified by QA | QA Engineer |

---

## Lock Rules

Locking prevents multiple agents from working on the same task.

### Lock-before-branch (must follow this order)

```
PRD level:
  1. Change PRD doc: status APPROVED → IN_DEVELOPMENT
  2. Commit PRD doc to main ← lock visible to all agents
  3. Create prd/PRD-XXX branch from main

Task level:
  1. Change task doc: locked: true, status → IN_DEVELOPMENT
  2. Commit task doc to prd/PRD-XXX ← lock visible to all agents
  3. Create feature/TASK-XXX branch from prd/PRD-XXX

QA level:
  1. Change task doc: locked: true, status → TESTING
  2. Commit task doc to prd/PRD-XXX ← lock visible to all agents
  3. Test on prd/PRD-XXX (no sub-branch)
```

### Lock states

- `locked: true` — Agent is actively working on this. Other agents must NOT select it.
- `locked: false` — Available for next agent to pick up.

---

## Dependency Activation

When a task reaches READY_FOR_TEST (after merge to PRD branch):

1. Check every task that lists it in `depends_on`
2. If ALL dependencies of that task are READY_FOR_TEST or TESTED or COMPLETED:
   - Change status PLANNED → READY_FOR_DEV
   - Update PROJECT_BOARD.md
3. This is performed by the Software Engineer after each merge
4. Product Manager does NOT perform automatic activation

---

## Cascade Completion

When a PRD branch merges to main:

1. PRD document: status → COMPLETED
2. ALL tasks under this PRD: status → COMPLETED
3. ALL bugs under this PRD: status → RESOLVED
4. ALL enhancements under this PRD: status → COMPLETED
5. Updated PROJECT_BOARD.md
6. This is performed by Release agent (or SE if Release not assigned)

## Reopening a Completed PRD

When a bug is filed against a COMPLETED PRD:

1. PM creates the bug task with `status: READY_FOR_DEV` and the correct `parent_prd`
2. PM changes the PRD document: status → REOPENED
3. PM logs the reason in PRD history
4. Normal lifecycle resumes: SE picks up the bug (READY_FOR_DEV → IN_DEVELOPMENT → ...)
5. PRD stays REOPENED until all new bugs are RESOLVED, then returns to COMPLETED

---

## PRD Versioning

Every approved change to a PRD creates a new version.

| Version | When |
|---------|------|
| 1.0.0 | Initial approval |
| 1.1.0 | Minor requirement update |
| 1.2.0 | Additional business rule |
| 2.0.0 | Major feature expansion |

Rules:
- Every version update must be recorded in the PRD Change History and CHANGELOG.md
- The active PRD version must always match the version referenced by newly created tasks
- Previously completed tasks continue referencing the PRD version under which they were created
- Never modify a completed task's version reference

---

## Rules

- Never invent new statuses. Only use the statuses defined above.
- Never skip a status in the lifecycle.
- A task can only go BLOCKED from its current active status.
- A COMPLETED task or bug is never modified. Use REOPENED on the PRD and create new bug tasks.
- If a status is set incorrectly (e.g., COMPLETED on creation with no work done), it may be corrected by adding a `corrected` entry to history explaining why.
- Requirement changes after IN_DEVELOPMENT require an Enhancement Task.

## Default Statuses

| Item | Created By | Starting Status |
|------|-----------|-----------------|
| PRD | PM | DRAFT |
| Task | PM | PLANNING |
| Bug | QA or PM | READY_FOR_DEV |
| Enhancement | PM | PLANNING |
