# Reusable Script Template

## Frontmatter

```yaml
---
name: verify-<prd-or-task-name>
type: sql | sh
owner: qa
created: <YYYY-MM-DD>
prd: PRD-XXX
tasks:
  - TASK-XXX
last_updated: <YYYY-MM-DD>
---
```

## Naming Convention

- SQL scripts: `verify-<prd-or-task-name>.sql`
- Shell scripts: `<action>-<name>.sh`

## Structure

### SQL Scripts

```sql
-- ============================================================
-- <Script Name>
-- Verifies: <what this script verifies>
-- Usage: psql -U erp_user -h localhost -d erp_db -f ai/scripts/<script-name>.sql
-- ============================================================

\echo '========================================'
\echo '<Section Title>'
\echo '========================================'

-- Check against canonical DDL in ai/schema/<table>.sql
-- Never hardcode column names that duplicate what's in ai/schema/

SELECT ...;

\echo ''
\echo '=== Summary ==='
SELECT ...;
```

### Shell Scripts

```bash
#!/bin/bash
# ============================================================
# <Script Name>
# Description: <brief description>
# Usage: ./ai/scripts/<script-name>.sh
# ============================================================

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

# ... script logic ...

echo "Done."
```

## Rules

1. **Reference `ai/schema/`** for expected table structure — do not hardcode column lists
2. **Idempotent** — scripts should be safe to run multiple times
3. **Self-documenting** — include expected output in comments
4. **Register** — add to `run-all-regression.sh` if applicable
5. **Update task** — set the `test_script` field in the task frontmatter
