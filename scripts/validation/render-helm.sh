#!/usr/bin/env bash
# Render all Helm charts to manifests for validation
# Requires: helm

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"
OUTPUT_DIR="${PROJECT_ROOT}/target/rendered-manifests"
HELM_VALUES_DIR="${PROJECT_ROOT}/src/main/resources/production/v1/helm"

# Environment to render (default: production)
ENVIRONMENT="${1:-production}"

# Cleanup and create output directory
rm -rf "${OUTPUT_DIR}"
mkdir -p "${OUTPUT_DIR}"

echo "Rendering Helm charts for environment: ${ENVIRONMENT}"
echo "Output directory: ${OUTPUT_DIR}"
echo ""

# Helm charts configuration
# Format: name|repository|chart|version|namespace|has_values
declare -a HELM_CHARTS=(
  "cert-manager|https://charts.jetstack.io|cert-manager|v1.19.2|cert-manager|true"
  "karpenter|oci://public.ecr.aws/karpenter/karpenter|karpenter|1.8.2|kube-system|true"
  "aws-load-balancer-controller|https://aws.github.io/eks-charts|aws-load-balancer-controller|1.17.0|aws-load-balancer|true"
  "metrics-server|https://kubernetes-sigs.github.io/metrics-server/|metrics-server|3.13.0|kube-system|true"
  "external-dns|https://kubernetes-sigs.github.io/external-dns/|external-dns|1.19.0|external-dns|true"
  "external-secrets|https://charts.external-secrets.io|external-secrets|1.2.0|external-secrets|true"
  "reloader|https://stakater.github.io/stakater-charts|reloader|2.2.7|reloader|true"
  "kyverno|https://kyverno.github.io/kyverno|kyverno|3.6.1|kyverno|true"
  "velero|https://vmware-tanzu.github.io/helm-charts|velero|11.3.1|velero|true"
  "goldilocks|https://charts.fairwinds.com/stable|goldilocks|10.2.0|goldilocks|true"
  "aws-node-termination-handler|https://aws.github.io/eks-charts|aws-node-termination-handler|0.21.0|kube-system|true"
  "grafana|https://grafana.github.io/helm-charts|k8s-monitoring|3.7.1|monitoring|true"
)

# Default values for templating (simulate Mustache rendering)
# These are placeholders that match the Mustache template variables
DEFAULT_VALUES_YAML=$(cat <<'EOF'
global:
  clusterName: example-eks
  region: us-west-2
  accountId: "123456789012"
  domain: example.com
EOF
)

# Counter for results
TOTAL=0
SUCCESS=0
FAILED=0
SKIPPED=0

# Function to render a single chart
render_chart() {
  local name=$1
  local repository=$2
  local chart=$3
  local version=$4
  local namespace=$5
  local has_values=$6

  TOTAL=$((TOTAL + 1))

  local output_file="${OUTPUT_DIR}/${name}.yaml"
  local values_file="${HELM_VALUES_DIR}/${name}.mustache"
  local temp_values_file="${OUTPUT_DIR}/.${name}-values.yaml"

  echo -n "Rendering ${name} (${version})... "

  # Check if chart is OCI-based
  local is_oci=false
  if [[ "${repository}" == oci://* ]]; then
    is_oci=true
  fi

  # Add helm repo if not OCI
  if [[ "${is_oci}" == false ]]; then
    if ! helm repo list 2>/dev/null | grep -q "${name}-repo"; then
      helm repo add "${name}-repo" "${repository}" --force-update > /dev/null 2>&1 || true
    fi
  fi

  # Prepare values file
  local helm_values_args=""
  if [[ "${has_values}" == "true" && -f "${values_file}" ]]; then
    # Convert Mustache template to YAML by replacing placeholders
    # This creates a minimal valid values file for rendering
    sed -E \
      -e 's/\{\{deployment:id\}\}/example/g' \
      -e 's/\{\{deployment:domain\}\}/example.com/g' \
      -e 's/\{\{deployment:region\}\}/us-west-2/g' \
      -e 's/\{\{deployment:account\}\}/123456789012/g' \
      -e 's/\{\{deployment:organization\}\}/example-org/g' \
      -e 's/\{\{deployment:team:name\}\}/platform/g' \
      -e 's/\{\{deployment:team:alias\}\}/platform/g' \
      -e 's/\{\{deployment:eks:grafana:secret\}\}/grafana-secret/g' \
      -e 's/\{\{[^}]+\}\}//g' \
      "${values_file}" > "${temp_values_file}" 2>/dev/null || true

    if [[ -s "${temp_values_file}" ]]; then
      helm_values_args="-f ${temp_values_file}"
    fi
  fi

  # Render chart
  local chart_ref
  if [[ "${is_oci}" == true ]]; then
    chart_ref="${repository}"
  else
    chart_ref="${name}-repo/${chart}"
  fi

  if helm template "${name}" "${chart_ref}" \
    --version "${version}" \
    --namespace "${namespace}" \
    --create-namespace \
    ${helm_values_args} \
    > "${output_file}" 2>/dev/null; then

    # Check if output is non-empty and valid
    if [[ -s "${output_file}" ]]; then
      echo "✓"
      SUCCESS=$((SUCCESS + 1))
    else
      echo "⚠ (empty output)"
      SKIPPED=$((SKIPPED + 1))
    fi
  else
    echo "✗"
    FAILED=$((FAILED + 1))
    # Create placeholder to indicate failure
    echo "# Failed to render ${name}" > "${output_file}"
  fi

  # Cleanup temp values
  rm -f "${temp_values_file}"
}

# Update helm repos
echo "Updating Helm repositories..."
helm repo update > /dev/null 2>&1 || true
echo ""

# Render each chart
for chart_config in "${HELM_CHARTS[@]}"; do
  IFS='|' read -r name repository chart version namespace has_values <<< "${chart_config}"
  render_chart "${name}" "${repository}" "${chart}" "${version}" "${namespace}" "${has_values}"
done

echo ""
echo "═══════════════════════════════════════════════════════════════"
echo "Rendering Summary"
echo "═══════════════════════════════════════════════════════════════"
echo "Total charts:    ${TOTAL}"
echo "Successful:      ${SUCCESS}"
echo "Failed:          ${FAILED}"
echo "Skipped/Empty:   ${SKIPPED}"
echo ""
echo "Rendered manifests available in: ${OUTPUT_DIR}"

# Exit with error if any failed
if [[ ${FAILED} -gt 0 ]]; then
  echo ""
  echo "⚠ Some charts failed to render. Check the output above."
  exit 1
fi
