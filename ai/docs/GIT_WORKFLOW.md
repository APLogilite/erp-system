# Git Workflow

This document defines every git operation, branch strategy, and lock-before-branch rule.

Every AI agent must read this before creating or merging branches.

---

## Branch Hierarchy

```
main                              ← visible to ALL agents
  └── prd/PRD-XXX                 ← visible to ALL agents (integration)
        └── feature/TASK-XXX      ← visible ONLY to working SE agent
        └── bugfix/BUG-XXX        ← visible ONLY to working SE agent
        └── enhancement/TASK-XXX  ← visible ONLY to working SE agent
```

### Visibility rules

- `main` — All agents see it. This is the single source of truth.
- `prd/PRD-XXX` — All agents see it. Tasks are merged here. QA tests here.
- `feature/TASK-XXX` — Only the working SE agent sees it. Other agents look at the PRD branch.
- QA never creates branches. QA tests directly on the PRD branch.
- PM never creates branches. PM works on `main`.

---

## Lock-before-branch Rule

Status changes (locks) must be committed to the **parent branch** BEFORE creating the child branch. This ensures all agents see the lock immediately.

### Starting a PRD

```
1. Change PRD doc: status APPROVED → IN_DEVELOPMENT
2. git add ai/prd/PRD-*.md
3. git commit -m "docs(PRD-XXX): advance to IN_DEVELOPMENT"
4. git push origin main
5. git checkout -b prd/PRD-XXX
6. git push -u origin prd/PRD-XXX
```

### Starting a task

```
1. Change task doc: locked: true, status → IN_DEVELOPMENT
2. git add ai/tasks/TASK-XXX.md
3. git commit -m "chore: lock TASK-XXX, start development"
4. git push origin prd/PRD-XXX
5. git checkout -b feature/TASK-XXX
6. (implement, build, lint, test)
7. Generate change report (ai/changes/CHANGE-TASK-XXX.md)
8. git add ai/changes/CHANGE-TASK-XXX.md
9. git commit -m "feat(TASK-XXX): implementation complete"
10. git checkout prd/PRD-XXX
11. git merge feature/TASK-XXX
12. git branch -d feature/TASK-XXX
13. Change task doc: locked: false, status → READY_FOR_TEST
14. git add ai/tasks/TASK-XXX.md
15. git commit -m "chore: TASK-XXX → READY_FOR_TEST"
16. git push origin prd/PRD-XXX
```

### QA starting a test

```
1. git checkout prd/PRD-XXX
2. git pull origin prd/PRD-XXX
3. Change task doc: locked: true, status → TESTING
4. git add ai/tasks/TASK-XXX.md
5. git commit -m "chore: TASK-XXX → TESTING"
6. git push origin prd/PRD-XXX
7. (test on prd/PRD-XXX, no sub-branch)
8. If PASS: change task doc: locked: false, status → TESTED
9. If FAIL: create bug task, set task back to READY_FOR_TEST
10. git add ai/tasks/TASK-XXX.md ai/tests/TEST-TASK-XXX.md
11. git commit -m "test(PRD-XXX): TASK-XXX testing complete"
12. git push origin prd/PRD-XXX
```

---

## Branch Naming

| Type | Pattern | Base Branch | Owner | Access Check |
|------|---------|-------------|-------|-------------|
| PRD integration | `prd/PRD-XXX-short-name` | `main` | Software Engineer | Enforced |
| Versioned PRD | `prd/PRD-XXX-v2` | `main` (post-merge) | Software Engineer | Enforced |
| Feature | `feature/TASK-XXX` | `prd/PRD-XXX` | Software Engineer | Enforced |
| Bugfix | `bugfix/BUG-XXX` | `prd/PRD-XXX` | Software Engineer | Enforced |
| Enhancement | `enhancement/TASK-XXX` | `prd/PRD-XXX` | Software Engineer | Enforced |
| Main | `main` | — | Release | Enforced |

> The **Access Check** column indicates the pre-commit hook (`scripts/check-access.mjs`) validates the commit against branch permissions for the current agent.

---

## Merge Rules

### Task branch → PRD branch (SE)

- Run all validation (build, lint, test) before merging
- Change report must exist before merge
- Task doc must be updated
- Use: `git merge feature/TASK-XXX` (on PRD branch)
- Delete feature branch after merge
- Never use `git merge --squash` (preserve history)

### PRD branch → main (Release)

- Only when PRD status is READY_FOR_DEPLOYMENT
- PM has confirmed with user
- All tasks are TESTED
- Use: `git checkout main && git merge prd/PRD-XXX`
- Do not delete PRD branch (it's a permanent record)

### Versioned PRD branch → main

- If original PRD was already merged, use `prd/PRD-XXX-v2` from latest main
- Same rules as original PRD merge

### What SE must NOT merge

- Task branch directly into main
- PRD branch into main
- Another agent's task branch

---

## Versioned PRD Branch Detection

When `prd/PRD-XXX` was already merged to main, subsequent work must create a versioned branch.

### Detection algorithm (run before every task)

1. Fetch the latest remote state: `git fetch --all`

2. Identify the original PRD branch name from the task (e.g. `prd/PRD-001-dynamic-form`).

3. Check if the original PRD branch has been merged:
   - If the branch does not exist locally or on remote → it was merged and deleted.
   - If it exists, run:
     ```
     git merge-base --is-ancestor origin/prd/PRD-XXX-name main
     ```
     Exit code 0 = ancestor of main (merged). Non-zero = not merged.

4. **If merged** (branch gone OR is ancestor of main):
   - `git checkout main && git pull`
   - Search for existing versioned branches: `git branch -a | grep prd/PRD-XXX-v`
   - Determine next version: if `-v2` exists, use `-v3`, etc.
   - Create the versioned branch:
     ```
     git checkout -b prd/PRD-XXX-v<N>
     git push -u origin prd/PRD-XXX-v<N>
     ```
   - This becomes the new Parent PRD branch.

5. **If NOT merged** (branch exists and is NOT ancestor):
   - `git checkout prd/PRD-XXX-name && git pull`
   - Original PRD branch remains the Parent PRD branch.

6. Verify:
   - ✓ Current branch is the determined base branch
   - ✓ Working tree is clean
   - ✓ Base branch is synchronized

---

## QA Single-Commit Rule

QA accumulates all artifacts throughout the PRD testing session and commits once at the end.

### Session accumulation

During testing:
- Create test reports (`ai/tests/TEST-TASK-XXX.md`)
- Create bug tasks (`ai/tasks/BUG-XXX.md`)
- Update task documents
- Update PROJECT_BOARD.md
- All changes stay in working tree (NOT committed per-task)

### Session end commit

When the PRD testing session ends (no READY_FOR_TEST tasks remain OR stopping condition reached):

1. Verify all documentation is synchronized
2. Commit ALL QA artifacts in a SINGLE commit directly to the test branch:
   ```
   git add ai/tests/ ai/tasks/BUG-*.md ai/PROJECT_BOARD.md
   git commit -m "test(PRD-XXX): QA session results"
   git push origin <test-branch>
   ```
3. If all PRD tasks are now TESTED:
   - Change PRD doc: status TESTING → READY_FOR_DEPLOYMENT
   - Change PRD doc: `updated` field to today
   - Update CHANGELOG.md
   - Run `git diff ai/prd/PRD-*.md` — verify ONLY `status` and `updated` changed
   - Include PRD file + CHANGELOG in the same session commit

---

## Failure Recovery

If any git operation fails:

1. Stop immediately
2. Record: failed operation, current branch, target branch, error message
3. Update task status → BLOCKED
4. Create failure report: `ai/failures/FAIL-TASK-XXX.md`
5. Update PROJECT_BOARD.md
6. Report the blocker

Never attempt to bypass git failures.

---

## Commit Conventions

| Scenario | Format | Example |
|----------|--------|---------|
| PRD status change | `docs(PRD-XXX): message` | `docs(PRD-004): advance to IN_DEVELOPMENT` |
| Task status change | `chore: message` | `chore: TASK-036 → READY_FOR_TEST` |
| Implementation | `feat(TASK-XXX): message` | `feat(TASK-036): create metadata schema migration` |
| Bug fix | `fix(BUG-XXX): message` | `fix(BUG-002): align API base path` |
| Test report | `test(PRD-XXX): message` | `test(PRD-004): QA session results` |
| Docs/templates | `docs: message` | `docs: update workflow rules` |
| Board/changelog | `chore: message` | `chore: update PROJECT_BOARD.md` |

---

## Synchronization

- Always fetch the latest remote state before any git operation
- Always pull the parent branch before creating a new branch
- Never assume the local branch is current
- After merge, push the parent branch immediately

---

## Pre-Commit Access Enforcement

The pre-commit hook (`scripts/check-access.mjs`) enforces file access rules:

1. Each agent writes its role to `.agent-role` at startup
2. The hook reads `.agent-role` + `ai/ACCESS_RULES.json`
3. It validates:
   - Current branch matches the agent's allowed branches
   - All staged files match the agent's allowed paths
   - Schema files are updated when migrations change
4. Violations block the commit with an error message

See `ai/ACCESS_RULES.md` for the full rule set.
