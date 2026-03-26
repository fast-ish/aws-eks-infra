#!/usr/bin/env bash
# Run kubeconform schema validation on rendered manifests
# Validates against Kubernetes 1.35 and 1.36 schemas

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"
MANIFESTS_DIR="${PROJECT_ROOT}/target/rendered-manifests"

# Kubernetes versions to validate against
K8S_VERSIONS=("1.35.0" "1.36.0")

# CRD schemas to skip (these are custom resources without built-in schemas)
SKIP_KINDS=(
  "Certificate"
  "ClusterIssuer"
  "Issuer"
  "NodePool"
  "EC2NodeClass"
  "ClusterPolicy"
  "Policy"
  "ClusterSecretStore"
  "SecretStore"
  "ExternalSecret"
  "VeleroSchedule"
  "BackupStorageLocation"
  "VolumeSnapshotLocation"
  "ServiceMonitor"
  "PodMonitor"
  "PrometheusRule"
  "GrafanaAgent"
  "GrafanaDashboard"
  "ClusterOutputs"
  "ClusterFlows"
  "Outputs"
  "Flows"
)

# Build skip kinds argument
SKIP_KINDS_ARG=""
for kind in "${SKIP_KINDS[@]}"; do
  SKIP_KINDS_ARG="${SKIP_KINDS_ARG} -skip ${kind}"
done

echo "Running kubeconform schema validation"
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

for K8S_VERSION in "${K8S_VERSIONS[@]}"; do
  echo "═══════════════════════════════════════════════════════════════"
  echo "Validating against Kubernetes ${K8S_VERSION}"
  echo "═══════════════════════════════════════════════════════════════"

  # Run kubeconform
  # -strict: reject unknown fields
  # -summary: show summary at end
  # -output: output format (text, json, tap)
  # -skip: skip CRDs that don't have schemas
  # -ignore-missing-schemas: don't fail on missing CRD schemas

  set +e
  kubeconform \
    -kubernetes-version "${K8S_VERSION}" \
    -strict \
    -summary \
    -output text \
    -ignore-missing-schemas \
    ${SKIP_KINDS_ARG} \
    "${MANIFESTS_DIR}"/*.yaml 2>&1

  EXIT_CODE=$?
  set -e

  if [[ ${EXIT_CODE} -eq 0 ]]; then
    echo "✓ Kubernetes ${K8S_VERSION} validation passed"
  else
    echo "✗ Kubernetes ${K8S_VERSION} validation failed"
    OVERALL_EXIT_CODE=1
  fi

  echo ""
done

echo "═══════════════════════════════════════════════════════════════"
if [[ ${OVERALL_EXIT_CODE} -eq 0 ]]; then
  echo "✓ All schema validations passed"
else
  echo "✗ Schema validation failed. Fix the issues above."
fi
echo "═══════════════════════════════════════════════════════════════"

exit ${OVERALL_EXIT_CODE}
