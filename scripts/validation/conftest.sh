#!/usr/bin/env bash
# Run conftest OPA policy validation on rendered manifests

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"
MANIFESTS_DIR="${PROJECT_ROOT}/target/rendered-manifests"
POLICIES_DIR="${PROJECT_ROOT}/policies"

echo "Running conftest OPA policy validation"
echo "Manifests directory: ${MANIFESTS_DIR}"
echo "Policies directory: ${POLICIES_DIR}"
echo ""

if [[ ! -d "${MANIFESTS_DIR}" ]]; then
  echo "Error: Manifests directory not found. Run render-helm.sh first."
  exit 1
fi

if [[ ! -d "${POLICIES_DIR}" ]]; then
  echo "Warning: Policies directory not found. Skipping conftest."
  exit 0
fi

# Count manifests
MANIFEST_COUNT=$(find "${MANIFESTS_DIR}" -name "*.yaml" -type f | wc -l | tr -d ' ')
if [[ ${MANIFEST_COUNT} -eq 0 ]]; then
  echo "No manifests found to validate."
  exit 0
fi

# Count policies
POLICY_COUNT=$(find "${POLICIES_DIR}" -name "*.rego" -type f ! -name "*_test.rego" | wc -l | tr -d ' ')
if [[ ${POLICY_COUNT} -eq 0 ]]; then
  echo "No policies found to apply."
  exit 0
fi

echo "Found ${MANIFEST_COUNT} manifest files"
echo "Found ${POLICY_COUNT} policy files"
echo ""

# Run policy tests first
echo "═══════════════════════════════════════════════════════════════"
echo "Running OPA Policy Unit Tests"
echo "═══════════════════════════════════════════════════════════════"

set +e
conftest verify --policy "${POLICIES_DIR}" 2>&1
TEST_EXIT_CODE=$?
set -e

if [[ ${TEST_EXIT_CODE} -ne 0 ]]; then
  echo ""
  echo "✗ Policy unit tests failed"
  exit ${TEST_EXIT_CODE}
fi

echo "✓ Policy unit tests passed"
echo ""

# Run conftest on manifests
echo "═══════════════════════════════════════════════════════════════"
echo "Validating Manifests Against Policies"
echo "═══════════════════════════════════════════════════════════════"

# Track results
TOTAL_FILES=0
PASSED_FILES=0
FAILED_FILES=0
WARNING_FILES=0

for manifest in "${MANIFESTS_DIR}"/*.yaml; do
  if [[ ! -f "${manifest}" ]]; then
    continue
  fi

  TOTAL_FILES=$((TOTAL_FILES + 1))
  filename=$(basename "${manifest}")

  set +e
  OUTPUT=$(conftest test \
    --policy "${POLICIES_DIR}" \
    --output stdout \
    --no-color \
    "${manifest}" 2>&1)

  EXIT_CODE=$?
  set -e

  # Count failures and warnings in output
  FAILURES=$(echo "${OUTPUT}" | grep -c "FAIL" || true)
  WARNINGS=$(echo "${OUTPUT}" | grep -c "WARN" || true)

  if [[ ${EXIT_CODE} -eq 0 && ${FAILURES} -eq 0 ]]; then
    echo "✓ ${filename}"
    PASSED_FILES=$((PASSED_FILES + 1))
  elif [[ ${FAILURES} -eq 0 && ${WARNINGS} -gt 0 ]]; then
    echo "⚠ ${filename} (${WARNINGS} warnings)"
    WARNING_FILES=$((WARNING_FILES + 1))
  else
    echo "✗ ${filename}"
    echo "${OUTPUT}" | grep -E "(FAIL|WARN)" | sed 's/^/    /'
    FAILED_FILES=$((FAILED_FILES + 1))
  fi
done

echo ""
echo "═══════════════════════════════════════════════════════════════"
echo "Policy Validation Summary"
echo "═══════════════════════════════════════════════════════════════"
echo "Total files:     ${TOTAL_FILES}"
echo "Passed:          ${PASSED_FILES}"
echo "Warnings:        ${WARNING_FILES}"
echo "Failed:          ${FAILED_FILES}"
echo ""

if [[ ${FAILED_FILES} -gt 0 ]]; then
  echo "✗ Policy validation failed"
  exit 1
else
  if [[ ${WARNING_FILES} -gt 0 ]]; then
    echo "⚠ Policy validation passed with warnings"
  else
    echo "✓ Policy validation passed"
  fi
fi
echo "═══════════════════════════════════════════════════════════════"
