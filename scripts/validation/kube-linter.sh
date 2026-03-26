#!/usr/bin/env bash
# Run kube-linter best practices validation on rendered manifests

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"
MANIFESTS_DIR="${PROJECT_ROOT}/target/rendered-manifests"
CONFIG_FILE="${PROJECT_ROOT}/.kube-linter.yaml"

echo "Running kube-linter best practices validation"
echo "Manifests directory: ${MANIFESTS_DIR}"
echo ""

if [[ ! -d "${MANIFESTS_DIR}" ]]; then
  echo "Error: Manifests directory not found. Run render-helm.sh first."
  exit 1
fi

# Count manifests
MANIFEST_COUNT=$(find "${MANIFESTS_DIR}" -name "*.yaml" -type f | wc -l | tr -d ' ')
if [[ ${MANIFEST_COUNT} -eq 0 ]]; then
  echo "No manifests found to validate."
  exit 0
fi

echo "Found ${MANIFEST_COUNT} manifest files"
echo ""

# Build config argument if config file exists
CONFIG_ARG=""
if [[ -f "${CONFIG_FILE}" ]]; then
  CONFIG_ARG="--config ${CONFIG_FILE}"
  echo "Using configuration: ${CONFIG_FILE}"
  echo ""
fi

echo "═══════════════════════════════════════════════════════════════"
echo "kube-linter Results"
echo "═══════════════════════════════════════════════════════════════"

# Run kube-linter
# --format: output format (plain, json, sarif)
# Returns non-zero if there are errors

set +e
kube-linter lint \
  ${CONFIG_ARG} \
  --format plain \
  "${MANIFESTS_DIR}" 2>&1

EXIT_CODE=$?
set -e

echo ""
echo "═══════════════════════════════════════════════════════════════"
if [[ ${EXIT_CODE} -eq 0 ]]; then
  echo "✓ kube-linter validation passed"
else
  echo "✗ kube-linter validation found issues"
  echo ""
  echo "To suppress specific checks, add them to .kube-linter.yaml:"
  echo "  checks:"
  echo "    exclude:"
  echo '      - "check-name"'
fi
echo "═══════════════════════════════════════════════════════════════"

exit ${EXIT_CODE}
