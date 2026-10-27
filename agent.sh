#!/usr/bin/env bash
# ============================================================
# ERP System — AI Agent CLI Helper
# ============================================================
# Shortcut tool to manage agent roles, check permissions,
# view project board status, and launch monitoring tools.
# ============================================================

set -e

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROLE_FILE="$DIR/.agent-role"

function print_usage() {
  echo "Usage: ./agent.sh <command> [args]"
  echo ""
  echo "Commands:"
  echo "  role [se|qa|pm|tw]   Get or set the active agent role"
  echo "  status               Show current agent role, branch, and staged files"
  echo "  check                Run boundary access check (scripts/check-access.mjs)"
  echo "  board                Display top pending tasks from ai/agent/project-board.md"
  echo "  monitor              Start the real-time project dashboard on port 3000"
  echo "  verify               Start both backend & frontend servers and check startup logs"
  echo ""
  echo "Roles:"
  echo "  pm  -> Product Manager     (PRDs, tasks, board, changelog)"
  echo "  se  -> Software Engineer   (code implementation, changes, schema)"
  echo "  qa  -> QA Engineer         (testing, test reports, bug reports)"
  echo "  tw  -> Technical Writer    (module and flow documentation)"
}

CMD="$1"

case "$CMD" in
  role)
    if [ -z "$2" ]; then
      if [ -f "$ROLE_FILE" ]; then
        echo "Active role: $(cat "$ROLE_FILE")"
      else
        echo "No role set. Set one with: ./agent.sh role [se|qa|pm|tw]"
      fi
    else
      TARGET_ROLE="$2"
      case "$TARGET_ROLE" in
        se|qa|pm|tw)
          echo "$TARGET_ROLE" > "$ROLE_FILE"
          echo "Active agent role set to: $TARGET_ROLE"
          ;;
        *)
          echo "Error: Invalid role '$TARGET_ROLE'. Allowed: se, qa, pm, tw"
          exit 1
          ;;
      esac
    fi
    ;;

  status)
    echo "========================================"
    echo " AI Agent Status"
    echo "========================================"
    if [ -f "$ROLE_FILE" ]; then
      echo "  Role   : $(cat "$ROLE_FILE")"
    else
      echo "  Role   : (none set)"
    fi
    CURRENT_BRANCH="$(git rev-parse --abbrev-ref HEAD 2>/dev/null || echo 'unknown')"
    echo "  Branch : $CURRENT_BRANCH"
    echo "  Git    : $(git status -s | wc -l) modified/untracked file(s)"
    echo "========================================"
    ;;

  check)
    node "$DIR/scripts/check-access.mjs"
    ;;

  board)
    echo "=== Ready For Development ==="
    sed -n '/## Ready For Development/,/---/p' "$DIR/ai/agent/project-board.md" | head -n -1
    echo ""
    echo "=== Ready For Test ==="
    sed -n '/## Ready For Test/,/---/p' "$DIR/ai/agent/project-board.md" | head -n -1
    ;;

  monitor)
    echo "Starting Project Monitor Dashboard on http://localhost:3000..."
    cd "$DIR/ai/agent/monitor"
    python3 server.py
    ;;

  verify)
    echo "Verifying server startup with start-all.sh..."
    bash "$DIR/start-all.sh"
    ;;

  *)
    print_usage
    ;;
esac
