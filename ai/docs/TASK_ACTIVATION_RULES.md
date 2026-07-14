# Task Activation Rules

A task may move from PLANNED to READY_FOR_DEV only if:

- Parent PRD is APPROVED.
- All dependency tasks are READY_FOR_TEST.
- No blocking bug exists.
- Task is not cancelled.
- Task is not locked.

Developer performs activation after completing work.

Product Manager never performs automatic activation.
