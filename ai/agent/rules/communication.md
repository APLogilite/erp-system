# Communication Guide

This document defines how every AI agent communicates, handles blockers, and applies general principles.

Every AI agent must read this before starting any work.

---

## Communication Style

### All agents
- Be concise and direct
- Communicate using facts, not assumptions
- Explain decisions when they affect architecture, quality, or scope
- Do not ask unnecessary questions
- If sufficient information exists, proceed

### When blocked
1. Clearly explain the blocker
2. Identify the affected PRD or task
3. Describe the root cause
4. Recommend the next action
5. Never stop with only "I am blocked"
6. Provide a structured summary

### When completing work
Provide a structured execution summary including:
- What was completed
- Validation results
- Documentation updated
- Branches created/merged
- Remaining work
- Recommendations

---

## General Principles

These apply to every agent regardless of role.

### Never guess
- If information is missing, ask
- Never assume user intent
- Never invent business requirements
- If the PRD is incomplete or ambiguous: stop, document, report

### Preserve history
- Always append new entries — never overwrite
- Never modify completed tasks or historical records
- Every decision must be traceable
- Project history is permanent

### Never skip stages
- Follow every step in the workflow
- Never bypass validation
- Never ignore failures or git conflicts
- If a step fails: stop, document, report

### Keep synchronized
- Task doc ↔ PROJECT_BOARD.md must always match
- PRD doc ↔ PROJECT_BOARD.md must always match
- Never update one without the other
- If inconsistency found: stop, correct, continue

### Quality
- Leave the codebase better than you found it
- Prioritize correctness over speed
- Never hide problems
- Never expand scope without authorization

### Boundaries
- Never modify files owned by another agent
- Never perform another agent's role
- If request is out of scope: refuse politely, redirect to correct agent

---

## Escalation

Stop and request user input when:

- Requirements conflict or are incomplete
- User approval is explicitly required
- Security or data integrity issue is identified
- Breaking change is required
- Multiple valid approaches have different business outcomes
- Project-wide issue prevents continuation

### Escalation format

When escalating:

```
BLOCKER: {short description}
AFFECTED: {PRD-XXX / TASK-XXX}
ROOT CAUSE: {what caused the block}
RECOMMENDED ACTION: {what needs to happen}
CAN CONTINUE: {yes/no — can unrelated work proceed?}
```

---

## Blocker Reporting

When a task becomes blocked, always:

1. Update task status → BLOCKED
2. Update PROJECT_BOARD.md
3. Create failure report (if applicable): `ai/project/failures/FAIL-TASK-XXX.md`
4. Determine whether unrelated work can continue

If another task has:
- No dependency on the blocked task
- No dependency on the same requirement issue

Then continue with that task. Do not let one blocker stop all work.
