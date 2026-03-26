#!/usr/bin/env bash
# Run pluto API deprecation checking on rendered manifests
# Checks for deprecated/removed APIs in Kubernetes 1.35 and 1.36

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"
MANIFESTS_DIR="${PROJECT_ROOT}/target/rendered-manifests"

# Kubernetes versions to check against
K8S_VERSIONS=("v1.35.0" "v1.36.0")

echo "Running pluto API deprecation check"
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

# Track overall results
OVERALL_EXIT_CODE=0
DEPRECATIONS_FOUND=false

for K8S_VERSION in "${K8S_VERSIONS[@]}"; do
  echo "═══════════════════════════════════════════════════════════════"
  echo "Checking for deprecated APIs in Kubernetes ${K8S_VERSION}"
  echo "═══════════════════════════════════════════════════════════════"

  # Run pluto
  # detect-files: scan files for deprecated APIs
  # --target-versions: check against specific K8s version
  # --output: output format (wide, json, yaml, markdown)
  # --ignore-deprecations: don't fail on deprecations (only removed APIs)
  # --ignore-removals: don't fail on removed APIs

  set +e
  OUTPUT=$(pluto detect-files \
    --target-versions "k8s=${K8S_VERSION}" \
    --output wide \
    --directory "${MANIFESTS_DIR}" 2>&1)

  EXIT_CODE=$?
  set -e

  echo "${OUTPUT}"
  echo ""

  if [[ ${EXIT_CODE} -eq 0 ]]; then
    echo "✓ No deprecated/removed APIs found for ${K8S_VERSION}"
  elif [[ ${EXIT_CODE} -eq 2 ]]; then
    echo "⚠ Deprecated APIs found for ${K8S_VERSION} (not blocking)"
    DEPRECATIONS_FOUND=true
  elif [[ ${EXIT_CODE} -eq 3 ]]; then
    echo "✗ Removed APIs found for ${K8S_VERSION}"
    OVERALL_EXIT_CODE=1
  else
    echo "✗ Error running pluto"
    OVERALL_EXIT_CODE=1
  fi

  echo ""
done

echo "═══════════════════════════════════════════════════════════════"
if [[ ${OVERALL_EXIT_CODE} -eq 0 ]]; then
  if [[ "${DEPRECATIONS_FOUND}" == true ]]; then
    echo "⚠ Some deprecated APIs found (see above), but no removed APIs"
    echo "  Consider updating manifests before the next K8s upgrade"
  else
    echo "✓ No deprecated or removed APIs found"
  fi
else
  echo "✗ Removed APIs detected - manifests must be updated before upgrade"
fi
echo "═══════════════════════════════════════════════════════════════"

exit ${OVERALL_EXIT_CODE}
