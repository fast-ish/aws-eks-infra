#!/usr/bin/env bash
# Run all manifest validations
# This is the main entry point for validation

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Track results
declare -A RESULTS

echo "╔═══════════════════════════════════════════════════════════════╗"
echo "║         Kubernetes Manifest Validation Suite                  ║"
echo "╚═══════════════════════════════════════════════════════════════╝"
echo ""

# Environment (passed as argument or default)
ENVIRONMENT="${1:-production}"
echo "Environment: ${ENVIRONMENT}"
echo ""

# Step 1: Render Helm charts
echo "────────────────────────────────────────────────────────────────"
echo "Step 1/5: Rendering Helm Charts"
echo "────────────────────────────────────────────────────────────────"
if "${SCRIPT_DIR}/render-helm.sh" "${ENVIRONMENT}"; then
  RESULTS["render"]="PASS"
else
  RESULTS["render"]="FAIL"
  echo -e "${RED}✗ Helm rendering failed. Cannot continue.${NC}"
  exit 1
fi
echo ""

# Step 2: Schema validation (kubeconform)
echo "────────────────────────────────────────────────────────────────"
echo "Step 2/5: Schema Validation (kubeconform)"
echo "────────────────────────────────────────────────────────────────"
if "${SCRIPT_DIR}/kubeconform.sh"; then
  RESULTS["schema"]="PASS"
else
  RESULTS["schema"]="FAIL"
fi
echo ""

# Step 3: Best practices (kube-linter)
echo "────────────────────────────────────────────────────────────────"
echo "Step 3/5: Best Practices (kube-linter)"
echo "────────────────────────────────────────────────────────────────"
if "${SCRIPT_DIR}/kube-linter.sh"; then
  RESULTS["lint"]="PASS"
else
  RESULTS["lint"]="FAIL"
fi
echo ""

# Step 4: API deprecation (pluto)
echo "────────────────────────────────────────────────────────────────"
echo "Step 4/5: API Deprecation Check (pluto)"
echo "────────────────────────────────────────────────────────────────"
if "${SCRIPT_DIR}/pluto.sh"; then
  RESULTS["deprecation"]="PASS"
else
  RESULTS["deprecation"]="FAIL"
fi
echo ""

# Step 5: Policy validation (conftest)
echo "────────────────────────────────────────────────────────────────"
echo "Step 5/5: Policy Validation (conftest)"
echo "────────────────────────────────────────────────────────────────"
if "${SCRIPT_DIR}/conftest.sh"; then
  RESULTS["policy"]="PASS"
else
  RESULTS["policy"]="FAIL"
fi
echo ""

# Summary
echo "╔═══════════════════════════════════════════════════════════════╗"
echo "║                    Validation Summary                         ║"
echo "╠═══════════════════════════════════════════════════════════════╣"

FAILED=0
for check in render schema lint deprecation policy; do
  result="${RESULTS[$check]:-SKIP}"
  if [[ "${result}" == "PASS" ]]; then
    printf "║  %-20s %s%-35s${NC}  ║\n" "${check}:" "${GREEN}✓" " PASSED"
  elif [[ "${result}" == "FAIL" ]]; then
    printf "║  %-20s %s%-35s${NC}  ║\n" "${check}:" "${RED}✗" " FAILED"
    FAILED=$((FAILED + 1))
  else
    printf "║  %-20s %s%-35s${NC}  ║\n" "${check}:" "${YELLOW}○" " SKIPPED"
  fi
done

echo "╚═══════════════════════════════════════════════════════════════╝"
echo ""

if [[ ${FAILED} -gt 0 ]]; then
  echo -e "${RED}✗ ${FAILED} validation(s) failed${NC}"
  exit 1
else
  echo -e "${GREEN}✓ All validations passed${NC}"
fi
