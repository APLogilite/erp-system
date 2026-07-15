#!/usr/bin/env node

import fs from 'fs';
import path from 'path';
import { execSync } from 'child_process';

const ROOT = path.resolve(import.meta.dirname, '..');

function globToRegex(pattern) {
  let regexStr = '';
  let i = 0;
  while (i < pattern.length) {
    const ch = pattern[i];
    if (ch === '*') {
      if (i + 1 < pattern.length && pattern[i + 1] === '*') {
        regexStr += '.*';
        i += 2;
        if (i < pattern.length && pattern[i] === '/') {
          i++;
        }
      } else {
        regexStr += '[^/]*';
        i++;
      }
    } else if (ch === '?') {
      regexStr += '[^/]';
      i++;
    } else if (/[.+^${}()|[\]\\]/.test(ch)) {
      regexStr += '\\' + ch;
      i++;
    } else {
      regexStr += ch;
      i++;
    }
  }
  return new RegExp(`^${regexStr}$`);
}

function matchesGlob(pattern, str) {
  return globToRegex(pattern).test(str);
}

const agentRolePath = path.join(ROOT, '.agent-role');
let agentRole;
try {
  agentRole = fs.readFileSync(agentRolePath, 'utf-8').trim();
} catch {
  process.exit(0);
}

const rulesPath = path.join(ROOT, 'ai', 'docs', 'ACCESS_RULES.json');
let rules;
try {
  rules = JSON.parse(fs.readFileSync(rulesPath, 'utf-8'));
} catch {
  console.error('ERROR: Could not read ai/docs/ACCESS_RULES.json');
  process.exit(1);
}

let branch;
try {
  branch = execSync('git rev-parse --abbrev-ref HEAD', { encoding: 'utf-8' }).trim();
} catch {
  console.error('ERROR: Could not determine current branch');
  process.exit(1);
}

let stagedFilesRaw;
try {
  stagedFilesRaw = execSync('git diff --cached --name-only', { encoding: 'utf-8' }).trim();
} catch {
  console.error('ERROR: Could not list staged files');
  process.exit(1);
}
const stagedFiles = stagedFilesRaw ? stagedFilesRaw.split('\n') : [];

const agentConfig = rules.rules[agentRole];
if (!agentConfig) {
  console.error(`ERROR: Unknown agent role "${agentRole}"`);
  process.exit(1);
}

const violations = [];

const branchOk = agentConfig.allow_branches.some(p => matchesGlob(p, branch));
if (!branchOk) {
  violations.push(
    `Branch "${branch}" is not allowed for role "${agentConfig.label}". Allowed: ${agentConfig.allow_branches.join(', ')}`
  );
}

for (const file of stagedFiles) {
  const fileOk = agentConfig.allow_paths.some(p => matchesGlob(p, file));
  if (!fileOk) {
    violations.push(
      `File "${file}" is not allowed for role "${agentConfig.label}". Allowed paths: ${agentConfig.allow_paths.join(', ')}`
    );
  }
}

if (agentRole === 'se') {
  const hasMigrationFile = stagedFiles.some(f =>
    f.startsWith('backend/src/main/resources/db/migration/')
  );
  const hasSchemaFile = stagedFiles.some(f => f.startsWith('ai/schema/'));
  if (hasMigrationFile && !hasSchemaFile) {
    console.warn(
      'WARNING: Migration files staged but no corresponding schema file under ai/schema/. Consider updating schema documentation.'
    );
  }
}

if (violations.length > 0) {
  console.error('Access control violations detected:');
  for (const v of violations) {
    console.error(`  - ${v}`);
  }
  process.exit(1);
}
